package com.sougata.chatly.util

import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R

object DecoratedViews {

    fun showSnackBar(parentView: View, aboveOf: View, text: String, duration: Int) {
        val snackBar = Snackbar.make(parentView, text, duration)
            .setBackgroundTint(parentView.context.getColor(R.color.snack_bar_bg))
            .setTextColor(parentView.context.getColor(R.color.white))

        snackBar.anchorView = aboveOf

        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
            textSize = 18f
        }

        snackBar.show()
    }

}