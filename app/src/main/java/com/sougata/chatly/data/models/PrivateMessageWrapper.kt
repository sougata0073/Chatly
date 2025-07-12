package com.sougata.chatly.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PrivateMessageWrapper(
    @SerialName("private_chat_message_id") val id: Long? = null,
    @SerialName("message") val privateMessage: PrivateMessage? = null,
    @SerialName("sender") val sender: User? = null,
    @SerialName("receiver") val receiver: User? = null
): Parcelable