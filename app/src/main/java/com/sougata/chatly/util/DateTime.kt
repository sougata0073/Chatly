package com.sougata.chatly.util

import com.sougata.chatly.common.Date
import com.sougata.chatly.common.Time
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

    fun millisToTime(timeInMillis: Long): Time {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return Time(hour, minute)
    }

    fun millisToDateTime(timeInMillis: Long): Pair<Date, Time> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return Date(day, month, year) to Time(hour, minute)
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

    fun isoTimestampToDateString(isoTimestamp: String): String {
        return this.isoTimestampToDate(isoTimestamp).toString()
    }

    fun isoTimestampToTime(isoTimestamp: String): Time {
        return this.millisToTime(this.isoTimestampToMillis(isoTimestamp))
    }

    fun isoTimestampToTimeString(isoTimestamp: String): String {
        return this.isoTimestampToTime(isoTimestamp).toString()
    }

    fun isoTimestampToDateTime(isoTimestamp: String): Pair<Date, Time> {
        return this.millisToDateTime(this.isoTimestampToMillis(isoTimestamp))
    }

    fun isoTimestampToDateTimeString(isoTimestamp: String): String {
        val dateTime = this.isoTimestampToDateTime(isoTimestamp)
        val date = dateTime.first
        val time = dateTime.second

        return "$date $time"
    }

    fun millisToISOTimestampString(timeInMillis: Long): String {
        return Instant.ofEpochMilli(timeInMillis)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getCurrentISOTimestamp(): String {
        return this.millisToISOTimestampString(this.getCurrentTimeMillis())
    }
}