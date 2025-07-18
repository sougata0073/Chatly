package com.sougata.chatly.data.models

import android.os.Parcelable
import com.sougata.chatly.common.ListModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class User(
    @SerialName("id") override val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("dob") val dob: String? = null,
    @SerialName("bio") val bio: String? = null,
    @SerialName("location") val location: Location? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("is_profile_updated_once") val isProfileUpdatedOnce: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null
): Parcelable, ListModel<String, User>{
    override fun areContentsTheSame(other: User): Boolean {
        return when {
            this.name != other.name -> false
            this.email != other.email -> false
            this.phoneNumber != other.phoneNumber -> false
            this.gender != other.gender -> false
            this.dob != other.dob -> false
            this.bio != other.bio -> false
            this.location != other.location -> false
            this.profileImageUrl != other.profileImageUrl -> false
            this.isProfileUpdatedOnce != other.isProfileUpdatedOnce -> false
            this.createdAt != other.createdAt -> false
            else -> true
        }
    }
}