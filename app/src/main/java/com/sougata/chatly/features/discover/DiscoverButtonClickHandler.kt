package com.sougata.chatly.features.discover

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.util.DecoratedViews

class DiscoverButtonClickHandler(
    private val view: View,
    private val context: Context
) : View.OnClickListener {
    override fun onClick(p0: View?) {

        val popupMenu = PopupMenu(
            ContextThemeWrapper(this.context, R.style.popupMenu), this.view
        )

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addFriend -> {
                    DecoratedViews.showSnackBar(
                        view,
                        null,
                        "Add friend",
                        Snackbar.LENGTH_SHORT
                    )
                    true
                }

                R.id.createGroup -> {
                    DecoratedViews.showSnackBar(
                        view,
                        null,
                        "Create group",
                        Snackbar.LENGTH_SHORT
                    )
                    true
                }

                else -> false
            }
        }

        popupMenu.apply {
            setForceShowIcon(true)
            menuInflater.inflate(R.menu.toolbar_add_menu, popupMenu.menu)
            show()
        }
    }
}