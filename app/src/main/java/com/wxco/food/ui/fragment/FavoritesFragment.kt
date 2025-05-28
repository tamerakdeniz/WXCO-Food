package com.wxco.food.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.wxco.food.data.model.FavoriteFood
import com.wxco.food.databinding.FragmentFavoritesBinding
import com.wxco.food.ui.adapter.FavoriteAdapter
import com.wxco.food.ui.viewmodel.FavoritesViewModel
import com.wxco.food.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var favoriteAdapter: FavoriteAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
    }
    
    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(
            onItemClick = { favoriteFood -> navigateToFoodDetail(favoriteFood) },
            onRemoveFromFavorites = { favoriteFood -> removeFromFavorites(favoriteFood) },
            onAddToCart = { favoriteFood -> addToCart(favoriteFood) }
        )
        
        binding.recyclerViewFavorites.apply {
            adapter = favoriteAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    
    private fun observeViewModel() {
        viewModel.favoriteFoods.observe(viewLifecycleOwner) { favoriteFoods ->
            favoriteAdapter.submitList(favoriteFoods)
            updateEmptyState(favoriteFoods.isEmpty())
        }
        
        viewModel.addToCartResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), resource.data, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.textEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewFavorites.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }
    }
    
    private fun removeFromFavorites(favoriteFood: FavoriteFood) {
        viewModel.removeFromFavorites(favoriteFood)
        Toast.makeText(requireContext(), "${favoriteFood.yemekAdi} removed from favorites!", Toast.LENGTH_SHORT).show()
    }
    
    private fun addToCart(favoriteFood: FavoriteFood) {
        viewModel.addToCart(favoriteFood)
    }
    
    private fun navigateToFoodDetail(favoriteFood: FavoriteFood) {
        try {
            val food = com.wxco.food.data.model.Food(
                yemekId = favoriteFood.yemekId,
                yemekAdi = favoriteFood.yemekAdi,
                yemekResimAdi = favoriteFood.yemekResimAdi,
                yemekFiyat = favoriteFood.yemekFiyat
            )
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToFoodDetailFragment(food)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Food detail: ${favoriteFood.yemekAdi}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 