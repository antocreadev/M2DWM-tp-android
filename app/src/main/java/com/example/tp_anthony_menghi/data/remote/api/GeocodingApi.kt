package com.example.tp_anthony_menghi.data.remote.api

import com.example.tp_anthony_menghi.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Interface pour le service de Geocoding Open-Meteo
 * Permet de rechercher des villes par nom
 */
interface GeocodingApi {
    
    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") cityName: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "fr",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
