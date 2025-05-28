package com.wxco.food.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wxco.food.R
import com.wxco.food.data.model.Food
import com.wxco.food.databinding.ItemFoodBinding

class FoodAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit,
    private val isFavoriteCheck: suspend (Int) -> Boolean
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FoodViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(food: Food) {
            with(binding) {
                textFoodName.text = food.yemekAdi
                textFoodPrice.text = food.formattedPrice

                Glide.with(imageFood.context)
                    .load(food.imageUrl)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageFood)

                root.setOnClickListener { onItemClick(food) }
                btnAddToCart.setOnClickListener { onAddToCartClick(food) }
            }
        }
    }
    
    private class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.yemekId == newItem.yemekId
        }
        
        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
} 