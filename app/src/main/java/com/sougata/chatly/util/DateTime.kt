package com.sougata.chatly.util

import com.sougata.chatly.common.Date
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

object DateTime {

    fun millisToDate(timeInMillis: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return Date(day, month, year)
    }

    fun millisToDateString(timeInMillis: Long): String {
        return this.millisToDate(timeInMillis).toString()
    }

    fun isoTimestampToMillis(isoTimestamp: String): Long {
        val dateTime = OffsetDateTime.parse(isoTimestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return dateTime.toInstant().toEpochMilli()
    }

    fun isoTimestampToDate(isoTimestamp: String): Date {
        return this.millisToDate(this.isoTimestampToMillis(isoTimestamp))
    }

    fun millisToISOTimestampString(timeInMillis: Long): String {
        return Instant.ofEpochMilli(timeInMillis)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}