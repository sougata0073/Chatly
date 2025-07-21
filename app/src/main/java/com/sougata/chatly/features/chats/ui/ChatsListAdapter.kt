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
import com.sougata.chatly.data.repositories.StorageRepository
import com.sougata.chatly.databinding.ItemChatBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.Animations
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.Files
import com.sougata.chatly.util.RecyclerViewUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsListAdapter(
    override var itemsList: MutableList<PrivateChat>
) : RecyclerViewUtil<Long, PrivateChat, ChatsListAdapter.MyViewHolder>(itemsList) {

    private val storageRepo = StorageRepository()

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pc: PrivateChat) {
            if (this.binding !is ItemChatBinding) {
                return
            }

            CoroutineScope(Dispatchers.Main).launch {
                val mediaData = pc.otherUser?.profileImageData
                if (mediaData != null) {
                    val profileImageFile =
                        storageRepo.downloadMedia(mediaData, binding.root.context)

                    Glide.with(binding.root)
                        .load(profileImageFile)
                        .error(R.drawable.ic_user_placeholder)
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(binding.ivProfileImage)
                } else {
                    Glide.with(binding.root)
                        .load(R.drawable.ic_user_placeholder)
                        .into(binding.ivProfileImage)
                }
            }

            this.binding.apply {
                tvName.text = pc.otherUser?.name
                tvLastMessage.text =
                    pc.lastMessage?.id.toString() + " " + pc.lastMessage?.text
                val lastMessageTime = pc.lastMessage?.createdAt
                if (lastMessageTime == null) {
                    tvLastMessageDate.text = ""
                    tvLastMessageTime.text = ""
                } else {
                    tvLastMessageDate.text =
                        DateTime.isoTimestampToDateString(lastMessageTime)
                    tvLastMessageTime.text =
                        DateTime.isoTimestampToTimeString(lastMessageTime)
                }
                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putParcelable(Keys.PRIVATE_CHAT, pc)
                    }
                    it.findNavController()
                        .navigate(R.id.action_chatsHomeFragment_to_privateMessagesFragment, bundle)
                }
                ivProfileImage.setOnClickListener {
                    val user = pc.otherUser
                    val bundle = Bundle().apply {
                        putString(Keys.USER_ID, user?.id)
                    }
                    it.findNavController()
                        .navigate(R.id.userProfileFragment, bundle, Animations.FRAGMENT_SLIDE)
                }
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