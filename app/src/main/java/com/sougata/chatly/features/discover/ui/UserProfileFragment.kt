package com.sougata.chatly.features.discover.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.R
import com.sougata.chatly.common.Keys
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.repositories.StorageRepository
import com.sougata.chatly.databinding.FragmentUserProfileBinding
import com.sougata.chatly.features.discover.view_models.UserProfileVM
import com.sougata.chatly.features.discover.view_models.UserProfileVMFactory
import com.sougata.chatly.util.DecoratedViews
import com.sougata.chatly.util.Files
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: UserProfileVM

    private lateinit var userId: String

    private val storageRepo = StorageRepository()

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

                this.lifecycleScope.launch {
                    val mediaData = user.profileImageData
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