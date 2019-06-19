package com.antonio.samir.meteoritelandingsspots.service.repository.remote

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import retrofit2.http.GET

/**
 * Interface needed by retrofit library in order to request from the server
 */
interface NasaServerEndPoint {

    companion object {

        val URL = "https://data.nasa.gov"
    }

    @GET("resource/y77d-th95.json")
    suspend fun publicMeteorites(): List<Meteorite>


}