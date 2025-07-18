package com.sougata.chatly.features.discover.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.SearchedUser
import com.sougata.chatly.data.models.SearchedUserDto
import com.sougata.chatly.data.models.ServerResponse
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.repositories.DiscoverRepository
import com.sougata.chatly.util.CoolDownTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddFriendVM : ViewModel() {

    private val limit: Long = 20L
    private var offset: Long = 0L

    private var searchJob: Job? = null
    private var prevSearchQuery = ""

    val repo = DiscoverRepository()

    private val _searchedUsersList = MutableLiveData<TaskResult<MutableList<SearchedUser>>>()
    val searchedUsersList: LiveData<TaskResult<MutableList<SearchedUser>>> = this._searchedUsersList

    private val _friendRequestSent = MutableLiveData<TaskResult<Pair<SearchedUser, ServerResponse?>>>()
    val friendRequestSent: LiveData<TaskResult<Pair<SearchedUser, ServerResponse?>>> = this._friendRequestSent

    var noMoreSearchedUsers = false

    private val searchCoolDownTime: Long = 2000L
    private val coolDownTimer = CoolDownTimer(this.searchCoolDownTime)

    init {
        this._searchedUsersList.value = TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")
    }

    fun loadSearchedUsers(searchQuery: String) {

        if (this.coolDownTimer.isTimerFinished()) {
            this.coolDownTimer.resetTimer()
        } else {
            return
        }

        if (this.searchJob?.isActive == true) {
            this.searchJob?.cancel()
        }

        this.reset()

        this.prevSearchQuery = searchQuery

        val prevList = _searchedUsersList.value?.result!!
        this.searchJob = this.viewModelScope.launch {
            val result = repo.searchUsers(SearchedUserDto(searchQuery, limit, offset))

            if (result.taskStatus == TaskStatus.COMPLETED) {
                offset += limit

                val newList = result.result!!
                prevList.addAll(newList)

                _searchedUsersList.value =
                    TaskResult(prevList, TaskStatus.COMPLETED, result.message)
            } else if (result.taskStatus == TaskStatus.FAILED) {
                _searchedUsersList.value = TaskResult(prevList, TaskStatus.FAILED, result.message)
            }
            coolDownTimer.startTimer()
        }
    }

    fun loadMoreSearchedUsers() {
        if (this.prevSearchQuery.isEmpty()) {
            return
        }

        val prevList = _searchedUsersList.value?.result!!
        this.searchJob = this.viewModelScope.launch {
            val result = repo.searchUsers(SearchedUserDto(prevSearchQuery, limit, offset))

            if (result.taskStatus == TaskStatus.COMPLETED) {
                offset += limit

                val newList = result.result!!
                if (newList.isEmpty()) {
                    noMoreSearchedUsers = true
                }

                prevList.addAll(newList)

                _searchedUsersList.value =
                    TaskResult(prevList, TaskStatus.COMPLETED, result.message)
            } else if (result.taskStatus == TaskStatus.FAILED) {
                _searchedUsersList.value = TaskResult(prevList, TaskStatus.FAILED, result.message)
            }
        }
    }

    fun sendFriendRequest(searchedUser: SearchedUser) {
        this._friendRequestSent.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val result = repo.sendFriendRequest(searchedUser.id!!)
            val pair = searchedUser to result.result

            _friendRequestSent.value = TaskResult(pair, result.taskStatus, result.message)
        }
    }

    fun reset() {
        this._searchedUsersList.value?.result!!.clear()
        this.prevSearchQuery = ""
        this.offset = 0L
        this.noMoreSearchedUsers = false
    }

}