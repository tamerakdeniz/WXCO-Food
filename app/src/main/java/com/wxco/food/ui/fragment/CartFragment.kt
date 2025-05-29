package com.wxco.food.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wxco.food.data.model.CartFood
import com.wxco.food.databinding.FragmentCartBinding
import com.wxco.food.ui.adapter.CartAdapter
import com.wxco.food.ui.viewmodel.CartViewModel
import com.wxco.food.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
        setupCheckoutButton()
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshCart()
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onRemoveFromCart = { cartFood ->
                viewModel.removeFromCart(cartFood)
            },
            onQuantityChange = { cartFood, newQuantity ->
                updateCartItemQuantity(cartFood, newQuantity)
            }
        )
        
        binding.recyclerViewCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }
    
    private fun observeViewModel() {
        viewModel.cartFoods.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val cartFoods = resource.data ?: emptyList()
                    
                    if (cartFoods.isEmpty()) {
                        showEmptyState()
                    } else {
                        showCartItems(cartFoods)
                    }
                    cartAdapter.submitList(cartFoods)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                    showEmptyState()
                }
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            binding.textTotal.text = "Total: ₺$totalPrice"
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { resource ->
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
    
    private fun showEmptyState() {
        binding.textEmpty.visibility = View.VISIBLE
        binding.recyclerViewCart.visibility = View.GONE
        binding.layoutTotal.visibility = View.GONE
        binding.checkoutAnimation.visibility = View.GONE
    }
    
    private fun showCartItems(cartFoods: List<Any>) {
        binding.textEmpty.visibility = View.GONE
        binding.recyclerViewCart.visibility = View.VISIBLE
        binding.layoutTotal.visibility = View.VISIBLE
        binding.checkoutAnimation.visibility = View.GONE
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshCart()
            binding.swipeRefresh.isRefreshing = false
        }
    }
    
    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            try {
                showCheckoutAnimation()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "An error occurred during the checkout process", Toast.LENGTH_SHORT).show()
                showEmptyState()
            }
        }
    }

    private fun showCheckoutAnimation() {
        binding.recyclerViewCart.visibility = View.GONE
        binding.layoutTotal.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
        binding.checkoutAnimation.visibility = View.VISIBLE

        binding.checkoutAnimation.apply {
            setAnimationFromUrl("https://lottie.host/7fa3bb6b-3649-4753-935e-969fdee5230c/X5bTY3mi0d.lottie")
            repeatCount = 0
            speed = 1f
            playAnimation()
            
            addAnimatorListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if (isAdded && activity != null) {
                        showEmptyState()
                        viewModel.clearCart()
                    }
                }
                
                override fun onAnimationCancel(animation: android.animation.Animator) {
                    if (isAdded && activity != null) {
                        showEmptyState()
                    }
                }
                
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
        }
    }
    
    private fun updateCartItemQuantity(cartFood: CartFood, newQuantity: Int) {
        viewModel.updateCartItemQuantity(cartFood, newQuantity)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 