package com.sougata.chatly

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sougata.chatly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = this._binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this._binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
                    .start()
            } else {
                this.binding.bottomNav.animate()
                    .translationY(this.binding.bottomNav.height.toFloat())
                    .setDuration(300)
                    .start()
            }
        }
    }

    private fun setupSearchView() {
//        this.binding.searchView.setOnSearchClickListener {
//            this.binding.tvToolbarHeader.visibility = View.GONE
//        }
//        this.binding.searchView.setOnCloseListener {
//            this.binding.tvToolbarHeader.visibility = View.VISIBLE
//            false
//        }
//
//        this.searchViewEditText =
//            this.binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
//
//        this.searchViewEditText.apply {
//            setTextColor(AppCompatResources.getColorStateList(this@MainActivity, R.color.black))
//
//            hint = "Search"
//            setHintTextColor(AppCompatResources.getColorStateList(this@MainActivity, R.color.grey))
//        }
    }

}