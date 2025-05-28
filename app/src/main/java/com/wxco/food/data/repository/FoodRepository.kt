package com.wxco.food.data.repository

import androidx.lifecycle.LiveData
import com.wxco.food.data.local.CartFoodDao
import com.wxco.food.data.local.FavoriteFoodDao
import com.wxco.food.data.model.*
import com.wxco.food.data.remote.FoodApiService
import com.wxco.food.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val apiService: FoodApiService,
    private val favoriteFoodDao: FavoriteFoodDao,
    private val cartFoodDao: CartFoodDao
) {

    suspend fun getAllFoods(): Resource<List<Food>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllFoods()
            if (response.isSuccessful) {
                response.body()?.let { foodsResponse ->
                    if (foodsResponse.success == 1 && !foodsResponse.yemekler.isNullOrEmpty()) {
                        Resource.Success(foodsResponse.yemekler)
                    } else {
                        Resource.Error("No foods found")
                    }
                } ?: Resource.Error("Response body is null")
            } else {
                Resource.Error("API Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }
    
    suspend fun addToCart(food: Food, quantity: Int): Resource<String> = withContext(Dispatchers.IO) {
        try {
            // First get current cart items
            when (val cartResult = getCartFoods()) {
                is Resource.Success -> {
                    val currentCart = cartResult.data ?: emptyList()
                    val existingItem = currentCart.find { it.yemekAdi == food.yemekAdi }
                    
                    if (existingItem != null) {
                        // Remove old item first
                        val removeResult = removeFromCart(existingItem.sepetYemekId)
                        if (removeResult !is Resource.Success) {
                            return@withContext removeResult
                        }
                    }
                    
                    // Add item with specified quantity (don't add to existing quantity)
                    val response = apiService.addToCart(
                        yemek_adi = food.yemekAdi,
                        yemek_resim_adi = food.yemekResimAdi,
                        yemek_fiyat = food.yemekFiyat,
                        yemek_siparis_adet = quantity
                    )
                    
                    if (response.isSuccessful) {
                        response.body()?.let { crudResponse ->
                            if (crudResponse.success == 1) {
                                Resource.Success(if (existingItem != null) "Updated quantity in cart" else "Successfully added to cart")
                            } else {
                                Resource.Error(crudResponse.message)
                            }
                        } ?: Resource.Error("Response body is null")
                    } else {
                        Resource.Error("API Error: ${response.message()}")
                    }
                }
                else -> Resource.Error("Failed to check cart status")
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }
    
    suspend fun getCartFoods(): Resource<List<CartFood>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCartFoods()
            if (response.isSuccessful) {
                response.body()?.let { cartResponse ->
                    if (cartResponse.success == 1) {
                        Resource.Success(cartResponse.sepet_yemekler ?: emptyList())
                    } else {
                        Resource.Success(emptyList())
                    }
                } ?: Resource.Success(emptyList())
            } else {
                Resource.Error("API Error: ${response.message()}")
            }
        } catch (e: com.google.gson.JsonSyntaxException) {
            Resource.Success(emptyList())
        } catch (e: java.io.EOFException) {
            Resource.Success(emptyList())
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }
    
    suspend fun removeFromCart(cartFoodId: Int): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeFromCart(cartFoodId)
            if (response.isSuccessful) {
                response.body()?.let { crudResponse ->
                    if (crudResponse.success == 1) {
                        Resource.Success("Successfully removed from cart")
                    } else {
                        Resource.Error(crudResponse.message)
                    }
                } ?: Resource.Error("Response body is null")
            } else {
                Resource.Error("API Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }

    fun getAllFavorites(): LiveData<List<FavoriteFood>> = favoriteFoodDao.getAllFavorites()
    
    suspend fun addToFavorites(food: Food) = withContext(Dispatchers.IO) {
        val favoriteFood = FavoriteFood(
            yemekId = food.yemekId,
            yemekAdi = food.yemekAdi,
            yemekResimAdi = food.yemekResimAdi,
            yemekFiyat = food.yemekFiyat
        )
        favoriteFoodDao.addToFavorites(favoriteFood)
    }
    
    suspend fun removeFromFavorites(foodId: Int) = withContext(Dispatchers.IO) {
        favoriteFoodDao.removeFromFavoritesById(foodId)
    }
    
    suspend fun isFavorite(foodId: Int): Boolean = withContext(Dispatchers.IO) {
        favoriteFoodDao.isFavorite(foodId)
    }

    fun getLocalCartFoods(): LiveData<List<CartFood>> = cartFoodDao.getAllCartFoods()
    
    suspend fun getCartTotalPrice(): Int = withContext(Dispatchers.IO) {
        cartFoodDao.getTotalPrice() ?: 0
    }
    
    suspend fun getCartTotalQuantity(): Int = withContext(Dispatchers.IO) {
        cartFoodDao.getTotalQuantity() ?: 0
    }

    // For adding from home or favorites screen
    suspend fun addToCartFromList(food: Food): Resource<String> = withContext(Dispatchers.IO) {
        try {
            when (val cartResult = getCartFoods()) {
                is Resource.Success -> {
                    val currentCart = cartResult.data ?: emptyList()
                    val existingItem = currentCart.find { it.yemekAdi == food.yemekAdi }
                    
                    if (existingItem != null) {
                        // Item already exists in cart
                        Resource.Error("This item is already in your cart!")
                    } else {
                        // Add new item with quantity 1
                        val response = apiService.addToCart(
                            yemek_adi = food.yemekAdi,
                            yemek_resim_adi = food.yemekResimAdi,
                            yemek_fiyat = food.yemekFiyat,
                            yemek_siparis_adet = 1
                        )
                        
                        if (response.isSuccessful) {
                            response.body()?.let { crudResponse ->
                                if (crudResponse.success == 1) {
                                    Resource.Success("Successfully added to cart")
                                } else {
                                    Resource.Error(crudResponse.message)
                                }
                            } ?: Resource.Error("Response body is null")
                        } else {
                            Resource.Error("API Error: ${response.message()}")
                        }
                    }
                }
                else -> Resource.Error("Failed to check cart status")
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }

    // For adding from detail screen with specific quantity
    suspend fun addToCartFromDetail(food: Food, quantity: Int): Resource<String> = withContext(Dispatchers.IO) {
        try {
            when (val cartResult = getCartFoods()) {
                is Resource.Success -> {
                    val currentCart = cartResult.data ?: emptyList()
                    val existingItem = currentCart.find { it.yemekAdi == food.yemekAdi }
                    
                    if (existingItem != null) {
                        // Remove existing item first
                        val removeResult = removeFromCart(existingItem.sepetYemekId)
                        if (removeResult !is Resource.Success) {
                            return@withContext removeResult
                        }
                    }
                    
                    // Add with new quantity
                    val response = apiService.addToCart(
                        yemek_adi = food.yemekAdi,
                        yemek_resim_adi = food.yemekResimAdi,
                        yemek_fiyat = food.yemekFiyat,
                        yemek_siparis_adet = quantity
                    )
                    
                    if (response.isSuccessful) {
                        response.body()?.let { crudResponse ->
                            if (crudResponse.success == 1) {
                                Resource.Success("Successfully updated cart")
                            } else {
                                Resource.Error(crudResponse.message)
                            }
                        } ?: Resource.Error("Response body is null")
                    } else {
                        Resource.Error("API Error: ${response.message()}")
                    }
                }
                else -> Resource.Error("Failed to check cart status")
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }

    // For updating quantity in cart
    suspend fun updateCartQuantity(cartFood: CartFood, newQuantity: Int): Resource<String> = withContext(Dispatchers.IO) {
        try {
            if (newQuantity <= 0) {
                return@withContext removeFromCart(cartFood.sepetYemekId)
            }

            // Remove existing item first
            val removeResult = removeFromCart(cartFood.sepetYemekId)
            if (removeResult !is Resource.Success) {
                return@withContext removeResult
            }
            
            // Add with new quantity
            val response = apiService.addToCart(
                yemek_adi = cartFood.yemekAdi,
                yemek_resim_adi = cartFood.yemekResimAdi,
                yemek_fiyat = cartFood.yemekFiyat,
                yemek_siparis_adet = newQuantity
            )
            
            if (response.isSuccessful) {
                response.body()?.let { crudResponse ->
                    if (crudResponse.success == 1) {
                        Resource.Success("Successfully updated quantity")
                    } else {
                        Resource.Error(crudResponse.message)
                    }
                } ?: Resource.Error("Response body is null")
            } else {
                Resource.Error("API Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network Error: ${e.localizedMessage}")
        }
    }
} 