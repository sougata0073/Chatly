package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.databinding.FragmentUserProfileBinding
import com.sougata.chatly.features.discover.view_models.UserProfileVM
import com.sougata.chatly.features.discover.view_models.UserProfileVMFactory
import com.sougata.chatly.util.DecoratedViews

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: UserProfileVM

    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.userId = this.requireArguments().getString(Keys.USER_ID).orEmpty()

        this.vm = ViewModelProvider(
            this,
            UserProfileVMFactory(this.userId)
        )[UserProfileVM::class.java]

        this.binding.toolBar.setNavigationOnClickListener {
            this.findNavController().popBackStack()
        }

        this.registerObservers()
    }

    private fun registerObservers() {
        this.vm.user.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                val user = it.result!!

                Glide.with(this)
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .error(R.drawable.ic_user_placeholder)
                    .into(binding.ivProfileImage)

                this.binding.apply {
                    tvName.text = user.name
                    tvContact.text = user.email ?: user.phoneNumber ?: "No Contact Available"
                }

            } else if (it.taskStatus == TaskStatus.FAILED) {
                DecoratedViews.showSnackBar(
                    requireView(),
                    null,
                    it.message,
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

}