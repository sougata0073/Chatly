package com.sougata.chatly.data.models

import com.sougata.chatly.common.ListModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestSent(
    @SerialName("id") override val id: Long? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("receiving_user") val receivingUser: User? = null
): ListModel<Long, FriendRequestSent> {
    override fun areContentsTheSame(other: FriendRequestSent): Boolean {
        return when {
            this.id != other.id -> false
            this.senderId != other.senderId -> false
            this.receiverId != other.receiverId -> false
            this.status != other.status -> false
            this.createdAt != other.createdAt -> false
            this.receivingUser != other.receivingUser -> false
            else -> true
        }
    }
}
