package com.sougata.chatly.common

import java.util.Locale

data class Time(
    val hour: Int,
    val minute: Int,
) {
    companion object {
        const val TIME_FORMAT = "%02d:%02d"
    }

    override fun toString(): String {
        return String.format(
            Locale.getDefault(), TIME_FORMAT, hour, minute
        )
    }
}
