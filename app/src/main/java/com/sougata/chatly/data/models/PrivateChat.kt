package com.sougata.chatly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivateChat(
    @SerialName("chat_id") val id: Long? = null,
    @SerialName("other_user") val otherUser: User? = null,
    @SerialName("last_message") val lastMessage: PrivateMessage? = null
)