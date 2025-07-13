package com.sougata.chatly.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PrivateMessage(
    @SerialName("id") val id: Long? = null,
    @SerialName("private_chat_id") val privateChatId: Long? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("text") var text: String? = null,
    @SerialName("media_type") var mediaType: String? = null,
    @SerialName("media_url") var mediaUrl: String? = null,
    @SerialName("is_text_edited") var isTextEdited: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null
): Parcelable