package com.wxco.food

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.wxco.food.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        setupLoadingAnimation()
        showLoadingAnimation() // İlk açılışta animasyonu göster
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Navigation değişikliklerini dinle
        navController.addOnDestinationChangedListener { _, _, _ ->
            showLoadingAnimation()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cartFragment -> {
                    navController.navigate(R.id.cartFragment)
                    true
                }
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.favoritesFragment -> {
                    navController.navigate(R.id.favoritesFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupLoadingAnimation() {
        binding.loadingAnimation.apply {
            setAnimationFromUrl("https://lottie.host/089867eb-af66-4fc8-803c-5a3ec8a7b0be/giYUgdbwD7.lottie")
            repeatCount = 1
            speed = 2f
        }
    }

    private fun showLoadingAnimation() {
        binding.loadingAnimation.apply {
            visibility = View.VISIBLE
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationEnd(p0: Animator) {}
                override fun onAnimationCancel(p0: Animator) {
                    visibility = View.GONE
                }
                override fun onAnimationRepeat(p0: Animator) {}
            })
            playAnimation()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            hideLoadingAnimation()
        }, 800)
    }

    private fun hideLoadingAnimation() {
        binding.loadingAnimation.apply {
            removeAllAnimatorListeners()
            cancelAnimation()
            visibility = View.GONE
        }
    }
} 