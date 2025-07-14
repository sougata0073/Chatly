package com.sougata.chatly.features.chats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.databinding.ItemChatBinding
import com.sougata.chatly.features.chats.util.PrivateChatsDiffUtil
import com.sougata.chatly.util.DateTime

class ChatsListAdapter(
    private var itemsList: MutableList<PrivateChat>
) : RecyclerView.Adapter<ChatsListAdapter.MyViewHolder>() {

    private var isItemLoaderVisible = false

    inner class MyViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pc: PrivateChat) {
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<ItemChatBinding>(
            inflater,
            R.layout.item_chat,
            parent,
            false
        )

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

    fun setItems(newItemsList: List<PrivateChat>) {
        this.hideItemLoader()

        val diffUtil = PrivateChatsDiffUtil(this.itemsList, newItemsList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.itemsList = newItemsList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun insertItemAt(position: Int, pc: PrivateChat) {
        this.hideItemLoader()

        this.itemsList.add(position, pc)
        this.notifyItemInserted(position)
    }

    fun insertItemAtFirst(pc: PrivateChat) {
        this.insertItemAt(0, pc)
    }

    fun updateItemAt(position: Int, pc: PrivateChat) {
        this.hideItemLoader()

        this.itemsList[position] = pc
        this.notifyItemChanged(position)
    }

    fun removeItemAt(position: Int) {
        this.hideItemLoader()

        this.itemsList.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun showItemLoader() {
        if (this.isItemLoaderVisible) {
            return
        }
        this.isItemLoaderVisible = true
        this.itemsList.add(PrivateChat())
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