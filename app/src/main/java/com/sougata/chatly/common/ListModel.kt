package com.sougata.chatly.common

interface ListModel <IdT, T> {
    val id: IdT?
    fun areContentsTheSame(other: T): Boolean
}