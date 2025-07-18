package com.sougata.chatly.data.repositories

import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.FriendRequestReceived
import com.sougata.chatly.data.models.FriendRequestSent
import com.sougata.chatly.data.models.LimitOffsetDto
import com.sougata.chatly.data.models.SearchedUser
import com.sougata.chatly.data.models.SearchedUserDto
import com.sougata.chatly.data.models.ServerResponse
import com.sougata.chatly.data.models.User
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

    suspend fun sendFriendRequest(receiverId: String): TaskResult<ServerResponse> =
        withContext(Dispatchers.IO) {
            try {

                val response = db.rpc(
                    "send_friend_request",
                    mapOf("_receiver_id" to receiverId)
                ).decodeSingle<ServerResponse>()

                return@withContext TaskResult(response, TaskStatus.COMPLETED, "Task Completed")

            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun getFriendRequestsReceived(limitOffsetDto: LimitOffsetDto): TaskResult<List<FriendRequestReceived>> =
        withContext(Dispatchers.IO) {
            try {

                val list = db.rpc("get_friend_requests_received", limitOffsetDto)
                    .decodeList<FriendRequestReceived>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun getFriendRequestsSent(limitOffsetDto: LimitOffsetDto): TaskResult<List<FriendRequestSent>> =
        withContext(Dispatchers.IO) {
            try {

                val list = db.rpc("get_friend_requests_sent", limitOffsetDto)
                    .decodeList<FriendRequestSent>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun acceptFriendRequest(friendRequestId: Long): TaskResult<ServerResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    db.rpc("accept_friend_request", mapOf("_friend_request_id" to friendRequestId))
                        .decodeSingle<ServerResponse>()
                return@withContext TaskResult(response, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun rejectFriendRequest(friendRequestId: Long): TaskResult<ServerResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    db.rpc("reject_friend_request", mapOf("_friend_request_id" to friendRequestId))
                        .decodeSingle<ServerResponse>()
                return@withContext TaskResult(response, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun deleteFriendRequest(friendRequestId: Long): TaskResult<ServerResponse> =
        withContext(Dispatchers.IO) {
            try {

                val serverResponse =
                    db.rpc("delete_friend_request", mapOf("_friend_request_id" to friendRequestId))
                        .decodeSingle<ServerResponse>()

                return@withContext TaskResult(
                    serverResponse,
                    TaskStatus.COMPLETED,
                    "Task Completed"
                )
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun getFriends(limitOffsetDto: LimitOffsetDto): TaskResult<List<User>> =
        withContext(Dispatchers.IO) {
            try {
                val list = db.rpc("get_friends", limitOffsetDto).decodeList<User>()

                return@withContext TaskResult(list, TaskStatus.COMPLETED, "Task Completed")
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

}