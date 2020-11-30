package com.antonio.samir.meteoritelandingsspots.features.detail

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.meteorite_detail.*
import kotlinx.android.synthetic.main.meteorite_detail_grid.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.apache.commons.lang3.StringUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class MeteoriteDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var meteorite: MeteoriteView

    private val viewModel: MeteoriteDetailViewModel by viewModel()

    private var meteoriteId: Meteorite? = null

    val TAG = MeteoriteDetailFragment::class.java.simpleName

    companion object {

        const val METEORITE = "METEORITE"
        const val OPENED_INSIDE_NAVIGATOR = "OPENED_INSIDE_NAVIGATOR"

        fun newInstance(meteorite: Meteorite): MeteoriteDetailFragment {
            val fragment = MeteoriteDetailFragment()
            val args = Bundle()
            args.putParcelable(METEORITE, meteorite)
            args.putBoolean(OPENED_INSIDE_NAVIGATOR, false)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            //Recovering the meteorite to work on it
            meteoriteId = arguments?.getParcelable(METEORITE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_meteorite_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape && arguments?.getBoolean(OPENED_INSIDE_NAVIGATOR) == false) {
            findNavController().popBackStack()
        }

        observeMeteorite()
    }

    private fun observeMeteorite() {

        viewModel.meteorite.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Result.Success -> {
                    result.data.let { meteorite ->
                        if (meteorite == this.meteorite) {
                            if (meteorite.address != this.meteorite.address) {
                                this.meteorite = meteorite
                                setLocationText(meteorite)
                            }
                        } else {
                            setMeteorite(meteorite)
                            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
                        }
                    }

                }
            }
        })

        meteoriteId?.let { setCurrentMeteorite(it) }
    }

    fun setCurrentMeteorite(meteorite: Meteorite) {
        viewModel.loadMeteorite(meteorite)
    }

    override fun onMapReady(map: GoogleMap) {

        meteorite?.let {
            val lat = it.reclat
            val log = it.reclong

            Log.d(TAG, "Show meteorite: ${it.id} $lat $log")

            setupMap(it.name, lat, log, map)
        }
    }

    private fun setupMap(meteoriteName: String?, lat: Double, log: Double, map: GoogleMap) {
        map.clear()

        val latLng = LatLng(lat, log)

        map.addMarker(MarkerOptions().position(
                latLng).title(meteoriteName))
        Log.d(TAG, "Marker added: $lat $log")

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }


    private fun setMeteorite(meteorite: MeteoriteView) {

        setLocationText(meteorite)

        val meteoriteName = meteorite.name
        setText(null, this.title, meteoriteName)

        setText(year_label, this.year, meteorite.yearString)

        setText(recclass_label, this.recclass, meteorite.recclass)

        setText(mass_label, this.mass, meteorite.mass)

        this.meteorite = meteorite

    }

    private fun setLocationText(meteorite: MeteoriteView) {
        val address = meteorite.address
        setText(null, locationTxt, address)
        locationTxt?.visibility = if (meteorite.hasAddress) {
            View.VISIBLE
        } else {
            viewModel.requestAddressUpdate(meteorite)
            View.GONE
        }
    }

    private fun setText(textFieldLabel: TextView?, textField: TextView?, text: String?) {
        if (StringUtils.isEmpty(text)) {
            textFieldLabel?.visibility = View.GONE
            textField?.visibility = View.GONE
        } else {
            textField?.text = text
            textField?.contentDescription = text
        }
    }

}

