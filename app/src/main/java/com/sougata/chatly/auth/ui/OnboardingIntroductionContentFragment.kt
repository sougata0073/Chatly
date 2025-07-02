package com.sougata.chatly.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sougata.chatly.databinding.FragmentOnboardingIntroductionContentBinding

private const val ARG_MAIN_IMAGE_ID: String = "mainImageId"
private const val ARG_HEADING: String = "heading"
private const val ARG_SUB_HEADING: String = "subHeading"

class OnboardingIntroductionContentFragment : Fragment() {

    private lateinit var _binding: FragmentOnboardingIntroductionContentBinding
    private val binding get() = this._binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentOnboardingIntroductionContentBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainImageId = requireArguments().getInt(ARG_MAIN_IMAGE_ID)
        val heading = requireArguments().getString(ARG_HEADING)
        val subHeading = requireArguments().getString(ARG_SUB_HEADING)

        this.binding.ivMainImage.setImageResource(mainImageId)
        this.binding.tvHeading.text = heading
        this.binding.tvSubHeading.text = subHeading
    }

    companion object {
        @JvmStatic
        fun newInstance(mainImageId: Int, heading: String, subHeading: String) =
            OnboardingIntroductionContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MAIN_IMAGE_ID, mainImageId)
                    putString(ARG_HEADING, heading)
                    putString(ARG_SUB_HEADING, subHeading)
                }
            }
    }
}