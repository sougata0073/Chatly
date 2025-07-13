package com.sougata.chatly.data.repositories

import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.PrivateChat
import com.sougata.chatly.data.models.PrivateChatDto
import com.sougata.chatly.data.models.PrivateMessage
import com.sougata.chatly.data.models.PrivateMessageGetDto
import com.sougata.chatly.data.models.PrivateMessagePostDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
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

    suspend fun getPrivateChat(privateChatId: Long): TaskResult<PrivateChat> =
        withContext(Dispatchers.IO) {

            try {
                val privateChat = db.rpc(
                    "get_private_chat",
                    mapOf("_private_chat_id" to privateChatId)
                )
                    .decodeSingle<PrivateChat>()

                return@withContext TaskResult(privateChat, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }

        }

    suspend fun getPrivateMessages(privateMessageGetDto: PrivateMessageGetDto): TaskResult<List<PrivateMessage>> =
        withContext(Dispatchers.IO) {
            try {

                val list = db.rpc("get_private_chat_messages", privateMessageGetDto)
                    .decodeList<PrivateMessage>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun insertPrivateMessage(privateMessagePostDto: PrivateMessagePostDto): TaskResult<PrivateMessage> =
        withContext(Dispatchers.IO) {
            try {

                val pmw = db.rpc("insert_private_message", privateMessagePostDto)
                    .decodeSingle<PrivateMessage>()

                return@withContext TaskResult(pmw, TaskStatus.COMPLETED, "Task Completed")

            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }
}