package com.sougata.chatly.auth.view_models

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.repositories.AuthenticationRepository
import com.sougata.chatly.data.repositories.StorageRepository
import com.sougata.chatly.util.DateTime
import kotlinx.coroutines.launch

class UserDetailsFormVM(
    private val user: User,
    private val application: Application
) : AndroidViewModel(application) {

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    var dobMillis: Long? = null
    val dobString = MutableLiveData<String>()
    val bio = MutableLiveData<String>()
    var profileImageUri: Uri? = null

    private val authRepo = AuthenticationRepository()
    private val storageRepo = StorageRepository()

    init {
        this.name.value = this.user.name.orEmpty()
        this.email.value = this.user.email.orEmpty()
        this.phoneNumber.value = this.user.phoneNumber.orEmpty()
        this.gender.value = this.user.gender.orEmpty()

        val dobIsoString = this.user.dob
        if (dobIsoString != null) {
            this.dobString.value = DateTime.isoTimestampToDateString(dobIsoString)
        }

        this.bio.value = this.user.bio.orEmpty()
    }

    private var _updateUserDetails = MutableLiveData<TaskResult<Unit>>()
    val updateUserDetails: LiveData<TaskResult<Unit>> = this._updateUserDetails

    fun updateUserDetails(user: User) {
        this._updateUserDetails.value = TaskResult(null, TaskStatus.STARTED, "")

        this.viewModelScope.launch {
            val pfUri = profileImageUri
            var isUploadSuccessful = true

            if (pfUri != null) {
                val uploadResult = storageRepo.uploadProfileImage(pfUri, application)
                if (uploadResult.taskStatus == TaskStatus.COMPLETED) {
                    user.profileImageData = uploadResult.result
                } else if (uploadResult.taskStatus == TaskStatus.FAILED) {
                    isUploadSuccessful = false
                    _updateUserDetails.value =
                        TaskResult(null, TaskStatus.FAILED, uploadResult.message)
                }
            }
            if (isUploadSuccessful) {
                _updateUserDetails.value = authRepo.updateUserDetails(user)
            }
        }
    }

}