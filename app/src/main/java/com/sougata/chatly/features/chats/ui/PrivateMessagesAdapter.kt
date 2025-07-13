package com.sougata.chatly.features.chats.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateMessage
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.databinding.ItemTextOnlyPrivateMessageReceiverBinding
import com.sougata.chatly.databinding.ItemTextOnlyPrivateMessageSenderBinding
import com.sougata.chatly.features.chats.util.PrivateMessagesDiffUtil
import com.sougata.chatly.util.DateTime
import io.github.jan.supabase.auth.auth

class PrivateMessagesAdapter(
    private var itemsList: MutableList<PrivateMessage>
) : RecyclerView.Adapter<PrivateMessagesAdapter.MyViewHolder>() {

    private val currentUserId = MySupabaseClient.getInstance().auth.currentUserOrNull()!!.id

    private val viewTypeSender = 1
    private val viewTypeReceiver = 2
    private val viewTypeLoader = 3

    private var isItemLoaderVisible = false

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pmw: PrivateMessage) {
            if (this.binding is ItemTextOnlyPrivateMessageSenderBinding) {
                this.binding.apply {
                    tvText.text = pmw.id.toString() + " " + pmw.text
                    val createdAt = pmw.createdAt
                    if (createdAt != null) {
                        tvDateTime.text = DateTime.isoTimestampToDateTimeString(createdAt)
                    }
                    if (pmw.isTextEdited == true) {
                        tvIsTextEdited.apply {
                            visibility = View.VISIBLE
                            text = "Edited"
                        }
                    } else {
                        tvIsTextEdited.visibility = View.GONE
                    }
                }
            } else if (this.binding is ItemTextOnlyPrivateMessageReceiverBinding) {
                this.binding.apply {
                    tvText.text = pmw.id.toString() + " " + pmw.text
                    val createdAt = pmw.createdAt
                    if (createdAt != null) {
                        tvDateTime.text = DateTime.isoTimestampToDateTimeString(createdAt)
                    }
                    if (pmw.isTextEdited == true) {
                        tvIsTextEdited.apply {
                            visibility = View.VISIBLE
                            text = "Edited"
                        }
                    } else {
                        tvIsTextEdited.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pm = this.itemsList[position]
        return if (pm.id == null) {
            return this.viewTypeLoader
        } else {
            if (pm.senderId == this.currentUserId) {
                this.viewTypeSender
            } else {
                this.viewTypeReceiver
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = if (viewType == this.viewTypeSender) {
            ItemTextOnlyPrivateMessageSenderBinding.inflate(inflater, parent, false)
        } else if (viewType == this.viewTypeReceiver) {
            ItemTextOnlyPrivateMessageReceiverBinding.inflate(inflater, parent, false)
        } else {
            ItemListLoadingBinding.inflate(inflater, parent, false)
        }

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(this.itemsList[position])
    }

    override fun getItemCount(): Int {
        return this.itemsList.size
    }

    fun setItems(newItemsList: List<PrivateMessage>) {
        this.hideItemLoader()

        val diffUtil = PrivateMessagesDiffUtil(
            this.itemsList,
            newItemsList
        )
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.itemsList = newItemsList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun insertItemAt(position: Int, pmw: PrivateMessage) {
        this.hideItemLoader()

        this.itemsList.add(position, pmw)
        this.notifyItemInserted(position)
    }

    fun insertItemAtFirst(pmw: PrivateMessage) {
        this.insertItemAt(0, pmw)
    }

    fun updateItemAt(position: Int, pmw: PrivateMessage) {
        this.hideItemLoader()

        this.itemsList[position] = pmw
        this.notifyItemChanged(position)
    }

    fun removeItemAt(position: Int) {
        this.hideItemLoader()

        this.itemsList.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun showItemLoader() {
        if (!this.isItemLoaderVisible) {
            this.isItemLoaderVisible = true
            this.itemsList.add(PrivateMessage())
            this.notifyItemInserted(this.itemsList.lastIndex)
        }
    }

    fun hideItemLoader() {
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