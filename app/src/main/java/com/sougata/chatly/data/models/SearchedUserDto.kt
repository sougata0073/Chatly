package com.sougata.chatly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchedUserDto(
    @SerialName("_search_query") val searchQuery: String,
    @SerialName("_limit") val limit: Long,
    @SerialName("_offset") val offset: Long
)