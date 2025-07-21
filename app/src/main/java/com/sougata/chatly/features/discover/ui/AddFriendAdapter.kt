package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.FriendRequestStatus
import com.sougata.chatly.common.Keys
import com.sougata.chatly.data.models.SearchedUser
import com.sougata.chatly.data.repositories.StorageRepository
import com.sougata.chatly.databinding.ItemAddFriendBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.Animations
import com.sougata.chatly.util.Files
import com.sougata.chatly.util.RecyclerViewUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFriendAdapter(
    override var itemsList: MutableList<SearchedUser>,
    private val sendFriendRequest: (receiver: SearchedUser) -> Unit
) : RecyclerViewUtil<String, SearchedUser, AddFriendAdapter.MyViewHolder>(itemsList) {

    private val storageRepo = StorageRepository()

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(su: SearchedUser) {
            if (this.binding !is ItemAddFriendBinding) {
                return
            }

            CoroutineScope(Dispatchers.Main).launch {
                val mediaData = su.user?.profileImageData
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
                            sendFriendRequest(su)
                        }
                    }
                }

                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(Keys.USER_ID, su.id)
                    }
                    it.findNavController()
                        .navigate(R.id.userProfileFragment, bundle, Animations.FRAGMENT_SLIDE)
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