package com.sougata.chatly.util

import com.google.firebase.Timestamp
import com.sougata.chatly.common.Date
import java.util.Calendar

object DateTime {

    fun getDate(timestamp: Timestamp): Date {
        val calendar = Calendar.getInstance()
        calendar.time = timestamp.toDate()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return Date(day, month, year)
    }

    fun getDate(timeInMillis: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return Date(day, month, year)
    }

    fun getDateString(timestamp: Timestamp): String {
        return this.getDate(timestamp).toString()
    }

    fun getDateString(timeInMillis: Long): String {
        return this.getDate(timeInMillis).toString()
    }

    fun getTimeStamp(timeInMillis: Long): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        return Timestamp(calendar.time)
    }
}