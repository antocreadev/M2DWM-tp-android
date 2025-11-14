package com.example.tp_anthony_menghi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tp_anthony_menghi.domain.model.HourlyWeather
import com.example.tp_anthony_menghi.domain.model.Weather
import com.example.tp_anthony_menghi.domain.model.WeatherCondition
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Entité Room pour stocker le cache météo
 */
@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val cityId: Int,
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double,
    val apparentTemperature: Double,
    val weatherCondition: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val windSpeed: Double,
    val humidity: Int,
    val precipitation: Double,
    val hourlyDataJson: String, // Données horaires sérialisées en JSON
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Extensions pour convertir Entity <-> Domain Model
 */
fun WeatherCacheEntity.toWeather(): Weather {
    val hourlyForecast = try {
        Json.decodeFromString<List<HourlyWeather>>(hourlyDataJson)
    } catch (e: Exception) {
        emptyList()
    }
    
    return Weather(
        cityId = cityId,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        currentTemperature = currentTemperature,
        apparentTemperature = apparentTemperature,
        weatherCondition = WeatherCondition.valueOf(weatherCondition),
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        windSpeed = windSpeed,
        humidity = humidity,
        precipitation = precipitation,
        hourlyForecast = hourlyForecast,
        timestamp = cachedAt
    )
}

fun Weather.toWeatherCacheEntity(): WeatherCacheEntity {
    return WeatherCacheEntity(
        cityId = cityId,
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        currentTemperature = currentTemperature,
        apparentTemperature = apparentTemperature,
        weatherCondition = weatherCondition.name,
        minTemperature = minTemperature,
        maxTemperature = maxTemperature,
        windSpeed = windSpeed,
        humidity = humidity,
        precipitation = precipitation,
        hourlyDataJson = Json.encodeToString(hourlyForecast),
        cachedAt = timestamp
    )
}
