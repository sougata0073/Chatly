package com.sougata.chatly.features.chats.util

import androidx.recyclerview.widget.DiffUtil
import com.sougata.chatly.data.models.PrivateMessageWrapper

class PrivateMessageWrappersDiffUtil(
    private val oldList: List<PrivateMessageWrapper>,
    private val newList: List<PrivateMessageWrapper>
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
            this.oldList[oldItemPosition].privateMessage != this.newList[newItemPosition].privateMessage -> false
            this.oldList[oldItemPosition].sender != this.newList[newItemPosition].sender -> false
            this.oldList[oldItemPosition].receiver != this.newList[newItemPosition].receiver -> false
            else -> true
        }
    }
}