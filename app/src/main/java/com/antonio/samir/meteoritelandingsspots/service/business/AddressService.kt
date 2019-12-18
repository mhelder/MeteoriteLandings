package com.antonio.samir.meteoritelandingsspots.service.business

import android.util.Log
import androidx.annotation.StringDef
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.AddressService.Status.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.service.business.AddressService.Status.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.DefaultDispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.DispatcherProvider
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import java.util.*


class AddressService(
        private val meteoriteRepository: MeteoriteRepositoryInterface,
        private val geoLocationUtil: GeoLocationUtilInterface,
        private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : AddressServiceInterface {

    val TAG = AddressService::class.java.simpleName

    override val status = MutableLiveData<String>()

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(DONE, LOADING)
    annotation class Status {
        companion object {
            const val DONE = "DONE"
            const val LOADING = "LOADING"
        }
    }

    override fun recoveryAddress() {

        GlobalScope.launch(dispatchers.default()) {
            if (status.value == null || status.value === DONE) {
                var meteorites = meteoriteRepository.meteoritesWithOutAddress()

                Log.i(TAG, "recoveryAddress $LOADING")

                status.postValue(LOADING)

                while (meteorites.isNotEmpty()) {
                    try {
                        meteorites.onEach { meteorite ->
                            recoverAddress(meteorite)
                        }
                        val meteoritesWithoutAddressCount: Int = meteoriteRepository.getMeteoritesWithoutAddressCount()
                        Log.i(TAG, "recoveryAddress ${meteorites.size} $meteoritesWithoutAddressCount")

                    } catch (e: Exception) {
                        Log.e(TAG, "Fail to retrieve address", e)
                    }
                    meteorites = meteoriteRepository.meteoritesWithOutAddress()
                }

                status.postValue(DONE)

            }

        }

    }

    override suspend fun recoverAddress(meteorite: Meteorite) = withContext(dispatchers.default()) {
        val recLat = meteorite.reclat
        val recLong = meteorite.reclong

        var metAddress = " "
        if (recLat != null && recLong != null) {
            val address = getAddress(recLat.toDouble(), recLong.toDouble())
            metAddress = address ?: " "
        }
        meteorite.address = metAddress

        meteoriteRepository.update(meteorite)
    }


    private fun getAddress(recLat: Double, recLong: Double): String? {
        var addressString: String? = null
        val address = geoLocationUtil.getAddress(recLat, recLong)
        if (address != null) {
            val finalAddress = ArrayList<String>()
            val city = address.locality
            if (StringUtils.isNoneEmpty(city)) {
                finalAddress.add(city)
            }

            val state = address.adminArea
            if (StringUtils.isNoneEmpty(state)) {
                finalAddress.add(state)
            }

            val countryName = address.countryName
            if (StringUtils.isNoneEmpty(countryName)) {
                finalAddress.add(countryName)
            }

            addressString = StringUtils.join(finalAddress, ", ")

        }

        return addressString
    }
}
