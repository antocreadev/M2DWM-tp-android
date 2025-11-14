package com.example.tp_anthony_menghi.di

import android.content.Context
import androidx.room.Room
import com.example.tp_anthony_menghi.data.local.dao.FavoriteDao
import com.example.tp_anthony_menghi.data.local.dao.WeatherCacheDao
import com.example.tp_anthony_menghi.data.local.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour la base de données Room
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Fournit l'instance de la base de données Weather
     */
    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * Fournit le DAO des favoris
     */
    @Provides
    fun provideFavoriteDao(database: WeatherDatabase): FavoriteDao {
        return database.favoriteDao()
    }
    
    /**
     * Fournit le DAO du cache météo
     */
    @Provides
    fun provideWeatherCacheDao(database: WeatherDatabase): WeatherCacheDao {
        return database.weatherCacheDao()
    }
}
