package com.wxco.food.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cart_foods")
data class CartFood(
    @PrimaryKey
    @SerializedName("sepet_yemek_id")
    val sepetYemekId: Int,
    @SerializedName("yemek_adi")
    val yemekAdi: String,
    @SerializedName("yemek_resim_adi")
    val yemekResimAdi: String,
    @SerializedName("yemek_fiyat")
    val yemekFiyat: Int,
    @SerializedName("yemek_siparis_adet")
    val yemekSiparisAdet: Int,
    @SerializedName("kullanici_adi")
    val kullaniciAdi: String = "tamer_akdeniz"
) : Parcelable {
    
    val imageUrl: String
        get() = "http://kasimadalan.pe.hu/yemekler/resimler/$yemekResimAdi"
    
    val totalPrice: Int
        get() = yemekFiyat * yemekSiparisAdet
    
    val formattedTotalPrice: String
        get() = "₺$totalPrice"
} 