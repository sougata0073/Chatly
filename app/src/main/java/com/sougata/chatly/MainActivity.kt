package com.sougata.chatly

import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.databinding.ActivityMainBinding
import com.sougata.chatly.util.DecoratedViews

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = this._binding!!

    private lateinit var searchViewEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this._binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.setupBottomNav()
        this.setupSearchView()
        this.setupAddButton()
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
    }

    private fun setupSearchView() {

        this.binding.searchView.setOnSearchClickListener {
            this.binding.tvToolbarHeader.visibility = View.GONE
        }
        this.binding.searchView.setOnCloseListener {
            this.binding.tvToolbarHeader.visibility = View.VISIBLE
            false
        }

        this.searchViewEditText =
            this.binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)

        this.searchViewEditText.apply {
            setTextColor(AppCompatResources.getColorStateList(this@MainActivity, R.color.black))

            hint = "Search"
            setHintTextColor(AppCompatResources.getColorStateList(this@MainActivity, R.color.grey))
        }
    }

    private fun setupAddButton() {
        this.binding.btnAdd.setOnClickListener {
            val popupMenu = PopupMenu(
                ContextThemeWrapper(this, R.style.popupMenu),
                it
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addFriend -> {
                        DecoratedViews.showSnackBar(
                            this.binding.root,
                            this.binding.bottomNav,
                            "Add friend",
                            Snackbar.LENGTH_SHORT
                        )
                        true
                    }

                    R.id.createGroup -> {
                        DecoratedViews.showSnackBar(
                            this.binding.root,
                            this.binding.bottomNav,
                            "Create group",
                            Snackbar.LENGTH_SHORT
                        )
                        true
                    }

                    else -> false
                }
            }

            popupMenu.menuInflater.inflate(R.menu.toolbar_add_menu, popupMenu.menu)
            popupMenu.show()
        }
    }

}