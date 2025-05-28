package com.wxco.food.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.wxco.food.data.model.Food
import com.wxco.food.databinding.FragmentHomeBinding
import com.wxco.food.ui.adapter.FoodAdapter
import com.wxco.food.ui.viewmodel.HomeViewModel
import com.wxco.food.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var foodAdapter: FoodAdapter
    private var allFoods: List<Food> = emptyList()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        loadMockData()
        setupSwipeRefresh()
    }
    
    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(
            onItemClick = { food -> navigateToFoodDetail(food) },
            onAddToCartClick = { food -> addToCart(food) },
            onFavoriteClick = { food -> toggleFavorite(food) },
            isFavoriteCheck = { foodId -> false }
        )
        
        binding.recyclerViewFoods.apply {
            adapter = foodAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }
    
    private fun setupSearchView() {
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchFoods(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchFoods(it) }
                return true
            }
        }
        
        binding.searchView.setOnQueryTextListener(queryTextListener)
        
        binding.searchView.setOnCloseListener {
            foodAdapter.submitList(allFoods)
            false
        }
    }
    
    private fun observeViewModel() {
        viewModel.addToCartResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Item added to cart! Check cart tab to see items.", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun loadMockData() {
        binding.progressBar.visibility = View.VISIBLE
        
        // Mock data for demonstration
        allFoods = listOf(
            Food(1, "Ayran", "ayran.png", 2),
            Food(2, "Baklava", "baklava.png", 15),
            Food(3, "Fanta", "fanta.png", 3),
            Food(4, "İzgara Somon", "izgarasomon.png", 35),
            Food(5, "İzgara Tavuk", "izgaratavuk.png", 25),
            Food(6, "Kadayıf", "kadayif.png", 12),
            Food(7, "Kahve", "kahve.png", 5),
            Food(8, "Köfte", "kofte.png", 20),
            Food(9, "Lazanya", "lazanya.png", 18),
            Food(10, "Makarna", "makarna.png", 16),
            Food(11, "Pizza", "pizza.png", 22),
            Food(12, "Su", "su.png", 1),
            Food(13, "Sütlaç", "sutlac.png", 8),
            Food(14, "Tiramisu", "tiramisu.png", 14)
        )
        
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewFoods.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE
        foodAdapter.submitList(allFoods)
    }
    
    private fun searchFoods(query: String) {
        if (query.isBlank()) {
            foodAdapter.submitList(allFoods)
        } else {
            val filteredFoods = allFoods.filter { food ->
                food.yemekAdi.contains(query, ignoreCase = true)
            }
            foodAdapter.submitList(filteredFoods)
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadMockData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    
    private fun addToCart(food: Food) {
        viewModel.addToCart(food)
    }
    
    private fun toggleFavorite(food: Food) {
        lifecycleScope.launch {
            val isFavorite = viewModel.isFavorite(food.yemekId)
            if (isFavorite) {
                viewModel.removeFromFavorites(food.yemekId)
                Toast.makeText(requireContext(), "${food.yemekAdi} removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addToFavorites(food)
                Toast.makeText(requireContext(), "${food.yemekAdi} added to favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateToFoodDetail(food: Food) {
        try {
            val action = HomeFragmentDirections.actionHomeFragmentToFoodDetailFragment(food)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Food detail: ${food.yemekAdi}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 