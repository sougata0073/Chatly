package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.data.models.FriendRequestReceived
import com.sougata.chatly.data.repositories.StorageRepository
import com.sougata.chatly.databinding.ItemFriendRequestReceivedBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.Animations
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.Files
import com.sougata.chatly.util.RecyclerViewUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendRequestReceivedAdapter(
    override var itemsList: MutableList<FriendRequestReceived>,
    private val acceptFriendRequest: (FriendRequestReceived) -> Unit,
    private val rejectFriendRequest: (FriendRequestReceived) -> Unit
) : RecyclerViewUtil<Long, FriendRequestReceived, FriendRequestReceivedAdapter.MyViewHolder>(
    itemsList
) {

    private val storageRepo = StorageRepository()

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(frr: FriendRequestReceived) {
            if (this.binding !is ItemFriendRequestReceivedBinding) {
                return
            }

            CoroutineScope(Dispatchers.Main).launch {
                val mediaData = frr.sendingUser?.profileImageData
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
                tvName.text = frr?.id.toString() + " " + frr.sendingUser?.name
                binding.tvSendingDate.text =
                    if (frr.createdAt == null) ""
                    else "Sent on: ${DateTime.isoTimestampToDateString(frr.createdAt)}"

                btnAcceptFriendRequest.setOnClickListener {
                    btnAcceptFriendRequest.isClickable = false
                    btnRejectFriendRequest.isClickable = false
                    acceptFriendRequest(frr)
                }
                btnRejectFriendRequest.setOnClickListener {
                    btnAcceptFriendRequest.isClickable = false
                    btnRejectFriendRequest.isClickable = false
                    rejectFriendRequest(frr)
                }

                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(Keys.USER_ID, frr.sendingUser?.id)
                    }
                    it.findNavController()
                        .navigate(R.id.userProfileFragment, bundle, Animations.FRAGMENT_SLIDE)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (this.itemsList[position].id == null) {
            this.viewTypeLoader
        } else {
            this.viewTypeNormal
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = android.view.LayoutInflater.from(parent.context)

        val binding = if (viewType == this.viewTypeNormal) {
            ItemFriendRequestReceivedBinding.inflate(inflater, parent, false)
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