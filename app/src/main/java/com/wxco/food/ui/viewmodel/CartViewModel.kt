package com.wxco.food.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wxco.food.data.model.CartFood
import com.wxco.food.data.repository.FoodRepository
import com.wxco.food.utils.Resource
import com.wxco.food.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {
    
    private val _cartFoods = MutableLiveData<Resource<List<CartFood>>>()
    val cartFoods: LiveData<Resource<List<CartFood>>> = _cartFoods
    
    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice
    
    private val _operationResult = SingleLiveEvent<Resource<String>>()
    val operationResult: LiveData<Resource<String>> = _operationResult
    
    init {
        loadCartFoods()
    }
    
    fun loadCartFoods() {
        _cartFoods.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getCartFoods()
            _cartFoods.value = result
            if (result is Resource.Success) {
                calculateTotalPrice(result.data ?: emptyList())
            }
        }
    }
    
    fun removeFromCart(cartFood: CartFood) {
        _operationResult.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.removeFromCart(cartFood.sepetYemekId)
            _operationResult.value = result
            if (result is Resource.Success) {
                loadCartFoods()
            }
        }
    }
    
    fun updateCartItemQuantity(cartFood: CartFood, newQuantity: Int) {
        _operationResult.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.updateCartQuantity(cartFood, newQuantity)
            _operationResult.value = result
            if (result is Resource.Success) {
                loadCartFoods()
            }
        }
    }
    
    private fun calculateTotalPrice(cartFoods: List<CartFood>) {
        val total = cartFoods.sumOf { it.totalPrice }
        _totalPrice.value = total
    }
    
    fun refreshCart() {
        loadCartFoods()
    }

    fun clearCart() {
        _operationResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                // Get all cart items
                when (val cartResult = repository.getCartFoods()) {
                    is Resource.Success -> {
                        val cartFoods = cartResult.data ?: emptyList()
                        
                        // Remove each item from cart
                        cartFoods.forEach { cartFood ->
                            repository.removeFromCart(cartFood.sepetYemekId)
                        }
                        
                        _operationResult.value = Resource.Success("Cart cleared successfully")
                        loadCartFoods() // Refresh cart state
                    }
                    else -> {
                        _operationResult.value = Resource.Error("Failed to clear cart")
                    }
                }
            } catch (e: Exception) {
                _operationResult.value = Resource.Error("Error clearing cart: ${e.localizedMessage}")
            }
        }
    }
} 