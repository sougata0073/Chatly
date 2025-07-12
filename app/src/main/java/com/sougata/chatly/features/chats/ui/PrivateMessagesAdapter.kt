package com.sougata.chatly.features.chats.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateMessageWrapper
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.databinding.ItemTextOnlyPrivateMessageReceiverBinding
import com.sougata.chatly.databinding.ItemTextOnlyPrivateMessageSenderBinding
import com.sougata.chatly.features.chats.util.PrivateMessageWrappersDiffUtil
import com.sougata.chatly.util.DateTime
import io.github.jan.supabase.auth.auth

class PrivateMessagesAdapter(
    private var itemsList: MutableList<PrivateMessageWrapper>
) : RecyclerView.Adapter<PrivateMessagesAdapter.MyViewHolder>() {

    private val currentUserId = MySupabaseClient.getInstance().auth.currentUserOrNull()!!.id

    private val viewTypeSender = 1
    private val viewTypeReceiver = 2
    private val viewTypeLoader = 3

    private var isItemLoaderVisible = false

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pmw: PrivateMessageWrapper) {
            if (this.binding is ItemTextOnlyPrivateMessageSenderBinding) {
                this.binding.apply {
                    tvText.text = pmw.privateMessage?.text
                    val createdAt = pmw.privateMessage?.createdAt
                    if (createdAt != null) {
                        tvDateTime.text = DateTime.isoTimestampToDateTimeString(createdAt)
                    }
                }
            } else if (this.binding is ItemTextOnlyPrivateMessageReceiverBinding) {
                this.binding.apply {
                    tvText.text = pmw.privateMessage?.text
                    val createdAt = pmw.privateMessage?.createdAt
                    if (createdAt != null) {
                        tvDateTime.text = DateTime.isoTimestampToDateTimeString(createdAt)
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
            if (pm.sender?.id == this.currentUserId) {
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

    fun setItems(newItemsList: List<PrivateMessageWrapper>) {
        this.hideItemLoader()

        val diffUtil = PrivateMessageWrappersDiffUtil(
            this.itemsList,
            newItemsList
        )
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.itemsList = newItemsList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun insertItemAtFirst(pmw: PrivateMessageWrapper) {
        this.itemsList.add(0, pmw)
        this.notifyItemInserted(0)
    }

    fun showItemLoader() {
        if (!this.isItemLoaderVisible) {
            this.isItemLoaderVisible = true
            this.itemsList.add(PrivateMessageWrapper())
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