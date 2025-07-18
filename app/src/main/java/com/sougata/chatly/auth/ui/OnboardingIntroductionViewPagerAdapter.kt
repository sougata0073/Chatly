package com.sougata.chatly.auth.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingIntroductionViewPagerAdapter(
    private val itemsList: List<Triple<Int, String, String>>,
    private val fragmentActivity: FragmentActivity
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {

        val data = this.itemsList[position]

        return OnboardingIntroductionContentFragment.newInstance(
            data.first,
            data.second,
            data.third
        )
    }

    override fun getItemCount(): Int {
        return this.itemsList.size
    }
}