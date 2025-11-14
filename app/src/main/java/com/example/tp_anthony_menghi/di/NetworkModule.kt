package com.example.tp_anthony_menghi.di

import com.example.tp_anthony_menghi.data.remote.api.GeocodingApi
import com.example.tp_anthony_menghi.data.remote.api.WeatherApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Module Hilt pour la configuration du réseau
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Fournit un client OkHttp configuré avec timeout et logging
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    /**
     * Fournit un instance Json pour la sérialisation
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }
    
    /**
     * Fournit Retrofit pour l'API Geocoding
     */
    @Provides
    @Singleton
    @Named("geocoding")
    fun provideGeocodingRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    /**
     * Fournit Retrofit pour l'API Weather
     */
    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    /**
     * Fournit l'API Geocoding
     */
    @Provides
    @Singleton
    fun provideGeocodingApi(@Named("geocoding") retrofit: Retrofit): GeocodingApi {
        return retrofit.create(GeocodingApi::class.java)
    }
    
    /**
     * Fournit l'API Weather
     */
    @Provides
    @Singleton
    fun provideWeatherApi(@Named("weather") retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
}
