package com.sougata.chatly.data.repositories

import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sougata.chatly.common.TaskComplete
import com.sougata.chatly.common.TaskStatus

class AuthenticationRepository {
    private val auth = Firebase.auth

    fun loginWithGoogle(googleIdToken: String, onComplete: (TaskComplete) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

        this.auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete(
                    TaskComplete(
                        TaskStatus.COMPLETED,
                        "Login Successful"
                    )
                )
            } else {
                onComplete(
                    TaskComplete(
                        TaskStatus.FAILED,
                        it.exception?.message.toString()
                    )
                )
            }
        }
    }
}