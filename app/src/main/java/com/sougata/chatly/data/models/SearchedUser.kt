package com.sougata.chatly.data.models

import com.sougata.chatly.common.ListModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchedUser(
    @SerialName("searched_user_id") override val id: String? = null,
    @SerialName("searched_user") val user: User? = null,
    @SerialName("is_friends") val isFriends: Boolean? = null,
    @SerialName("friend_request_status") var friendRequestStatus: String? = null
) : ListModel<String, SearchedUser> {

    override fun areContentsTheSame(other: SearchedUser): Boolean {
        return when {
            this.user != other.user -> false
            this.isFriends != other.isFriends -> false
            this.friendRequestStatus != other.friendRequestStatus -> false
            else -> true
        }
    }

}