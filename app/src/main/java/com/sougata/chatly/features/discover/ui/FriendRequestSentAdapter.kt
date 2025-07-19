package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.data.models.FriendRequestSent
import com.sougata.chatly.databinding.ItemFriendRequestSentBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.Animations
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.RecyclerViewUtil

class FriendRequestSentAdapter(
    override var itemsList: MutableList<FriendRequestSent>,
    private val deleteFriendRequest: (FriendRequestSent) -> Unit
) :
    RecyclerViewUtil<Long, FriendRequestSent, FriendRequestSentAdapter.MyViewHolder>(itemsList) {

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(frs: FriendRequestSent) {

            if (this.binding !is ItemFriendRequestSentBinding) {
                return
            }

            Glide.with(binding.root)
                .load(frs.receivingUser?.profileImageUrl)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(binding.ivProfileImage)

            this.binding.apply {
                tvName.text = frs.receivingUser?.name
                binding.tvSendingDate.text =
                    if (frs.createdAt == null) ""
                    else "Sent on: ${DateTime.isoTimestampToDateString(frs.createdAt)}"

                btnDeleteFriendRequest.setOnClickListener {
                    it.isClickable = false
                    deleteFriendRequest(frs)
                }

                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(Keys.USER_ID, frs.receivingUser?.id)
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
            ItemFriendRequestSentBinding.inflate(inflater, parent, false)
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