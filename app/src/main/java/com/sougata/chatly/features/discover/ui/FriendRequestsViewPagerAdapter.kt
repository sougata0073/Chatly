package com.sougata.chatly.features.discover.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendRequestsViewPagerAdapter(
    private val fragmentsList: List<Fragment>,
    private val fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return this.fragmentsList[position]
    }

    override fun getItemCount(): Int {
        return this.fragmentsList.size
    }
}