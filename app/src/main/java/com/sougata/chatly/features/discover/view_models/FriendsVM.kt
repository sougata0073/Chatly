package com.sougata.chatly.features.discover.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.LimitOffsetDto
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.repositories.DiscoverRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendsVM: ViewModel() {

    private val limit: Long = 20L
    private var offset: Long = 0L

    private val _friendsList = MutableLiveData<TaskResult<MutableList<User>>>()
    val friendsList: LiveData<TaskResult<MutableList<User>>> = this._friendsList

    private val repo = DiscoverRepository()

    var isFirstTimeListLoading = true
    var noMoreFriends = false

    init {
        this._friendsList.value = TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")

        this.loadFriends()
    }

    fun loadFriends() {
        val prevList = this._friendsList.value?.result!!
        this._friendsList.value = TaskResult(prevList, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            if(isFirstTimeListLoading) {
                delay(200)
                isFirstTimeListLoading = false
            }
            val result = repo.getFriends(LimitOffsetDto(limit, offset))

            val newList = result.result

            if (newList != null) {
                if (newList.isEmpty()) {
                    noMoreFriends = true
                }
                prevList.addAll(result.result)
            }
            _friendsList.value = TaskResult(prevList, result.taskStatus, result.message)
            offset += limit
        }
    }

}