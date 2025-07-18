package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.sougata.chatly.databinding.FragmentFriendRequestsBinding

class FriendRequestsFragment : Fragment() {

    private var _binding: FragmentFriendRequestsBinding? = null
    private val binding get() = this._binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.toolBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }

        this.setupViewPagerAndTabLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }

    private fun setupViewPagerAndTabLayout() {

        val receiveFrag = FriendRequestsReceivedFragment()
        val sentFrag = FriendRequestsSentFragment()

        val fragList = listOf(receiveFrag, sentFrag)

        this.binding.viewPager.adapter =
            FriendRequestsViewPagerAdapter(fragList, this.requireActivity())

        TabLayoutMediator(this.binding.tabLayout, this.binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Received"
                1 -> tab.text = "Sent"
            }
        }.attach()
    }
}