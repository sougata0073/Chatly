package com.sougata.chatly.data.models

import com.sougata.chatly.common.ListModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestReceived(
    @SerialName("id") override val id: Long? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("sending_user") val sendingUser: User? = null
): ListModel<Long, FriendRequestReceived> {
    override fun areContentsTheSame(other: FriendRequestReceived): Boolean {
        return when {
            this.senderId != other.senderId -> false
            this.receiverId != other.receiverId -> false
            this.status != other.status -> false
            this.createdAt != other.createdAt -> false
            this.sendingUser != other.sendingUser -> false
            else -> true
        }
    }
}
