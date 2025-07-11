package com.sougata.chatly.data.repositories

import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateChatDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatsRepository {
    private val supabase = MySupabaseClient.getInstance()
    private val db = this.supabase.postgrest
    private val auth = this.supabase.auth

    suspend fun getPrivateChats(limit: Long = 20, offset: Long): TaskResult<List<PrivateChat>> =
        withContext(Dispatchers.IO) {

            try {
                val arg = PrivateChatDto(limit, offset)

                val list = db.rpc("get_private_chats", arg).decodeList<PrivateChat>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }

        }
}