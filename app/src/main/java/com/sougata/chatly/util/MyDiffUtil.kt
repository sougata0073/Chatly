package com.sougata.chatly.util

import androidx.recyclerview.widget.DiffUtil
import com.sougata.chatly.common.ListModel

class MyDiffUtil<IdT, T : ListModel<IdT, T>>(
    private val oldList: List<T>,
    private val newList: List<T>
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
        return this.oldList[oldItemPosition].areContentsTheSame(this.newList[newItemPosition])
    }
}