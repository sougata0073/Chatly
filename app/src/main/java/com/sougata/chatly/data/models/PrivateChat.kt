package com.sougata.chatly.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PrivateChat(
    @SerialName("private_chat_id") val id: Long? = null,
    @SerialName("other_user") val otherUser: User? = null,
    @SerialName("last_message") var lastMessage: PrivateMessage? = null
): Parcelable