package com.example.tp_anthony_menghi.data.remote.dto

import com.example.tp_anthony_menghi.domain.model.City
import kotlinx.serialization.Serializable

/**
 * DTO pour la réponse de l'API Geocoding
 */
@Serializable
data class GeocodingResponse(
    val results: List<CityDto>? = null
)

/**
 * DTO pour les données d'une ville
 */
@Serializable
data class CityDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String? = null
)

/**
 * Extension pour convertir DTO -> Domain Model
 */
fun CityDto.toCity(): City {
    return City(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        admin1 = admin1
    )
}
