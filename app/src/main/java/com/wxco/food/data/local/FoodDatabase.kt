package com.wxco.food.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.wxco.food.data.model.CartFood
import com.wxco.food.data.model.FavoriteFood

@Database(
    entities = [FavoriteFood::class, CartFood::class],
    version = 1,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {
    
    abstract fun favoriteFoodDao(): FavoriteFoodDao
    abstract fun cartFoodDao(): CartFoodDao
    
    companion object {
        @Volatile
        private var INSTANCE: FoodDatabase? = null
        
        fun getDatabase(context: Context): FoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "wxco_food_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 