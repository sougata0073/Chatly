package com.sougata.chatly.data.models

import android.os.Parcelable
import com.sougata.chatly.common.ListModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PrivateMessage(
    @SerialName("id") override val id: Long? = null,
    @SerialName("private_chat_id") val privateChatId: Long? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("receiver_id") val receiverId: String? = null,
    @SerialName("text") var text: String? = null,
    @SerialName("media_data") var mediaData: MediaData? = null,
    @SerialName("is_text_edited") var isTextEdited: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null
) : Parcelable, ListModel<Long, PrivateMessage> {

    override fun areContentsTheSame(other: PrivateMessage): Boolean {
        return when {
            this.privateChatId != other.privateChatId -> false
            this.senderId != other.senderId -> false
            this.receiverId != other.receiverId -> false
            this.text != other.text -> false
            this.mediaData != other.mediaData -> false
            this.isTextEdited != other.isTextEdited -> false
            this.createdAt != other.createdAt -> false
            else -> true
        }
    }

}