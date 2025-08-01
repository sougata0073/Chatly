package com.sougata.chatly.auth.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.MainActivity
import com.sougata.chatly.R
import com.sougata.chatly.auth.view_models.UserDetailsFormVM
import com.sougata.chatly.auth.view_models.UserDetailsFormVMFactory
import com.sougata.chatly.common.Keys
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.databinding.FragmentUserDetailsFormBinding
import com.sougata.chatly.util.DateTime
import com.sougata.chatly.util.DecoratedViews
import com.sougata.chatly.util.Validator

class UserDetailsFormFragment : Fragment() {

    private var _binding: FragmentUserDetailsFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm: UserDetailsFormVM

    private lateinit var previousUser: User

    private val pickMedia =
        registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {

                vm.profileImageUri = uri

                Glide.with(this)
                    .load(uri)
                    .error(R.drawable.ic_user_placeholder)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(this.binding.ivProfileImage)

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentUserDetailsFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.viewBlocker.progressBar.setIndicatorColor(requireActivity().getColor(R.color.bw))

        this.previousUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(Keys.USER, User::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(Keys.USER)
        }!!

        this.vm = ViewModelProvider(
            this,
            UserDetailsFormVMFactory(this.previousUser, requireActivity().application)
        )[UserDetailsFormVM::class.java]

        this.binding.btnEditProfileImage.setOnClickListener {
            this.pickMedia.launch(
                PickVisualMediaRequest(PickVisualMedia.ImageOnly)
            )
        }

        this.binding.vm = this.vm
        this.binding.lifecycleOwner = this.viewLifecycleOwner

        this.setupObservers()
        this.setupDobCalendarButton()
        this.setupSaveButton()
    }

    private fun setupObservers() {
        this.vm.updateUserDetails.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

                this.binding.viewBlocker.parentLayout.visibility = View.VISIBLE

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.binding.viewBlocker.parentLayout.visibility = View.GONE
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finishAffinity()

            } else if (it.taskStatus == TaskStatus.FAILED) {

                this.binding.viewBlocker.parentLayout.visibility = View.GONE
                DecoratedViews.showSnackBar(
                    requireView(),
                    requireView(),
                    it.message,
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

    private fun setupSaveButton() {
        this.binding.btnSave.setOnClickListener {
            val nameString = this.vm.name.value
            val emailString = this.vm.email.value
            val phoneNumberString = this.vm.phoneNumber.value
            val genderString = this.vm.gender.value
            val dob = this.vm.dobMillis
            val bioString = this.vm.bio.value

            if (nameString.isNullOrEmpty()) {
                this.binding.nameLayout.error = "Name can't be empty"
                return@setOnClickListener
            }
            val emailResult = Validator.validateEmail(emailString.orEmpty())
            if (!emailString.isNullOrEmpty() && !emailResult.isValid) {
                this.binding.emailLayout.error = emailResult.message
                return@setOnClickListener
            }
            val phoneNumberResult = Validator.validatePhoneNumber(phoneNumberString.orEmpty())
            if (!phoneNumberString.isNullOrEmpty() && !phoneNumberResult.isValid) {
                this.binding.phoneNumberLayout.error = phoneNumberResult.message
                return@setOnClickListener
            }

            val updatedUser = User(
                name = nameString,
                email = emailString,
                phoneNumber = phoneNumberString,
                gender = genderString,
                dob = if (dob == null) null else DateTime.millisToISOTimestampString(dob),
                bio = bioString,
                location = null,
                profileImageData = null
            )

            this.vm.updateUserDetails(updatedUser)
        }
    }

    private fun setupDobCalendarButton() {
        this.binding.dobLayout.setEndIconOnClickListener { view ->
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.addOnPositiveButtonClickListener { timeInMillis ->
                this.vm.dobMillis = timeInMillis
                this.vm.dobString.value = DateTime.millisToDateString(timeInMillis)
            }

            datePicker.show(this.parentFragmentManager, "DatePicker")
        }
    }
}