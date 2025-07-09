package com.sougata.chatly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivateChat(
    @SerialName("chat_id") val chatId: Long? = null,
    @SerialName("other_user") val userUid: User? = null
)