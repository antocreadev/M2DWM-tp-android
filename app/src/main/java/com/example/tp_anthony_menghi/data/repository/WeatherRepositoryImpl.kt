package com.example.tp_anthony_menghi.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.tp_anthony_menghi.data.local.dao.FavoriteDao
import com.example.tp_anthony_menghi.data.local.dao.WeatherCacheDao
import com.example.tp_anthony_menghi.data.local.entity.toCity
import com.example.tp_anthony_menghi.data.local.entity.toFavoriteEntity
import com.example.tp_anthony_menghi.data.local.entity.toWeather
import com.example.tp_anthony_menghi.data.local.entity.toWeatherCacheEntity
import com.example.tp_anthony_menghi.data.remote.api.GeocodingApi
import com.example.tp_anthony_menghi.data.remote.api.WeatherApi
import com.example.tp_anthony_menghi.data.remote.dto.toCity
import com.example.tp_anthony_menghi.data.remote.dto.toWeather
import com.example.tp_anthony_menghi.domain.model.City
import com.example.tp_anthony_menghi.domain.model.FavoriteCity
import com.example.tp_anthony_menghi.domain.model.Weather
import com.example.tp_anthony_menghi.domain.repository.WeatherRepository
import com.example.tp_anthony_menghi.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implémentation du repository météo
 * Gère la logique de cache et les appels réseau
 */
@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val favoriteDao: FavoriteDao,
    private val weatherCacheDao: WeatherCacheDao,
    @ApplicationContext private val context: Context
) : WeatherRepository {
    
    companion object {
        private const val CACHE_VALIDITY_MS = 30 * 60 * 1000L // 30 minutes
    }
    
    /**
     * Recherche des villes via l'API Geocoding
     */
    override suspend fun searchCities(query: String): Resource<List<City>> {
        return try {
            if (!isNetworkAvailable()) {
                return Resource.Error("Pas de connexion internet")
            }
            
            val response = geocodingApi.searchCity(query)
            val cities = response.results?.map { it.toCity() } ?: emptyList()
            
            if (cities.isEmpty()) {
                Resource.Error("Aucune ville trouvée pour '$query'")
            } else {
                Resource.Success(cities)
            }
        } catch (e: HttpException) {
            Resource.Error(handleHttpException(e))
        } catch (e: IOException) {
            Resource.Error("Erreur réseau. Vérifiez votre connexion.")
        } catch (e: Exception) {
            Resource.Error("Une erreur est survenue: ${e.localizedMessage}")
        }
    }
    
    /**
     * Récupère la météo avec stratégie de cache
     * 1. Vérifie le cache local
     * 2. Si cache valide (< 30 min), retourne le cache
     * 3. Sinon, fetch depuis l'API
     * 4. En cas d'erreur, retourne le cache expiré si disponible
     */
    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        cityId: Int,
        cityName: String
    ): Resource<Weather> {
        return try {
            // 1. Vérifier le cache
            val cached = weatherCacheDao.getWeatherCache(cityId)
            val cacheValid = cached != null && isCacheValid(cached.cachedAt)
            
            // 2. Si cache valide, retourner depuis le cache
            if (cacheValid && cached != null) {
                return Resource.Success(cached.toWeather())
            }
            
            // 3. Vérifier la connectivité
            if (!isNetworkAvailable()) {
                // Mode hors ligne : retourner cache même expiré
                return if (cached != null) {
                    Resource.Success(cached.toWeather())
                } else {
                    Resource.Error("Pas de connexion et aucune donnée en cache")
                }
            }
            
            // 4. Fetch depuis l'API
            val response = weatherApi.getWeather(latitude, longitude)
            val weather = response.toWeather(cityId, cityName)
            
            // 5. Mettre en cache
            weatherCacheDao.insertWeatherCache(weather.toWeatherCacheEntity())
            
            Resource.Success(weather)
            
        } catch (e: HttpException) {
            // En cas d'erreur, retourner le cache si disponible
            val cached = weatherCacheDao.getWeatherCache(cityId)
            if (cached != null) {
                Resource.Success(cached.toWeather())
            } else {
                Resource.Error(handleHttpException(e))
            }
        } catch (e: IOException) {
            // Erreur réseau : retourner cache si disponible
            val cached = weatherCacheDao.getWeatherCache(cityId)
            if (cached != null) {
                Resource.Success(cached.toWeather())
            } else {
                Resource.Error("Erreur réseau. Vérifiez votre connexion.")
            }
        } catch (e: Exception) {
            val cached = weatherCacheDao.getWeatherCache(cityId)
            if (cached != null) {
                Resource.Success(cached.toWeather())
            } else {
                Resource.Error("Une erreur est survenue: ${e.localizedMessage}")
            }
        }
    }
    
    /**
     * Récupère les favoris avec leur météo
     */
    override fun getFavorites(): Flow<List<FavoriteCity>> {
        return favoriteDao.getAllFavorites().map { favorites ->
            favorites.map { favorite ->
                val city = favorite.toCity()
                val weather = weatherCacheDao.getWeatherCache(favorite.cityId)?.toWeather()
                FavoriteCity(city, weather)
            }
        }
    }
    
    /**
     * Ajoute une ville aux favoris
     */
    override suspend fun addFavorite(city: City) {
        favoriteDao.insertFavorite(city.toFavoriteEntity())
    }
    
    /**
     * Supprime une ville des favoris
     */
    override suspend fun removeFavorite(cityId: Int) {
        favoriteDao.deleteFavoriteById(cityId)
    }
    
    /**
     * Vérifie si une ville est en favoris
     */
    override suspend fun isFavorite(cityId: Int): Boolean {
        return favoriteDao.getFavoriteById(cityId) != null
    }
    
    /**
     * Vérifie si le cache est encore valide (< 30 minutes)
     */
    private fun isCacheValid(cachedAt: Long): Boolean {
        return (System.currentTimeMillis() - cachedAt) < CACHE_VALIDITY_MS
    }
    
    /**
     * Vérifie la disponibilité du réseau
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    /**
     * Gère les exceptions HTTP
     */
    private fun handleHttpException(e: HttpException): String {
        return when (e.code()) {
            429 -> "Trop de requêtes. Réessayez plus tard."
            500, 502, 503 -> "Erreur serveur. Réessayez."
            else -> "Erreur réseau (${e.code()})"
        }
    }
}
