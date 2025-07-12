package com.sougata.chatly.data.repositories

import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateChatDto
import com.sougata.chatly.data.models.PrivateMessageDto
import com.sougata.chatly.data.models.PrivateMessageWrapper
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ChatsRepository {
    private val supabase = MySupabaseClient.getInstance()
    private val db = this.supabase.postgrest

    suspend fun getPrivateChats(privateChatDto: PrivateChatDto): TaskResult<List<PrivateChat>> =
        withContext(Dispatchers.IO) {

            try {
                val list = db.rpc("get_private_chats", privateChatDto).decodeList<PrivateChat>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }

        }

    suspend fun getPrivateMessages(privateMessageDto: PrivateMessageDto): TaskResult<List<PrivateMessageWrapper>> =
        withContext(Dispatchers.IO) {
            try {

                val list = db.rpc("get_private_chat_messages", privateMessageDto)
                    .decodeList<PrivateMessageWrapper>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }
}