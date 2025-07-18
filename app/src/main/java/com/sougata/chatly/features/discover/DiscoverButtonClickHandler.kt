package com.sougata.chatly.features.discover

import android.content.Context
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.util.DecoratedViews

class DiscoverButtonClickHandler(
    private val view: View,
    private val context: Context,
) : View.OnClickListener {

    private val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.popup_enter)
        .setExitAnim(R.anim.popup_exit)
        .setPopEnterAnim(R.anim.popup_enter_back)
        .setPopExitAnim(R.anim.popup_exit_back)
        .build()

    override fun onClick(p0: View?) {

        val popupMenu = PopupMenu(
            ContextThemeWrapper(this.context, R.style.popupMenu), this.view
        )

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addFriend -> {
                    this.view.findNavController().navigate(R.id.addFriendFragment, null, this.navOptions)

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

                R.id.friendRequests -> {
                    this.view.findNavController().navigate(R.id.friendRequestsFragment, null, this.navOptions)
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