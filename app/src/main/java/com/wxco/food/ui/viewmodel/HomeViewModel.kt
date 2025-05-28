package com.wxco.food.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wxco.food.data.model.Food
import com.wxco.food.data.repository.FoodRepository
import com.wxco.food.utils.Resource
import com.wxco.food.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {
    
    private val _foods = MutableLiveData<Resource<List<Food>>>()
    val foods: LiveData<Resource<List<Food>>> = _foods
    
    private val _searchResults = MutableLiveData<List<Food>>()
    val searchResults: LiveData<List<Food>> = _searchResults
    
    private val _isSearchMode = MutableLiveData<Boolean>()
    val isSearchMode: LiveData<Boolean> = _isSearchMode
    
    private val _addToCartResult = SingleLiveEvent<Resource<String>>()
    val addToCartResult: LiveData<Resource<String>> = _addToCartResult
    
    private var allFoods: List<Food> = emptyList()
    
    init {
        loadFoods()
    }
    
    fun loadFoods() {
        _foods.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getAllFoods()
            _foods.value = result
            if (result is Resource.Success) {
                allFoods = result.data ?: emptyList()
            }
        }
    }
    
    fun searchFoods(query: String) {
        if (query.isBlank()) {
            _isSearchMode.value = false
            _searchResults.value = emptyList()
        } else {
            _isSearchMode.value = true
            val filteredFoods = allFoods.filter { food ->
                food.yemekAdi.contains(query, ignoreCase = true)
            }
            _searchResults.value = filteredFoods
        }
    }
    
    fun clearSearch() {
        _isSearchMode.value = false
        _searchResults.value = emptyList()
    }
    
    fun addToCart(food: Food) {
        _addToCartResult.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.addToCartFromList(food)
            _addToCartResult.value = result
        }
    }
    
    fun addToFavorites(food: Food) {
        viewModelScope.launch {
            repository.addToFavorites(food)
        }
    }
    
    fun removeFromFavorites(foodId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(foodId)
        }
    }
    
    suspend fun isFavorite(foodId: Int): Boolean {
        return repository.isFavorite(foodId)
    }
} 