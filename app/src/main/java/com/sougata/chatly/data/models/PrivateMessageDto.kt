package com.sougata.chatly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivateMessageDto(
    @SerialName("_private_chat_id") val privateChatId: Long,
    @SerialName("_limit") val limit: Long,
    @SerialName("_offset") val offset: Long
)
