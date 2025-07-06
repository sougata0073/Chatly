package com.sougata.chatly.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.snackbar.Snackbar
import com.sougata.chatly.MainActivity
import com.sougata.chatly.R
import com.sougata.chatly.auth.view_models.AuthenticationVM
import com.sougata.chatly.common.Keys
import com.sougata.chatly.common.Messages
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.databinding.FragmentAuthenticationBinding
import com.sougata.chatly.util.DecoratedViews
import kotlinx.coroutines.launch

class AuthenticationFragment : Fragment() {

    private lateinit var _binding: FragmentAuthenticationBinding
    private val binding get() = _binding

    private lateinit var authVM: AuthenticationVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.viewBlocker.progressBar.setIndicatorColor(requireActivity().getColor(R.color.bw))

        this.authVM = ViewModelProvider(this)[AuthenticationVM::class.java]

        this.setupContinueWithGoogleButton()
        this.registerObservers()
    }

    private fun setupContinueWithGoogleButton() {
        this.binding.btnContinueWithGoogle.setOnClickListener {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(this.resources.getString(R.string.server_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(this.requireContext())

            this.lifecycleScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = requireContext(),
                    )
                    val credential = result.credential
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken

                    authVM.loginWithGoogle(googleIdToken)

                } catch (e: GetCredentialException) {
                    DecoratedViews.showSnackBar(
                        requireView(),
                        requireView(),
                        e.message.toString(),
                        Snackbar.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun registerObservers() {
        this.authVM.loginWithGoogle.observe(this.viewLifecycleOwner) {
            if (it.taskStatus == TaskStatus.STARTED) {

                this.binding.viewBlocker.parentLayout.visibility = View.VISIBLE

            } else if (it.taskStatus == TaskStatus.COMPLETED) {

                this.binding.viewBlocker.parentLayout.visibility = View.GONE

                if (it.message == Messages.NEW_USER) {
                    val bundle = Bundle().apply {
                        putParcelable(Keys.USER, it.result)
                    }

                    this.findNavController().navigate(
                        R.id.action_authenticationFragment_to_userRegisterDetailsFragment,
                        bundle
                    )
                } else if (it.message == Messages.OLD_USER) {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                }

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
}