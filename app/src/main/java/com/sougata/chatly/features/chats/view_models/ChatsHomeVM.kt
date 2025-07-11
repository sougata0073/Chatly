package com.sougata.chatly.features.chats.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.repositories.ChatsRepository
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject

class ChatsHomeVM : ViewModel() {

    private val limit: Long = 20L
    private var offset: Long = 0L

    private val _chatsList = MutableLiveData<TaskResult<List<PrivateChat>>>()
    val chatsList: LiveData<TaskResult<List<PrivateChat>>> = this._chatsList

    private val chatsRepo = ChatsRepository()

    init {
        this.loadPrivateChats()
//        this.realtimeTest()
    }

    fun loadPrivateChats() {

        this._chatsList.value = TaskResult(null, TaskStatus.STARTED, "Task Started")

        this.viewModelScope.launch {
            val list = chatsRepo.getPrivateChats(limit = limit, offset = offset)
            _chatsList.value = list
            offset += limit
        }
    }

    private fun realtimeTest() {
        this.viewModelScope.launch {
            try {
                val supabase = MySupabaseClient.getInstance()
//                supabase.realtime.setAuth()

                val channel = supabase.channel("private_chats:2af956bb-4047-4e41-bc8f-3e8b32765ac5"){
                    // Setting private is very important to receive updates from a private channel else will not work
                    this.isPrivate = true
                }
                // use event = "*" to listen all events
                val changeFlow = channel.broadcastFlow<JsonObject>(event = "INSERT")
                changeFlow.onEach {
                    Log.d("TAGRT", "Change: $it")
                }.launchIn(viewModelScope)

                channel.status.onEach {
                    Log.d("TAGRT", "Status: $it")
                }.launchIn(viewModelScope)

                channel.subscribe()

            } catch (e: Exception) {
                Log.d("TAGRT", "Realtime exception: $e")
            }
        }
    }


}