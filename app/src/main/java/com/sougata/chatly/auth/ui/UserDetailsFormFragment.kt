package com.sougata.chatly.auth.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
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
            UserDetailsFormVMFactory(this.previousUser)
        )[UserDetailsFormVM::class.java]

        this.binding.vm = this.vm
        this.binding.lifecycleOwner = this.viewLifecycleOwner

        this.initializeUI()
        this.setupObservers()
        this.setupDobCalendarButton()
        this.setupSaveButton()
    }

    private fun initializeUI() {
        Glide.with(requireContext())
            .load(this.vm.profileImageUrl.value)
            .error(R.drawable.ic_user_placeholder)
            .placeholder(R.drawable.ic_user_placeholder)
            .into(this.binding.ivProfileImage)
    }

    private fun setupObservers() {
        this.vm.updateUserDetails.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

                this.binding.viewBlocker.progressBar.visibility = View.VISIBLE

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.binding.viewBlocker.progressBar.visibility = View.GONE
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finishAffinity()

            } else if (it.taskStatus == TaskStatus.FAILED) {

                this.binding.viewBlocker.progressBar.visibility = View.GONE
                DecoratedViews.showSnackBar(
                    requireView(),
                    requireView(),
                    it.message,
                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG
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
            val dob = this.vm.dob
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
                id = this.previousUser.id,
                name = nameString,
                email = emailString,
                phoneNumber = phoneNumberString,
                gender = genderString,
                dob = dob,
                bio = bioString,
                location = null,
                profileImageUrl = this.previousUser.profileImageUrl,
                timestamp = this.previousUser.timestamp
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
                this.vm.dob = DateTime.getTimeStamp(timeInMillis)
                this.vm.dobString.value = DateTime.getDateString(timeInMillis)
            }

            datePicker.show(this.parentFragmentManager, "DatePicker")
        }
    }
}