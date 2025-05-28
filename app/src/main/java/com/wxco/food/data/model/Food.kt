package com.wxco.food.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    @SerializedName("yemek_id")
    val yemekId: Int,
    @SerializedName("yemek_adi")
    val yemekAdi: String,
    @SerializedName("yemek_resim_adi")
    val yemekResimAdi: String,
    @SerializedName("yemek_fiyat")
    val yemekFiyat: Int
) : Parcelable {
    
    val imageUrl: String
        get() = "http://kasimadalan.pe.hu/yemekler/resimler/$yemekResimAdi"
    
    val formattedPrice: String
        get() = "₺$yemekFiyat"
} 