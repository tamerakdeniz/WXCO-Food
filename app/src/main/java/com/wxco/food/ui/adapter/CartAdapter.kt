package com.wxco.food.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wxco.food.R
import com.wxco.food.data.model.CartFood
import com.wxco.food.databinding.ItemCartFoodBinding

class CartAdapter(
    private val onRemoveFromCart: (CartFood) -> Unit,
    private val onQuantityChange: (CartFood, Int) -> Unit
) : ListAdapter<CartFood, CartAdapter.CartViewHolder>(CartDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class CartViewHolder(private val binding: ItemCartFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(cartFood: CartFood) {
            with(binding) {
                textFoodName.text = cartFood.yemekAdi
                textQuantity.text = "Quantity: ${cartFood.yemekSiparisAdet}"
                textFoodPrice.text = "Unit Price: ₺${cartFood.yemekFiyat}"
                textQtyValue.text = cartFood.yemekSiparisAdet.toString()
                
                val totalPrice = cartFood.yemekFiyat * cartFood.yemekSiparisAdet
                textTotalPrice.text = "Total: ₺$totalPrice"

                Glide.with(imageFood.context)
                    .load(cartFood.imageUrl)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(imageFood)

                btnIncrease.setOnClickListener { 
                    val newQuantity = cartFood.yemekSiparisAdet + 1
                    onQuantityChange(cartFood, newQuantity)
                }

                btnDecrease.setOnClickListener { 
                    val newQuantity = cartFood.yemekSiparisAdet - 1
                    if (newQuantity > 0) {
                        onQuantityChange(cartFood, newQuantity)
                    } else {
                        onRemoveFromCart(cartFood)
                    }
                }

                btnRemove.setOnClickListener { onRemoveFromCart(cartFood) }
            }
        }
    }
    
    private class CartDiffCallback : DiffUtil.ItemCallback<CartFood>() {
        override fun areItemsTheSame(oldItem: CartFood, newItem: CartFood): Boolean {
            return oldItem.sepetYemekId == newItem.sepetYemekId
        }
        
        override fun areContentsTheSame(oldItem: CartFood, newItem: CartFood): Boolean {
            return oldItem == newItem
        }
    }
} 