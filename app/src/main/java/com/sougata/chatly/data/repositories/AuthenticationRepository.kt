package com.sougata.chatly.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sougata.chatly.common.FirestoreCollections
import com.sougata.chatly.common.Messages
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.models.User
import com.sougata.chatly.data.util.DataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val usersCol = db.collection(FirestoreCollections.USERS)

    suspend fun loginWithGoogle(googleIdToken: String): TaskResult<User> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->

                val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

                auth.signInWithCredential(credential).addOnCompleteListener { loginTask ->
                    if (loginTask.isSuccessful) {
                        val currentUser = auth.currentUser

                        if (currentUser == null) {
                            continuation.resume(
                                TaskResult(
                                    null,
                                    TaskStatus.FAILED,
                                    "Login failed, user is null"
                                )
                            )
                        }

                        currentUser!!

                        usersCol.document(currentUser.uid).get()
                            .addOnCompleteListener { userFetchingTask ->
                                if (userFetchingTask.isSuccessful) {
                                    // User already exists in database so return and no need to set details
                                    if (userFetchingTask.result.exists()) {
                                        val fetchedUser =
                                            userFetchingTask.result.toObject(User::class.java)

                                        // Returning
                                        continuation.resume(
                                            TaskResult(
                                                fetchedUser,
                                                TaskStatus.COMPLETED,
                                                Messages.OLD_USER
                                            )
                                        )
                                    } else {
                                        // New user so set details
                                        val user = User(
                                            id = currentUser.uid,
                                            name = currentUser.displayName,
                                            email = currentUser.email,
                                            phoneNumber = currentUser.phoneNumber,
                                            gender = null,
                                            dob = null,
                                            bio = null,
                                            location = null,
                                            profileImageUrl = currentUser.photoUrl.toString(),
                                            timestamp = Timestamp.now()
                                        )

                                        val map = user.toMap().toMutableMap()

                                        val searchKeyWords =
                                            DataGenerator.generateUserSearchKeywords(currentUser.displayName.orEmpty())

                                        map.put("searchKeywords", searchKeyWords)

                                        usersCol.document(currentUser.uid).set(map)
                                            .addOnCompleteListener { dataEntryTask ->
                                                if (dataEntryTask.isSuccessful) {
                                                    // Successfully set details for the new user
                                                    continuation.resume(
                                                        TaskResult(
                                                            user,
                                                            TaskStatus.COMPLETED,
                                                            Messages.NEW_USER
                                                        )
                                                    )
                                                } else {
                                                    // Failed to set details of the new user
                                                    continuation.resume(
                                                        TaskResult(
                                                            null,
                                                            TaskStatus.FAILED,
                                                            dataEntryTask.exception?.message.toString()
                                                        )
                                                    )
                                                }
                                            }

                                    }
                                } else {
                                    // Failed to fetch old user details
                                    continuation.resume(
                                        TaskResult(
                                            null,
                                            TaskStatus.FAILED,
                                            userFetchingTask.exception?.message.toString()
                                        )
                                    )
                                }
                            }
                    } else {
                        // Failed to login
                        continuation.resume(
                            TaskResult(
                                null,
                                TaskStatus.FAILED,
                                loginTask.exception?.message.toString()
                            )
                        )
                    }
                }
            }
        }

    suspend fun updateUserDetails(user: User): TaskResult<Unit> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val userDoc = usersCol.document(user.id!!)
            userDoc.firestore.runTransaction { transaction ->

                val map = user.toMap().toMutableMap()
                val previousName = transaction.get(userDoc).getString(User::name.name)
                if (previousName != user.name) {
                    val searchKeywords =
                        DataGenerator.generateUserSearchKeywords(user.name.orEmpty())
                    map.put("searchKeywords", searchKeywords)
                }
                transaction.update(userDoc, map)

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(
                        TaskResult(
                            null,
                            TaskStatus.COMPLETED,
                            "User details updated successfully"
                        )
                    )
                } else {
                    continuation.resume(
                        TaskResult(
                            null,
                            TaskStatus.FAILED,
                            task.exception?.message.toString()
                        )
                    )
                }
            }
        }
    }
}