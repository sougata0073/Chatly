package com.sougata.chatly.util

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sougata.chatly.common.ListModel

abstract class RecyclerViewUtil<IdT, T : ListModel<IdT, T>, VH : RecyclerView.ViewHolder>(
    open var itemsList: MutableList<T>
) : RecyclerView.Adapter<VH>() {

    private var isItemLoaderVisible = false

    fun setItems(newItemsList: List<T>) {
        this.hideItemLoader()

        val diffUtil = MyDiffUtil(this.itemsList, newItemsList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.itemsList = newItemsList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return this.itemsList.size
    }

    fun insertItemAt(position: Int, item: T) {
        this.hideItemLoader()

        this.itemsList.add(position, item)
        this.notifyItemInserted(position)
    }

    fun insertItemAtFirst(item: T) {
        this.insertItemAt(0, item)
    }

    fun updateItemAt(position: Int, item: T) {
        this.hideItemLoader()

        this.itemsList[position] = item
        this.notifyItemChanged(position)
    }

    fun updateItemById(id: IdT, item: T) {
        this.hideItemLoader()

        val index = this.itemsList.indexOfFirst { it.id == id }
        if (index != -1) {
            this.updateItemAt(index, item)
        }
    }

    fun removeItemAt(position: Int) {
        this.hideItemLoader()

        this.itemsList.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun removeItemById(id: Long) {
        this.hideItemLoader()

        val index = this.itemsList.indexOfFirst { it.id == id }
        if (index != -1) {
            this.removeItemAt(index)
        }
    }

    fun getItemIdAt(position: Int): IdT? {
        return if (this.itemsList.isEmpty() ||
            position < 0 ||
            position >= this.itemsList.size
        ) {
            null
        } else {
            this.itemsList[position].id
        }
    }

    fun showItemLoader(dummyModel: T) {
        if (this.isItemLoaderVisible) {
            return
        }
        this.isItemLoaderVisible = true
        this.itemsList.add(dummyModel)
        this.notifyItemInserted(this.itemsList.lastIndex)
    }

    fun hideItemLoader() {
        if (!this.isItemLoaderVisible) {
            return
        }
        val lastIndex = this.itemsList.lastIndex
        if (lastIndex >= 0) {
            val lastItem = this.itemsList[lastIndex]
            if (lastItem.id == null) {
                this.isItemLoaderVisible = false
                this.itemsList.removeAt(lastIndex)
                this.notifyItemRemoved(lastIndex)
            }
        }
    }
}