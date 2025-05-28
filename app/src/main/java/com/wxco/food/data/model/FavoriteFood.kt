package com.wxco.food.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite_foods")
data class FavoriteFood(
    @PrimaryKey
    val yemekId: Int,
    val yemekAdi: String,
    val yemekResimAdi: String,
    val yemekFiyat: Int,
    val addedDate: Long = System.currentTimeMillis()
) : Parcelable {
    
    val imageUrl: String
        get() = "http://kasimadalan.pe.hu/yemekler/resimler/$yemekResimAdi"
    
    val formattedPrice: String
        get() = "₺$yemekFiyat"
    
    fun toFood(): Food {
        return Food(
            yemekId = yemekId,
            yemekAdi = yemekAdi,
            yemekResimAdi = yemekResimAdi,
            yemekFiyat = yemekFiyat
        )
    }
} 