package com.example.tp_anthony_menghi.domain.model

import kotlinx.serialization.Serializable

/**
 * Modèle du domaine représentant les données météo d'une ville
 */
@Serializable
data class Weather(
    val cityId: Int,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val apparentTemperature: Double,
    val weatherCondition: WeatherCondition,
    val minTemperature: Double,
    val maxTemperature: Double,
    val windSpeed: Double,
    val humidity: Int,
    val precipitation: Double,
    val hourlyForecast: List<HourlyWeather>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Données météo par heure
 */
@Serializable
data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: Double
)

/**
 * Conditions météorologiques
 */
@Serializable
enum class WeatherCondition {
    SUNNY,    // Ensoleillé
    CLOUDY,   // Nuageux
    RAINY,    // Pluvieux
    STORMY,   // Orageux
    SNOWY,    // Neigeux
    FOGGY     // Brumeux
}
