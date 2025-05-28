package com.wxco.food.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wxco.food.data.model.FavoriteFood
import com.wxco.food.data.repository.FoodRepository
import com.wxco.food.utils.Resource
import com.wxco.food.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {
    
    val favoriteFoods: LiveData<List<FavoriteFood>> = repository.getAllFavorites()
    
    private val _addToCartResult = SingleLiveEvent<Resource<String>>()
    val addToCartResult: LiveData<Resource<String>> = _addToCartResult
    
    fun removeFromFavorites(favoriteFood: FavoriteFood) {
        viewModelScope.launch {
            repository.removeFromFavorites(favoriteFood.yemekId)
        }
    }
    
    fun addToCart(favoriteFood: FavoriteFood) {
        viewModelScope.launch {
            val food = favoriteFood.toFood()
            val result = repository.addToCartFromList(food)
            _addToCartResult.value = result
        }
    }
} 