package com.sougata.chatly.common

import java.util.Locale

data class Date(
    val day: Int,
    val month: Int,
    val year: Int
) {
    companion object {
        const val DATE_FORMAT = "%02d-%02d-%04d"
    }

    override fun toString(): String {
        return String.format(
            Locale.getDefault(), DATE_FORMAT, day, month, year
        )
    }
}
