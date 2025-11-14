package com.example.tp_anthony_menghi.data.remote.dto

import com.example.tp_anthony_menghi.domain.model.HourlyWeather
import com.example.tp_anthony_menghi.domain.model.Weather
import com.example.tp_anthony_menghi.domain.model.WeatherCondition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO pour la réponse de l'API Weather
 */
@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyData
)

/**
 * DTO pour les données horaires
 */
@Serializable
data class HourlyData(
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature: List<Double?>,
    @SerialName("relative_humidity_2m")
    val humidity: List<Int?>,
    @SerialName("apparent_temperature")
    val apparentTemperature: List<Double?>,
    val rain: List<Double?>,
    @SerialName("wind_speed_10m")
    val windSpeed: List<Double?>
)

/**
 * Extensions pour convertir DTO -> Domain Model
 */
fun WeatherResponse.toWeather(cityId: Int, cityName: String): Weather {
    val hourly = this.hourly
    
    // Prendre les valeurs actuelles (première heure non-null)
    val currentTemp = hourly.temperature.firstOrNull { it != null } ?: 0.0
    val currentHumidity = hourly.humidity.firstOrNull { it != null } ?: 0
    val currentApparentTemp = hourly.apparentTemperature.firstOrNull { it != null } ?: currentTemp
    val currentRain = hourly.rain.firstOrNull { it != null } ?: 0.0
    val currentWindSpeed = hourly.windSpeed.firstOrNull { it != null } ?: 0.0
    
    // Calculer min/max sur les prochaines 24h (filtrer les null)
    val next24h = hourly.temperature.take(24).filterNotNull()
    val minTemp = next24h.minOrNull() ?: currentTemp
    val maxTemp = next24h.maxOrNull() ?: currentTemp
    
    return Weather(
        cityId = cityId,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        currentTemperature = currentTemp,
        apparentTemperature = currentApparentTemp,
        weatherCondition = determineWeatherCondition(currentRain, currentWindSpeed),
        minTemperature = minTemp,
        maxTemperature = maxTemp,
        windSpeed = currentWindSpeed,
        humidity = currentHumidity,
        precipitation = currentRain,
        hourlyForecast = mapHourlyData(hourly)
    )
}

/**
 * Détermine la condition météo basée sur les précipitations et le vent
 */
private fun determineWeatherCondition(rain: Double, windSpeed: Double): WeatherCondition {
    return when {
        rain > 10.0 && windSpeed > 50.0 -> WeatherCondition.STORMY
        rain > 5.0 -> WeatherCondition.RAINY
        rain > 0.1 -> WeatherCondition.CLOUDY
        windSpeed > 30.0 -> WeatherCondition.CLOUDY
        else -> WeatherCondition.SUNNY
    }
}

/**
 * Mappe les données horaires (limité à 24h)
 */
private fun mapHourlyData(hourly: HourlyData): List<HourlyWeather> {
    val size = minOf(24, hourly.time.size)
    return (0 until size).mapNotNull { index ->
        // Ne créer un HourlyWeather que si toutes les données sont disponibles
        val temp = hourly.temperature.getOrNull(index)
        val humidity = hourly.humidity.getOrNull(index)
        val windSpeed = hourly.windSpeed.getOrNull(index)
        val rain = hourly.rain.getOrNull(index)
        
        if (temp != null && humidity != null && windSpeed != null && rain != null) {
            HourlyWeather(
                time = hourly.time[index],
                temperature = temp,
                humidity = humidity,
                windSpeed = windSpeed,
                precipitation = rain
            )
        } else {
            null
        }
    }
}
