package com.example.tp_anthony_menghi.domain.repository

import com.example.tp_anthony_menghi.domain.model.City
import com.example.tp_anthony_menghi.domain.model.FavoriteCity
import com.example.tp_anthony_menghi.domain.model.Weather
import com.example.tp_anthony_menghi.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface de repository - Contrat pour la gestion des données météo
 * Implémentation dans la couche data
 */
interface WeatherRepository {
    
    /**
     * Recherche des villes par nom via l'API Geocoding
     */
    suspend fun searchCities(query: String): Resource<List<City>>
    
    /**
     * Recherche inversée : trouve les villes proches de coordonnées données
     */
    suspend fun getCitiesNearLocation(latitude: Double, longitude: Double): Resource<List<City>>
    
    /**
     * Récupère les données météo pour une ville
     * Gère le cache et le mode hors ligne
     */
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        cityId: Int,
        cityName: String
    ): Resource<Weather>
    
    /**
     * Récupère la liste des villes favorites avec leur météo
     */
    fun getFavorites(): Flow<List<FavoriteCity>>
    
    /**
     * Ajoute une ville aux favoris
     */
    suspend fun addFavorite(city: City)
    
    /**
     * Retire une ville des favoris
     */
    suspend fun removeFavorite(cityId: Int)
    
    /**
     * Vérifie si une ville est en favoris
     */
    suspend fun isFavorite(cityId: Int): Boolean
}
