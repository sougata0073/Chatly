package com.sougata.chatly.features.discover.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.repositories.DiscoverRepository
import kotlinx.coroutines.launch

class UserProfileVM(private val userId: String) : ViewModel() {

    private val _user = MutableLiveData<TaskResult<User>>()
    val user = this._user

    val repo = DiscoverRepository()

    init {
        this.loadUserDetails()
    }

    fun loadUserDetails() {
        this._user.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val user = repo.getUserDetails(userId)

            _user.value = user
        }
    }
}