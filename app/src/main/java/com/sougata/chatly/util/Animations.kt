package com.sougata.chatly.util

import androidx.navigation.NavOptions
import com.sougata.chatly.R

object Animations {
    val FRAGMENT_SLIDE = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()

}