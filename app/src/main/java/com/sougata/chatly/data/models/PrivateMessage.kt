package com.sougata.chatly.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PrivateMessage(
    @SerialName("private_chat_id") val privateChatId: Long? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("text") val text: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("is_text_edited") val isTextEdited: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null
): Parcelable