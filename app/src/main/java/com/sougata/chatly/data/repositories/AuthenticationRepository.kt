package com.sougata.chatly.data.repositories

import android.util.Log
import com.sougata.chatly.common.Messages
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AuthenticationRepository {
    private val supabase = MySupabaseClient.getInstance()
    private val auth = this.supabase.auth
    private val db = this.supabase.postgrest

    suspend fun loginWithGoogle(googleIdToken: String, rawNonce: String): TaskResult<User> =
        withContext(Dispatchers.IO) {
            try {
                auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }
                val currentUser = supabase.auth.currentUserOrNull()
                if (currentUser == null) {
                    return@withContext TaskResult(
                        null,
                        TaskStatus.FAILED,
                        "Login failed, user is null"
                    )
                }

                val result = db.rpc("get_own_details")
                val user = Json.decodeFromString<User>(result.data)

                if (user.isProfileUpdatedOnce == false) {
                    return@withContext TaskResult(user, TaskStatus.COMPLETED, Messages.NEW_USER)
                } else {
                    return@withContext TaskResult(user, TaskStatus.COMPLETED, Messages.OLD_USER)
                }

            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun updateUserDetails(user: User): TaskResult<Unit> = withContext(Dispatchers.IO) {
        try {

            val data = db.rpc(
                "update_own_details", mapOf("_new_user" to user)
            ).data

            return@withContext TaskResult(
                null,
                TaskStatus.COMPLETED,
                data
            )
        } catch (e: Exception) {
            return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
        }
    }
}