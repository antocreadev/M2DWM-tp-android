package com.example.tp_anthony_menghi.data.remote.api

import com.example.tp_anthony_menghi.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Interface pour le service météo Open-Meteo
 * Utilise le modèle meteofrance_seamless
 */
interface WeatherApi {
    
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,apparent_temperature,rain,wind_speed_10m",
        @Query("models") models: String = "meteofrance_seamless",
        @Query("timezone") timezone: String = "Europe/Paris"
    ): WeatherResponse
}
