package com.sougata.chatly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse(
    @SerialName("is_successful") val isSuccessful: Boolean? = null,
    @SerialName("message") val message: String? = null
)
