package com.example.tp_anthony_menghi.domain.model

/**
 * Modèle composite représentant une ville favorite avec sa météo
 */
data class FavoriteCity(
    val city: City,
    val weather: Weather? = null
)
