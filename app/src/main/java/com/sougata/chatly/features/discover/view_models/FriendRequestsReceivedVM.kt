package com.sougata.chatly.features.discover.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.FriendRequestReceived
import com.sougata.chatly.data.models.LimitOffsetDto
import com.sougata.chatly.data.models.ServerResponse
import com.sougata.chatly.data.repositories.DiscoverRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendRequestsReceivedVM : ViewModel() {

    private val limit: Long = 20L
    private var offset: Long = 0L

    private val _friendRequestsReceivedList =
        MutableLiveData<TaskResult<MutableList<FriendRequestReceived>>>()
    val friendRequestsReceivedList: LiveData<TaskResult<MutableList<FriendRequestReceived>>> =
        this._friendRequestsReceivedList

    private val _acceptFriendRequest =
        MutableLiveData<TaskResult<Pair<FriendRequestReceived, ServerResponse?>>>()
    val acceptFriendRequest: LiveData<TaskResult<Pair<FriendRequestReceived, ServerResponse?>>> =
        this._acceptFriendRequest

    private val _rejectFriendRequest =
        MutableLiveData<TaskResult<Pair<FriendRequestReceived, ServerResponse?>>>()
    val rejectFriendRequest: LiveData<TaskResult<Pair<FriendRequestReceived, ServerResponse?>>> =
        this._rejectFriendRequest

    val repo = DiscoverRepository()

    var isFirstTimeListLoading = true
    var noMoreFriendRequestsReceived = false

    init {
        this._friendRequestsReceivedList.value =
            TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")
        this.loadFriendRequestsReceived()
    }

    fun loadFriendRequestsReceived() {
        val prevList = this._friendRequestsReceivedList.value?.result
        this._friendRequestsReceivedList.value =
            TaskResult(prevList, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            if(isFirstTimeListLoading) {
                delay(200)
                isFirstTimeListLoading = false
            }
            val result = repo.getFriendRequestsReceived(LimitOffsetDto(limit, offset))

            val newList = result.result

            if (newList != null) {
                if (newList.isEmpty()) {
                    noMoreFriendRequestsReceived = true
                }
                prevList?.addAll(result.result)
            }

            _friendRequestsReceivedList.value =
                TaskResult(prevList, result.taskStatus, result.message)
            offset += limit
        }
    }

    fun acceptFriendRequest(frr: FriendRequestReceived) {
        this._acceptFriendRequest.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val result = repo.acceptFriendRequest(frr.id!!)
            val pair = frr to result.result
            val response = result.result

            if (response?.isSuccessful == true) {
                val prevList = _friendRequestsReceivedList.value?.result!!
                val removeIndex = prevList.indexOfFirst { it.id == frr.id }
                if(removeIndex != -1) {
                    prevList.removeAt(removeIndex)
                    offset--
                }
            }

            _acceptFriendRequest.value = TaskResult(pair, result.taskStatus, result.message)
        }
    }

    fun rejectFriendRequest(frr: FriendRequestReceived) {
        this._rejectFriendRequest.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val result = repo.rejectFriendRequest(frr.id!!)
            val pair = frr to result.result
            val response = result.result

            if (response?.isSuccessful == true) {
                val prevList = _friendRequestsReceivedList.value?.result!!
                val removeIndex = prevList.indexOfFirst { it.id == frr.id }
                if(removeIndex != -1) {
                    prevList.removeAt(removeIndex)
                    offset--
                }
            }

            _rejectFriendRequest.value = TaskResult(pair, result.taskStatus, result.message)
        }
    }
}