package com.sougata.chatly.data.repositories

import android.util.Log
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.SearchedUser
import com.sougata.chatly.data.models.SearchedUserDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscoverRepository {

    private val supabase = MySupabaseClient.getInstance()
    private val db = this.supabase.postgrest

    suspend fun searchUsers(searchedUserDto: SearchedUserDto): TaskResult<List<SearchedUser>> =
        withContext(Dispatchers.IO) {
            try {

                val list = db.rpc("search_users", searchedUserDto).decodeList<SearchedUser>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun sendFriendRequest(receiverId: String): TaskResult<Unit> =
        withContext(Dispatchers.IO) {
            try {

                val data = db.rpc(
                    "send_friend_request",
                    mapOf("_receiver_id" to receiverId)
                ).data

                return@withContext TaskResult(null, TaskStatus.COMPLETED, data)

            } catch (e: Exception) {
                Log.d("TAGFF", e.message.toString())
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

}