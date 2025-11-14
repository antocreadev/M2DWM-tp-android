package com.example.tp_anthony_menghi.data.local.dao

import androidx.room.*
import com.example.tp_anthony_menghi.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour gérer les opérations sur les villes favorites
 */
@Dao
interface FavoriteDao {
    
    /**
     * Récupère tous les favoris triés par date d'ajout (plus récents en premier)
     */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    /**
     * Récupère un favori par son ID
     */
    @Query("SELECT * FROM favorites WHERE cityId = :cityId")
    suspend fun getFavoriteById(cityId: Int): FavoriteEntity?
    
    /**
     * Ajoute ou met à jour un favori
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)
    
    /**
     * Supprime un favori
     */
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    
    /**
     * Supprime un favori par son ID
     */
    @Query("DELETE FROM favorites WHERE cityId = :cityId")
    suspend fun deleteFavoriteById(cityId: Int)
}
