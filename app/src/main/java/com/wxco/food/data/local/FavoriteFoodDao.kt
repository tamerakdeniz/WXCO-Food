package com.wxco.food.data.local

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.room.*
import com.wxco.food.data.model.FavoriteFood

@Dao
interface FavoriteFoodDao {
    
    @Query("SELECT * FROM favorite_foods ORDER BY addedDate DESC")
    fun getAllFavorites(): LiveData<List<FavoriteFood>>
    
    @Query("SELECT * FROM favorite_foods WHERE yemekId = :foodId")
    suspend fun getFavoriteById(foodId: Int): FavoriteFood?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favoriteFood: FavoriteFood)
    
    @Delete
    suspend fun removeFromFavorites(favoriteFood: FavoriteFood)
    
    @Query("DELETE FROM favorite_foods WHERE yemekId = :foodId")
    suspend fun removeFromFavoritesById(foodId: Int)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_foods WHERE yemekId = :foodId)")
    suspend fun isFavorite(foodId: Int): Boolean
    
    @Query("DELETE FROM favorite_foods")
    suspend fun clearAllFavorites()
} 