package com.sougata.chatly.data.models

import android.os.Parcelable
import com.sougata.chatly.common.ListModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PrivateChat(
    @SerialName("private_chat_id") override val id: Long? = null,
    @SerialName("other_user") val otherUser: User? = null,
    @SerialName("last_message") var lastMessage: PrivateMessage? = null
): Parcelable, ListModel<Long, PrivateChat> {

    override fun areContentsTheSame(other: PrivateChat): Boolean {
        return when {
            this.otherUser != other.otherUser -> false
            this.lastMessage != other.lastMessage -> false
            else -> true
        }
    }
}