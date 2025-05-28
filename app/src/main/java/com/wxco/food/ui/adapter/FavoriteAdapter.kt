package com.wxco.food.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wxco.food.R
import com.wxco.food.data.model.FavoriteFood
import com.wxco.food.databinding.ItemFavoriteFoodBinding

class FavoriteAdapter(
    private val onItemClick: (FavoriteFood) -> Unit,
    private val onRemoveFromFavorites: (FavoriteFood) -> Unit,
    private val onAddToCart: (FavoriteFood) -> Unit
) : ListAdapter<FavoriteFood, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteFood: FavoriteFood) {
            binding.apply {
                textFoodName.text = favoriteFood.yemekAdi
                textFoodPrice.text = "₺${favoriteFood.yemekFiyat}"

                Glide.with(itemView.context)
                    .load(favoriteFood.imageUrl)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(imageFood)

                root.setOnClickListener {
                    onItemClick(favoriteFood)
                }

                btnRemoveFavorite.setOnClickListener {
                    onRemoveFromFavorites(favoriteFood)
                }

                btnAddToCart.setOnClickListener {
                    onAddToCart(favoriteFood)
                }
            }
        }
    }

    private class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteFood>() {
        override fun areItemsTheSame(oldItem: FavoriteFood, newItem: FavoriteFood): Boolean {
            return oldItem.yemekId == newItem.yemekId
        }

        override fun areContentsTheSame(oldItem: FavoriteFood, newItem: FavoriteFood): Boolean {
            return oldItem == newItem
        }
    }
} 