package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class NasaService(val service: NasaServerEndPoint) : NasaServiceInterface {

    override suspend fun getMeteorites(): List<Meteorite>? = withContext(Dispatchers.IO) {

        val meteorites: List<Meteorite>?
        try {
            meteorites = service.publicMeteorites()
        } catch (e: IOException) {
            throw MeteoriteServerException(e)
        }

        return@withContext meteorites
    }

}
