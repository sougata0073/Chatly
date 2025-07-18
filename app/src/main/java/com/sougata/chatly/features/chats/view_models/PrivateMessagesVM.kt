package com.sougata.chatly.features.chats.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.DbOperations
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateMessage
import com.sougata.chatly.data.models.PrivateMessageGetDto
import com.sougata.chatly.data.models.PrivateMessagePostDto
import com.sougata.chatly.data.repositories.ChatsRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class PrivateMessagesVM(private val privateChat: PrivateChat) : ViewModel() {

    private val limit: Long = 30L
    private var offset: Long = 0L

    private val _messagesList = MutableLiveData<TaskResult<MutableList<PrivateMessage>>>()
    val messagesList: LiveData<TaskResult<MutableList<PrivateMessage>>> = this._messagesList

    private val _messageSent = MutableLiveData<TaskResult<Pair<Long, PrivateMessage?>>>()
    val messageSent: LiveData<TaskResult<Pair<Long, PrivateMessage?>>> = this._messageSent

    private val _messageReceived = MutableLiveData<Pair<Int, PrivateMessage>>()
    val messageReceived: LiveData<Pair<Int, PrivateMessage>> = this._messageReceived

    private val _messageUpdated = MutableLiveData<Pair<Int, PrivateMessage>>()
    val messageUpdated: LiveData<Pair<Int, PrivateMessage>> = this._messageUpdated

    private val _messageDeleted = MutableLiveData<Pair<Int, PrivateMessage>>()
    val messageDeleted: LiveData<Pair<Int, PrivateMessage>> = this._messageDeleted

    private val chatsRepo = ChatsRepository()
    private val supabase = MySupabaseClient.getInstance()
    val currentUserId = this.supabase.auth.currentUserOrNull()!!.id

    var isFirstTimeListLoading = true
    var noMoreMessages = false

    init {
        this._messagesList.value = TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")

        this.loadPrivateMessages()
        this.initialiseRealtimeMessageFlow()
    }

    fun loadPrivateMessages() {
        val prevList = _messagesList.value?.result
        this._messagesList.value = TaskResult(prevList, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            if(isFirstTimeListLoading) {
                delay(200)
                isFirstTimeListLoading = false
            }
            val result =
                chatsRepo.getPrivateMessages(PrivateMessageGetDto(privateChat.id!!, limit, offset))

            val newList = result.result

            if (newList != null) {
                if (newList.isEmpty()) {
                    noMoreMessages = true
                }
                prevList?.addAll(result.result)
            }

            _messagesList.value = TaskResult(prevList, result.taskStatus, result.message)
            offset += limit
        }
    }

    fun insertMessage(tempPrivateMessage: PrivateMessage) {

        this._messageSent.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        val receiverId = this.privateChat.otherUser?.id
        val prevList = _messagesList.value?.result!!
        prevList.add(0, tempPrivateMessage)

        if (receiverId == null) {
            this._messageSent.value =
                TaskResult(null, TaskStatus.FAILED, "Receiver Id is null")
            return
        }

        this.viewModelScope.launch {
            val result = chatsRepo.insertPrivateMessage(
                PrivateMessagePostDto(
                    privateChat.id!!,
                    receiverId,
                    tempPrivateMessage.text,
                    tempPrivateMessage.mediaType,
                    tempPrivateMessage.mediaUrl
                )
            )

            val updateIndex = prevList.indexOfFirst { it.id == tempPrivateMessage.id }
            if (result.taskStatus == TaskStatus.COMPLETED) {
                val fetchedPrivateMessages = result.result!!
                prevList[updateIndex] = fetchedPrivateMessages

                _messageSent.value =
                    TaskResult(
                        tempPrivateMessage.id!! to fetchedPrivateMessages,
                        TaskStatus.COMPLETED,
                        result.message
                    )
            } else if (result.taskStatus == TaskStatus.FAILED) {
                prevList.removeAt(updateIndex)
                _messageSent.value =
                    TaskResult(
                        tempPrivateMessage.id!! to null,
                        TaskStatus.FAILED,
                        result.message
                    )
            }
        }
    }

    private fun initialiseRealtimeMessageFlow() {

        val channelId = "private_chats:${this.currentUserId}"
        val messageChannel = this.supabase.channel(channelId) {
            this.isPrivate = true
        }
        val broadcastFlow = messageChannel.broadcastFlow<JsonObject>("*")
        broadcastFlow.onEach {
            this.onMessageBroadcast(it)
        }.launchIn(this.viewModelScope)
    }

    private fun onMessageBroadcast(jo: JsonObject) {
        val operation = jo["operation"].toString()

        when (operation) {
            DbOperations.INSERT -> {
                val newPm = Json.decodeFromString<PrivateMessage>(jo["record"].toString())

                if (newPm.privateChatId == this.privateChat.id) {
                    onMessageReceived(newPm)
                }
            }

            DbOperations.UPDATE -> {
                val newPm = Json.decodeFromString<PrivateMessage>(jo["record"].toString())

                if (newPm.privateChatId == this.privateChat.id) {
                    onMessageUpdate(newPm)
                }
            }

            DbOperations.DELETE -> {
                val oldPm = Json.decodeFromString<PrivateMessage>(jo["old_record"].toString())

                if (oldPm.privateChatId == this.privateChat.id) {
                    onMessageDelete(oldPm)
                }
            }
        }
    }

    private fun onMessageReceived(newPm: PrivateMessage) {
        if (newPm.senderId == this.currentUserId) return

        this._messagesList.value?.result!!.add(0, newPm)
        _messageReceived.value = 0 to newPm

        this.offset++
    }

    private fun onMessageUpdate(newPm: PrivateMessage) {
        val list = this._messagesList.value?.result!!

        val index = list.indexOfFirst { it.id == newPm.id }

        if (index >= 0) {
            list[index] = newPm
            this._messageUpdated.value = index to newPm
        }
    }

    private fun onMessageDelete(oldPm: PrivateMessage) {
        val list = this._messagesList.value?.result!!

        val index = list.indexOfFirst { it.id == oldPm.id }
        val isRemoved = list.removeAt(index) == oldPm

        if (isRemoved) {
            this._messageDeleted.value = index to oldPm

            this.offset--
        }
    }

}