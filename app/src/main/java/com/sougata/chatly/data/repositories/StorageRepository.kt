package com.sougata.chatly.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.sougata.chatly.common.MediaType
import com.sougata.chatly.common.TaskResult
import com.sougata.chatly.common.TaskStatus
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.MediaData
import com.sougata.chatly.util.Files
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.downloadAuthenticatedTo
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StorageRepository {

    private val supabase = MySupabaseClient.getInstance()
    private val pfImagesBucket = this.supabase.storage.from("profile-images")
    private val currentUserId = this.supabase.auth.currentUserOrNull()!!.id

    suspend fun uploadProfileImage(
        pfImageUri: Uri,
        context: Context
    ): TaskResult<MediaData> =
        withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(pfImageUri)
                val contentType = ContentType.parse(mimeType ?: "image/jpeg")
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

                val fileName = "$currentUserId.$extension"
                val compressedImageByteArray = Files.compressImage(pfImageUri, 500_000, context)

                val upload = pfImagesBucket.upload(fileName, compressedImageByteArray) {
                    this.upsert = true
                    this.contentType = contentType
                }

                val mediaData = MediaData(
                    bucketId = pfImagesBucket.bucketId,
                    path = upload.path,
                    type = MediaType.IMAGE
                )

                return@withContext TaskResult(
                    mediaData,
                    TaskStatus.COMPLETED,
                    "Profile Image Uploaded"
                )
            } catch (e: Exception) {
                return@withContext TaskResult(null, TaskStatus.FAILED, e.message.toString())
            }
        }

    suspend fun downloadMedia(mediaData: MediaData, context: Context): File =
        withContext(Dispatchers.IO) {
            try {
                val bucketId =
                    mediaData.bucketId ?: throw IllegalArgumentException("Bucket ID is null")
                val parentFileDir = context.getExternalFilesDir(bucketId)
                val path = mediaData.path ?: throw IllegalArgumentException("Path is null")
                val finalPath = parentFileDir?.path + "/" + path
                val file = File(finalPath)

                if (file.exists()) {
                    Log.d("TAGFF", "From cache ${file.path}")
                    return@withContext file
                } else {
                    val bucket = MySupabaseClient.getInstance().storage.from(
                        bucketId
                    )
                    bucket.downloadAuthenticatedTo(path, file)
                    Log.d("TAGFF", "From server")
                    return@withContext file
                }

            } catch (e: Exception) {
                Log.d("TAGFF", "Error: ${e.message}")
                throw e
            }
        }

}