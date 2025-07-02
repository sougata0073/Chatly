package com.sougata.chatly.auth.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sougata.chatly.common.TaskComplete
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.AuthenticationRepository

class AuthenticationVM(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthenticationRepository()

    private val _loginWithGoogle = MutableLiveData<TaskComplete>()
    val loginWithGoogle: LiveData<TaskComplete> = this._loginWithGoogle

    fun loginWithGoogle(googleIdToken: String) {

        this._loginWithGoogle.value = TaskComplete(TaskStatus.STARTED, "Login Started")

        this.authRepo.loginWithGoogle(googleIdToken) { taskComplete ->
            this._loginWithGoogle.value = taskComplete
        }
    }

}