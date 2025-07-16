package com.sougata.chatly.features.discover.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.FriendRequestStatus
import com.sougata.chatly.data.models.SearchedUser
import com.sougata.chatly.data.models.User
import com.sougata.chatly.databinding.ItemAddFriendBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.RecyclerViewUtil

class AddFriendAdapter(
    override var itemsList: MutableList<SearchedUser>,
    private val sendFriendRequest: (receiver: SearchedUser, binding: ItemAddFriendBinding) -> Unit
) : RecyclerViewUtil<String, SearchedUser, AddFriendAdapter.MyViewHolder>(itemsList) {

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(su: SearchedUser) {
            if (this.binding !is ItemAddFriendBinding) {
                return
            }

            Glide.with(this.binding.root)
                .load(su.user?.profileImageUrl)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(this.binding.ivProfileImage)

            this.binding.apply {
                tvName.text = su.user?.name
                tvContact.text = su.user?.email ?: su.user?.phoneNumber ?: "No contact available"

                if (su.isFriends == true || su.friendRequestStatus == FriendRequestStatus.ACCEPTED) {
                    ivFriendShipStatus.apply {
                        this.background = null
                        this.setImageResource(R.drawable.ic_friends)
                        setOnClickListener(null)
                    }
                } else if (su.friendRequestStatus == FriendRequestStatus.PENDING) {
                    ivFriendShipStatus.apply {
                        this.background = null
                        this.setImageResource(R.drawable.ic_check)
                        setOnClickListener(null)
                    }
                } else {
                    ivFriendShipStatus.apply {
                        this.isClickable = true
                        this.isFocusable = true
                        this.setImageResource(R.drawable.ic_add_friend)
                        setOnClickListener {
                            sendFriendRequest(su, binding)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val af = this.itemsList[position]

        return if (af.id == null) {
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
            ItemAddFriendBinding.inflate(inflater, parent, false)
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