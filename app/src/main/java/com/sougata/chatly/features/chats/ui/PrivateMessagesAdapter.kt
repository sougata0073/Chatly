package com.sougata.chatly.features.chats.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateMessage
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.databinding.ItemPmTextReceiveBinding
import com.sougata.chatly.databinding.ItemPmTextSendBinding
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.RecyclerViewUtil
import io.github.jan.supabase.auth.auth

class PrivateMessagesAdapter(
    override var itemsList: MutableList<PrivateMessage>
) : RecyclerViewUtil<Long, PrivateMessage, PrivateMessagesAdapter.MyViewHolder>(itemsList) {

    private val currentUserId = MySupabaseClient.getInstance().auth.currentUserOrNull()!!.id

    private val viewTypeSender = 1
    private val viewTypeReceiver = 2
    private val viewTypeLoader = 3

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pmw: PrivateMessage) {
            if (this.binding is ItemPmTextSendBinding) {
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
            } else if (this.binding is ItemPmTextReceiveBinding) {
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
            ItemPmTextSendBinding.inflate(inflater, parent, false)
        } else if (viewType == this.viewTypeReceiver) {
            ItemPmTextReceiveBinding.inflate(inflater, parent, false)
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

}