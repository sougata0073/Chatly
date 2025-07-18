package com.sougata.chatly.features.discover.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.FriendRequestSent
import com.sougata.chatly.data.models.LimitOffsetDto
import com.sougata.chatly.data.models.ServerResponse
import com.sougata.chatly.data.repositories.DiscoverRepository
import kotlinx.coroutines.launch

class FriendRequestsSentVM : ViewModel() {

    private val limit: Long = 20L
    private var offset: Long = 0L

    private val _friendRequestsSentList =
        MutableLiveData<TaskResult<MutableList<FriendRequestSent>>>()
    val friendRequestsSentList: LiveData<TaskResult<MutableList<FriendRequestSent>>> get() = this._friendRequestsSentList

    private val _deleteFriendRequest =
        MutableLiveData<TaskResult<Pair<FriendRequestSent, ServerResponse?>>>()
    val deleteFriendRequest: LiveData<TaskResult<Pair<FriendRequestSent, ServerResponse?>>> get() = this._deleteFriendRequest

    val repo = DiscoverRepository()

    var noMoreFriendRequestsSent = false

    init {
        this._friendRequestsSentList.value =
            TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")
        this.loadFriendRequestsSent()
    }

    fun loadFriendRequestsSent() {
        val prevList = this._friendRequestsSentList.value?.result
        this._friendRequestsSentList.value =
            TaskResult(prevList, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val result = repo.getFriendRequestsSent(LimitOffsetDto(limit, offset))

            val newList = result.result

            if (newList != null) {
                if (newList.isEmpty()) {
                    noMoreFriendRequestsSent = true
                }
                prevList?.addAll(result.result)
            }

            _friendRequestsSentList.value = TaskResult(prevList, result.taskStatus, result.message)
            offset += limit
        }
    }

    fun deleteFriendRequest(friendRequestSent: FriendRequestSent) {
        this._deleteFriendRequest.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val result = repo.deleteFriendRequest(friendRequestSent.id!!)
            val pair = friendRequestSent to result.result
            val response = result.result

            if (response?.isSuccessful == true) {
                val prevList = _friendRequestsSentList.value?.result!!
                val removeIndex = prevList.indexOfFirst { it.id == friendRequestSent.id }
                if(removeIndex != -1) {
                    prevList.removeAt(removeIndex)
                    offset--
                }
            }

            _deleteFriendRequest.value =
                TaskResult(pair, result.taskStatus, result.message)
        }
    }
}