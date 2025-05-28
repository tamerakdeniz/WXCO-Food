package com.wxco.food.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wxco.food.data.model.Food
import com.wxco.food.data.repository.FoodRepository
import com.wxco.food.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {
    
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity
    
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    
    private val _addToCartResult = MutableLiveData<Boolean>()
    val addToCartResult: LiveData<Boolean> = _addToCartResult
    
    private var currentFood: Food? = null
    
    init {
        _quantity.value = 1
    }
    
    fun setFood(food: Food) {
        currentFood = food
        checkIfFavorite(food.yemekId)
    }
    
    private fun checkIfFavorite(foodId: Int) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(foodId)
            _isFavorite.value = isFav
        }
    }
    
    fun increaseQuantity() {
        val currentQuantity = _quantity.value ?: 1
        _quantity.value = currentQuantity + 1
    }
    
    fun decreaseQuantity() {
        val currentQuantity = _quantity.value ?: 1
        if (currentQuantity > 1) {
            _quantity.value = currentQuantity - 1
        }
    }
    
    fun addToCart() {
        val food = currentFood ?: return
        val currentQuantity = _quantity.value ?: 1
        
        viewModelScope.launch {
            val result = repository.addToCartFromDetail(food, currentQuantity)
            _addToCartResult.value = result is Resource.Success
        }
    }
    
    fun toggleFavorite() {
        val food = currentFood ?: return
        
        viewModelScope.launch {
            val currentIsFavorite = _isFavorite.value ?: false
            if (currentIsFavorite) {
                repository.removeFromFavorites(food.yemekId)
                _isFavorite.value = false
            } else {
                repository.addToFavorites(food)
                _isFavorite.value = true
            }
        }
    }
} 