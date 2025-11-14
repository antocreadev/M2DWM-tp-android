package com.example.tp_anthony_menghi.domain.model

/**
 * Modèle du domaine représentant une ville
 */
data class City(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String? = null // région/état
)
