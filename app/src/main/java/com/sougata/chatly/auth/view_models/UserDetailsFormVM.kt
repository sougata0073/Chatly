package com.sougata.chatly.auth.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.repositories.AuthenticationRepository
import kotlinx.coroutines.launch

class UserDetailsFormVM(private val user: User) : ViewModel() {

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    var dob: Timestamp? = null
    val dobString = MutableLiveData<String>()
    val bio = MutableLiveData<String>()
    val profileImageUrl = MutableLiveData<String>()

    private val authRepo = AuthenticationRepository()

    init {
        this.profileImageUrl.value = this.user.profileImageUrl
        this.name.value = this.user.name.orEmpty()
        this.email.value = this.user.email.orEmpty()
    }

    private var _updateUserDetails = MutableLiveData<TaskResult<Unit>>()
    val updateUserDetails: LiveData<TaskResult<Unit>> = this._updateUserDetails

    fun updateUserDetails(user: User) {
        this._updateUserDetails.value = TaskResult(null, TaskStatus.STARTED, "")

        this.viewModelScope.launch {
            _updateUserDetails.value = authRepo.updateUserDetails(user)
        }
    }

}