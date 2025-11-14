package com.example.tp_anthony_menghi.data.local.dao

import androidx.room.*
import com.example.tp_anthony_menghi.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour gérer le cache des données météo
 */
@Dao
interface WeatherCacheDao {
    
    /**
     * Récupère le cache météo pour une ville
     */
    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId")
    suspend fun getWeatherCache(cityId: Int): WeatherCacheEntity?
    
    /**
     * Insère ou met à jour le cache météo
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(cache: WeatherCacheEntity)
    
    /**
     * Supprime les caches plus vieux qu'un timestamp donné
     */
    @Query("DELETE FROM weather_cache WHERE cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
    
    /**
     * Récupère tous les caches météo
     */
    @Query("SELECT * FROM weather_cache")
    fun getAllCachedWeather(): Flow<List<WeatherCacheEntity>>
}
