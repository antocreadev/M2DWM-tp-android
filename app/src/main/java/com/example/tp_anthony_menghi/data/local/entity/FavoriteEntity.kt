package com.example.tp_anthony_menghi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tp_anthony_menghi.domain.model.City

/**
 * Entité Room pour stocker les villes favorites
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val cityId: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String?,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Extensions pour convertir Entity <-> Domain Model
 */
fun FavoriteEntity.toCity(): City {
    return City(
        id = cityId,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        admin1 = admin1
    )
}

fun City.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        cityId = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        admin1 = admin1
    )
}
