package com.wxco.food.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.wxco.food.R
import com.wxco.food.databinding.FragmentFoodDetailBinding
import com.wxco.food.ui.viewmodel.FoodDetailViewModel
import com.wxco.food.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodDetailFragment : Fragment() {
    
    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: FoodDetailFragmentArgs by navArgs()
    private val viewModel: FoodDetailViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeData()
        setupClickListeners()
        viewModel.setFood(args.food)
    }
    
    private fun setupUI() {
        val food = args.food
        
        binding.textFoodName.text = food.yemekAdi
        binding.textFoodPrice.text = "₺${food.yemekFiyat}"
        Glide.with(this)
            .load("${Constants.BASE_IMAGE_URL}${food.yemekResimAdi}")
            .placeholder(R.drawable.ic_food_placeholder)
            .error(R.drawable.ic_food_placeholder)
            .into(binding.imageFoodDetail)
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMinus.setOnClickListener {
            viewModel.decreaseQuantity()
        }
        
        binding.btnPlus.setOnClickListener {
            viewModel.increaseQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }
    
    private fun observeData() {
        viewModel.quantity.observe(viewLifecycleOwner) { quantity ->
            binding.textQuantity.text = quantity.toString()
            updateTotalPrice(quantity)
        }
        
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite) {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }
        
        viewModel.addToCartResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigateUp()
            }
        }
    }
    
    private fun updateTotalPrice(quantity: Int) {
        val food = args.food
        val totalPrice = food.yemekFiyat.toInt() * quantity
        binding.textTotalPrice.text = "Total: ₺$totalPrice"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 