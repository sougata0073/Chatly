package com.sougata.chatly.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sougata.chatly.MainActivity
import com.sougata.chatly.R
import com.sougata.chatly.databinding.FragmentOnboardingIntroductionBinding

class OnboardingIntroductionFragment : Fragment() {

    private var _binding: FragmentOnboardingIntroductionBinding? = null
    private val binding get() = this._binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentOnboardingIntroductionBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setupViewPager()
        this.setupButtons()

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        this._binding = null
    }

    private fun setupViewPager() {
        val viewPagerItemsList = listOf(
            Triple(
                R.drawable.onboarding_intro_1,
                "Group Chatting",
                "Connect with multiple members in group chat"
            ),
            Triple(
                R.drawable.onboarding_intro_2,
                "Video And Voice Calls",
                "Instantly connect via video and voice calls"
            ),
            Triple(
                R.drawable.onboarding_intro_3,
                "Message Encryption",
                "Ensure privacy with encrypted messages"
            ),
            Triple(
                R.drawable.onboarding_intro_4,
                "Cross-Platform Compatibility",
                "Access chats on any device seamlessly"
            )
        )

        this.binding.vpContent.adapter = ViewPagerAdapter(viewPagerItemsList, requireActivity())
        this.binding.dotsIndicator.attachTo(this.binding.vpContent)
    }

    private fun setupButtons() {
        this.binding.btnNext.setOnClickListener {
            val lastItemIndex = this.binding.vpContent.adapter!!.itemCount - 1
            val currentItemIndex = this.binding.vpContent.currentItem

            if (currentItemIndex == lastItemIndex) {
                this.goToAuthenticationFragment()
            } else {
                this.binding.vpContent.currentItem++
            }
        }

        this.binding.btnGetStarted.setOnClickListener {
            this.goToAuthenticationFragment()
        }
        this.binding.btnSkip.setOnClickListener {
            this.goToAuthenticationFragment()
        }
    }


    private fun goToAuthenticationFragment() {
        this.findNavController()
            .navigate(R.id.userRegisterDetailsFragment)
    }

}