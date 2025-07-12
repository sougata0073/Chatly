package com.sougata.chatly.features.chats.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateMessageDto
import com.sougata.chatly.data.models.PrivateMessageWrapper
import com.sougata.chatly.data.repositories.ChatsRepository
import kotlinx.coroutines.launch

class PrivateMessagesVM(private val privateChat: PrivateChat) : ViewModel() {

    private val limit: Long = 30L
    private var offset: Long = 0L

    private val _messagesList = MutableLiveData<TaskResult<MutableList<PrivateMessageWrapper>>>()
    val messagesList: LiveData<TaskResult<MutableList<PrivateMessageWrapper>>> = this._messagesList

    private val chatsRepo = ChatsRepository()

    var noMoreMessages = false

    init {
        this._messagesList.value = TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")
        this.loadPrivateMessages()
    }

    fun loadPrivateMessages() {
        val prevList = _messagesList.value?.result
        this._messagesList.value = TaskResult(prevList, TaskStatus.STARTED, "Task Started")

        val privateChatId = this.privateChat.id

        if (privateChatId == null) {
            this._messagesList.value =
                TaskResult(prevList, TaskStatus.FAILED, "Private Chat Id is null")
            return
        }

        this.viewModelScope.launch {
            val result =
                chatsRepo.getPrivateMessages(PrivateMessageDto(privateChatId, limit, offset))

            val newList = result.result

            if (newList != null) {
                if(newList.isEmpty()) {
                    noMoreMessages = true
                }
                prevList?.addAll(result.result)
            }

            _messagesList.value = TaskResult(prevList, result.taskStatus, result.message)
            offset += limit
        }
    }

}