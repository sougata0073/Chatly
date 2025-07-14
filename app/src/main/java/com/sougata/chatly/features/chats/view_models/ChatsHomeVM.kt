package com.sougata.chatly.features.chats.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.DbOperations
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateChatDto
import com.sougata.chatly.data.models.PrivateMessage
import com.sougata.chatly.data.repositories.ChatsRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ChatsHomeVM : ViewModel() {

    private val limit: Long = 20L
    private var offset: Long = 0L

    private val _chatsList = MutableLiveData<TaskResult<MutableList<PrivateChat>>>()
    val chatsList: LiveData<TaskResult<MutableList<PrivateChat>>> = this._chatsList

    private val _messageReceived = MutableLiveData<Pair<Int, PrivateChat>>()
    val messageReceived: LiveData<Pair<Int, PrivateChat>> = this._messageReceived

    private val _messageUpdated = MutableLiveData<Pair<Int, PrivateChat>>()
    val messageUpdated: LiveData<Pair<Int, PrivateChat>> = this._messageUpdated

    private val _messageDeleted = MutableLiveData<Pair<Int, PrivateChat?>>()
    val messageDeleted: LiveData<Pair<Int, PrivateChat?>> = this._messageDeleted

    private val chatsRepo = ChatsRepository()
    private val supabase = MySupabaseClient.getInstance()
    private val currentUserId = supabase.auth.currentUserOrNull()!!.id
    private val channelId = "private_chats:${this.currentUserId}"
    private val messageChannel = supabase.channel(channelId) {
        this.isPrivate = true
    }

    var noMoreChats = false
    var isSearchViewExpanded = false

    init {
        this._chatsList.value = TaskResult(mutableListOf(), TaskStatus.NONE, "Initialising")

        this.loadPrivateChats()
        this.subscribeToPrivateMessagesChannel()
    }

    fun loadPrivateChats() {
        val prevList = this._chatsList.value?.result
        this._chatsList.value = TaskResult(prevList, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val result = chatsRepo.getPrivateChats(PrivateChatDto(limit, offset))

            val newList = result.result

            if (newList != null) {
                if (newList.isEmpty()) {
                    noMoreChats = true
                }
                prevList?.addAll(result.result)
            }
            _chatsList.value = TaskResult(prevList, result.taskStatus, result.message)
            offset += limit
        }
    }

    private fun subscribeToPrivateMessagesChannel() {

        val broadcastFlow = this.messageChannel.broadcastFlow<JsonObject>("*")
        broadcastFlow.onEach {
            this.onMessageBroadcast(it)
        }.launchIn(this.viewModelScope)

        this.viewModelScope.launch {
            try {
                messageChannel.subscribe()
            } catch (e: Exception) {
                Log.d("TAGZZ", e.message.toString())
            }
        }
    }

    private fun onMessageBroadcast(jo: JsonObject) {
        val operation = jo["operation"].toString()

        when (operation) {
            DbOperations.INSERT -> {
                val newPm = Json.decodeFromString<PrivateMessage>(jo["record"].toString())
                onMessageReceived(newPm)
            }

            DbOperations.UPDATE -> {
                val newPm = Json.decodeFromString<PrivateMessage>(jo["record"].toString())
                onMessageUpdate(newPm)
            }

            DbOperations.DELETE -> {
                val oldPm = Json.decodeFromString<PrivateMessage>(jo["old_record"].toString())
                onMessageDelete(oldPm)
            }
        }
    }

    private fun onMessageReceived(newPm: PrivateMessage) {
        val prevList = _chatsList.value?.result!!
        val indexOfChat = prevList.indexOfFirst { it.id == newPm.privateChatId }

        if (indexOfChat != -1) {
            val removedChat = prevList.removeAt(indexOfChat)
            removedChat.lastMessage = newPm
            prevList.add(0, removedChat)

            this._messageReceived.value = indexOfChat to removedChat
            this._messageReceived.value = null
        } else {
            this.viewModelScope.launch {
                val result = chatsRepo.getPrivateChat(newPm.privateChatId!!)
                if (result.taskStatus == TaskStatus.COMPLETED) {
                    val fetchedChat = result.result!!
                    prevList.add(0, fetchedChat)

                    _messageReceived.value = indexOfChat to fetchedChat
                    _messageReceived.value = null

                    offset++
                }
            }
        }
    }

    private fun onMessageUpdate(newPm: PrivateMessage) {
        val prevList = _chatsList.value?.result!!
        val indexOfChat = prevList.indexOfFirst { it.id == newPm.privateChatId }

        if (indexOfChat != -1 && prevList[indexOfChat].lastMessage?.id == newPm.id) {
            prevList[indexOfChat].lastMessage = newPm

            this._messageUpdated.value = indexOfChat to prevList[indexOfChat]
            this._messageUpdated.value = null
        }
    }

    private fun onMessageDelete(oldPm: PrivateMessage) {
        val prevList = _chatsList.value?.result!!
        val indexOfChat = prevList.indexOfFirst { it.id == oldPm.privateChatId }

        if (indexOfChat != -1 && prevList[indexOfChat].lastMessage?.id == oldPm.id) {
            this.viewModelScope.launch {
                val result = chatsRepo.getPrivateChat(oldPm.privateChatId!!)
                if (result.taskStatus == TaskStatus.COMPLETED) {
                    prevList[indexOfChat] = result.result!!

                    _messageDeleted.value = indexOfChat to prevList[indexOfChat]
                    _messageDeleted.value = null
                } else if (result.taskStatus == TaskStatus.FAILED) {
                    _messageDeleted.value = indexOfChat to null
                    _messageDeleted.value = null
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        // Don't use viewmodel scope or unsubscription will get cancelled
        CoroutineScope(Dispatchers.IO).launch {
            messageChannel.unsubscribe()
        }
    }
}