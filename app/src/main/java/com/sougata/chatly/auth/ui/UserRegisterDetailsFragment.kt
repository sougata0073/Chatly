package com.sougata.chatly.auth.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sougata.chatly.R
import com.sougata.chatly.databinding.FragmentUserRegisterDetailsBinding

class UserRegisterDetailsFragment : Fragment() {

    private var _binding: FragmentUserRegisterDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentUserRegisterDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.viewBlocker.progressBar.trackColor = requireActivity().getColor(R.color.bw)
    }
}