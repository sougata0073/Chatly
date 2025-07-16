package com.sougata.chatly.util

class CoolDownTimer(
    private val coolDownTime: Long
) {

    private var startTime: Long = 0
    private var endTime: Long = 0

    fun startTimer() {
        this.startTime = System.currentTimeMillis()
        this.endTime = this.startTime + this.coolDownTime
    }

    fun isTimerFinished(): Boolean {
        return System.currentTimeMillis() >= this.endTime
    }

    fun isTimerRunning(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime > this.startTime && currentTime < this.endTime
    }

    fun resetTimer() {
        this.startTime = 0
        this.endTime = 0
    }

}