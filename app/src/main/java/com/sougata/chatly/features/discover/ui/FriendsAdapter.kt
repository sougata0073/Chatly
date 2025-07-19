package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.data.models.User
import com.sougata.chatly.databinding.ItemFriendBinding
import com.sougata.chatly.databinding.ItemListLoadingBinding
import com.sougata.chatly.util.Animations
import com.sougata.chatly.util.RecyclerViewUtil

class FriendsAdapter(
    override var itemsList: MutableList<User>
) : RecyclerViewUtil<String, User, FriendsAdapter.MyViewHolder>(itemsList) {

    private val viewTypeNormal = 1
    private val viewTypeLoader = 2

    inner class MyViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(u: User) {
            if (this.binding !is ItemFriendBinding) {
                return
            }

            Glide.with(this.binding.root)
                .load(u.profileImageUrl)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(this.binding.ivProfileImage)

            this.binding.apply {
                tvName.text = u.name
                tvContact.text = u.email ?: u.phoneNumber ?: "No contact available"

                root.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(Keys.USER_ID, u.id)
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
            ItemFriendBinding.inflate(inflater, parent, false)
        } else {
            ItemListLoadingBinding.inflate(inflater, parent, false)
        }

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        return holder.bind(this.itemsList[position])
    }

}