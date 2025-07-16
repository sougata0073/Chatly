package com.sougata.chatly.features.chats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.databinding.ItemChatBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.RecyclerViewUtil

class ChatsListAdapter(
    override var itemsList: MutableList<PrivateChat>
) : RecyclerViewUtil<Long, PrivateChat, ChatsListAdapter.MyViewHolder>(itemsList) {

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pc: PrivateChat) {
            if (this.binding !is ItemChatBinding) {
                return
            }

            Glide.with(this.binding.root)
                .load(pc.otherUser?.profileImageUrl)
                .error(R.drawable.ic_user_placeholder)
                .placeholder(R.drawable.ic_user_placeholder)
                .into(this.binding.ivProfileImage)

            this.binding.tvName.text = pc.otherUser?.name
            this.binding.tvLastMessage.text =
                pc.lastMessage?.id.toString() + " " + pc.lastMessage?.text

            val lastMessageTime = pc.lastMessage?.createdAt
            if (lastMessageTime == null) {
                this.binding.tvLastMessageDate.text = ""
                this.binding.tvLastMessageTime.text = ""
            } else {
                this.binding.tvLastMessageDate.text =
                    DateTime.isoTimestampToDateString(lastMessageTime)
                this.binding.tvLastMessageTime.text =
                    DateTime.isoTimestampToTimeString(lastMessageTime)
            }

            this.binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putParcelable(Keys.PRIVATE_CHAT, pc)
                }
                it.findNavController()
                    .navigate(R.id.action_chatsHomeFragment_to_privateMessagesFragment, bundle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pc = this.itemsList[position]

        return if (pc.id == null) {
            this.viewTypeLoader
        } else {
            this.viewTypeNormal
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = if (viewType == this.viewTypeNormal) {
            ItemChatBinding.inflate(inflater, parent, false)
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