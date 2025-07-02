package com.sougata.chatly.common

data class TaskResult<T>(
    val result: T?,
    val taskStatus: TaskStatus,
    val message: String
)