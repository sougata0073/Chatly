package com.sougata.chatly.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrivateMessagePostDto(
    @SerialName("_private_chat_id") val privateChatId: Long,
    @SerialName("_receiver_id") val receiverId: String,
    @SerialName("_text") val text: String?,
    @SerialName("_media_type") val mediaType: String?,
    @SerialName("_media_url") val mediaUrl: String?
)
