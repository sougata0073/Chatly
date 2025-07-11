package com.sougata.chatly.features.chats.util

import androidx.recyclerview.widget.DiffUtil
import com.sougata.chatly.data.models.PrivateChat

class PrivateChatsDiffUtil(
    private val oldList: List<PrivateChat>,
    private val newList: List<PrivateChat>
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
        return this.oldList[oldItemPosition].otherUser == this.newList[newItemPosition].otherUser
    }
}