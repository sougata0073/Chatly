package com.sougata.chatly.features.chats.util

import androidx.recyclerview.widget.DiffUtil
import com.sougata.chatly.data.models.PrivateMessage

class PrivateMessagesDiffUtil(
    private val oldList: List<PrivateMessage>,
    private val newList: List<PrivateMessage>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return this.oldList.size
    }

    override fun getNewListSize(): Int {
        return this.newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return this.oldList[oldItemPosition].id == this.newList[newItemPosition].id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return when {
            this.oldList[oldItemPosition].privateChatId != this.newList[newItemPosition].privateChatId -> false
            this.oldList[oldItemPosition].senderId != this.newList[newItemPosition].senderId -> false
            this.oldList[oldItemPosition].receiverId != this.newList[newItemPosition].receiverId -> false
            this.oldList[oldItemPosition].text != this.newList[newItemPosition].text -> false
            this.oldList[oldItemPosition].mediaType != this.newList[newItemPosition].mediaType -> false
            this.oldList[oldItemPosition].mediaUrl != this.newList[newItemPosition].mediaUrl -> false
            this.oldList[oldItemPosition].isTextEdited != this.newList[newItemPosition].isTextEdited -> false
            this.oldList[oldItemPosition].createdAt != this.newList[newItemPosition].createdAt -> false
            else -> true
        }
    }
}