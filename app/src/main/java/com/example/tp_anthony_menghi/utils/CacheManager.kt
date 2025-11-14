package com.example.tp_anthony_menghi.utils

import com.example.tp_anthony_menghi.data.local.dao.WeatherCacheDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestionnaire de cache pour optimiser le stockage
 */
@Singleton
class CacheManager @Inject constructor(
    private val weatherCacheDao: WeatherCacheDao
) {
    companion object {
        /**
         * Durée de validité du cache : 30 minutes
         */
        const val CACHE_VALIDITY_MS = 30 * 60 * 1000L
        
        /**
         * Age maximum du cache avant suppression : 7 jours
         */
        const val MAX_CACHE_AGE_MS = 7 * 24 * 60 * 60 * 1000L
    }
    
    /**
     * Vérifie si un cache est encore valide
     */
    fun isCacheValid(cachedAt: Long): Boolean {
        return (System.currentTimeMillis() - cachedAt) < CACHE_VALIDITY_MS
    }
    
    /**
     * Nettoie les vieux caches (> 7 jours)
     */
    suspend fun cleanOldCache() {
        withContext(Dispatchers.IO) {
            val threshold = System.currentTimeMillis() - MAX_CACHE_AGE_MS
            weatherCacheDao.deleteOldCache(threshold)
        }
    }
    
    /**
     * Retourne le temps restant avant expiration du cache (en ms)
     */
    fun getTimeUntilExpiration(cachedAt: Long): Long {
        val elapsed = System.currentTimeMillis() - cachedAt
        return (CACHE_VALIDITY_MS - elapsed).coerceAtLeast(0)
    }
    
    /**
     * Retourne true si le cache approche de l'expiration (< 5 minutes)
     */
    fun isNearExpiration(cachedAt: Long): Boolean {
        val timeLeft = getTimeUntilExpiration(cachedAt)
        return timeLeft < 5 * 60 * 1000L // 5 minutes
    }
}
