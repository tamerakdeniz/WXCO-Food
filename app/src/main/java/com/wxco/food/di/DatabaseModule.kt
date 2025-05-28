package com.wxco.food.di

import android.content.Context
import androidx.room.Room
import com.wxco.food.data.local.CartFoodDao
import com.wxco.food.data.local.FavoriteFoodDao
import com.wxco.food.data.local.FoodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FoodDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FoodDatabase::class.java,
            "wxco_food_database"
        ).build()
    }
    
    @Provides
    fun provideFavoriteFoodDao(database: FoodDatabase): FavoriteFoodDao {
        return database.favoriteFoodDao()
    }
    
    @Provides
    fun provideCartFoodDao(database: FoodDatabase): CartFoodDao {
        return database.cartFoodDao()
    }
} 