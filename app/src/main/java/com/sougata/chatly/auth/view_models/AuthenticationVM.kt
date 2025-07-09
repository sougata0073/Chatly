package com.sougata.chatly.auth.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.repositories.AuthenticationRepository
import kotlinx.coroutines.launch

class AuthenticationVM(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthenticationRepository()

    private val _loginWithGoogle = MutableLiveData<TaskResult<User>>()
    val loginWithGoogle: LiveData<TaskResult<User>> = this._loginWithGoogle

    fun loginWithGoogle(googleIdToken: String, rawNonce: String) {

        this._loginWithGoogle.value = TaskResult(null, TaskStatus.STARTED, "Login Started")

        this.viewModelScope.launch {
            _loginWithGoogle.value = authRepo.loginWithGoogle(googleIdToken, rawNonce)
        }
    }

}