package com.wxco.food.data.remote

import com.wxco.food.data.model.CartResponse
import com.wxco.food.data.model.CrudResponse
import com.wxco.food.data.model.FoodsResponse
import retrofit2.Response
import retrofit2.http.*

interface FoodApiService {
    
    @GET("yemekler/tumYemekleriGetir.php")
    suspend fun getAllFoods(): Response<FoodsResponse>
    
    @FormUrlEncoded
    @POST("yemekler/sepeteYemekEkle.php")
    suspend fun addToCart(
        @Field("yemek_adi") yemek_adi: String,
        @Field("yemek_resim_adi") yemek_resim_adi: String,
        @Field("yemek_fiyat") yemek_fiyat: Int,
        @Field("yemek_siparis_adet") yemek_siparis_adet: Int,
        @Field("kullanici_adi") kullanici_adi: String = "tamer_akdeniz"
    ): Response<CrudResponse>
    
    @FormUrlEncoded
    @POST("yemekler/sepettekiYemekleriGetir.php")
    suspend fun getCartFoods(
        @Field("kullanici_adi") kullanici_adi: String = "tamer_akdeniz"
    ): Response<CartResponse>
    
    @FormUrlEncoded
    @POST("yemekler/sepettenYemekSil.php")
    suspend fun removeFromCart(
        @Field("sepet_yemek_id") sepet_yemek_id: Int,
        @Field("kullanici_adi") kullanici_adi: String = "tamer_akdeniz"
    ): Response<CrudResponse>
} 