package com.example.tp_anthony_menghi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tp_anthony_menghi.data.local.dao.FavoriteDao
import com.example.tp_anthony_menghi.data.local.dao.WeatherCacheDao
import com.example.tp_anthony_menghi.data.local.entity.FavoriteEntity
import com.example.tp_anthony_menghi.data.local.entity.WeatherCacheEntity

/**
 * Base de données Room pour l'application météo
 */
@Database(
    entities = [FavoriteEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun weatherCacheDao(): WeatherCacheDao
}
