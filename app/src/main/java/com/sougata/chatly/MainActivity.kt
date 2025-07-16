package com.sougata.chatly

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sougata.chatly.databinding.ActivityMainBinding
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = this._binding!!

    private lateinit var vm: MainActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this._binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        this.vm = ViewModelProvider(this)[MainActivityVM::class.java]

        this.setupBottomNav()
    }

    override fun onDestroy() {
        super.onDestroy()

        this._binding = null
    }

    private fun setupBottomNav() {
        val navHostFragment =
            this.supportFragmentManager.findFragmentById(R.id.navHostMain) as NavHostFragment
        val navController = navHostFragment.navController
        this.binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id

            if (id == R.id.chatsHomeFragment ||
                id == R.id.groupsHomeFragment ||
                id == R.id.profileHomeFragment ||
                id == R.id.moreHomeFragment
            ) {

                this.binding.bottomNav.animate()
                    .translationY(0f)
                    .setDuration(300)
                    .withStartAction {
                        this.binding.bottomNav.visibility = View.VISIBLE
                    }
                    .start()
            } else {

                this.binding.bottomNav.animate()
                    .translationY(this.binding.bottomNav.height.toFloat())
                    .setDuration(300)
                    .withEndAction {
                        this.binding.bottomNav.visibility = View.GONE
                    }
                    .start()
            }
        }
    }

}