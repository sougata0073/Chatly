package com.sougata.chatly.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.sougata.chatly.data.MySupabaseClient
import com.sougata.chatly.data.models.MediaData
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import io.github.jan.supabase.storage.downloadAuthenticatedTo
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Files {

    suspend fun compressImage(
        uncompressedImageUri: Uri,
        maxSize: Long,
        context: Context
    ): ByteArray =
        withContext(Dispatchers.Default) {
            val cr = context.contentResolver
            val fileInputStream = cr.openInputStream(uncompressedImageUri)
            val uncompressedImageFile = File(context.cacheDir, "temp_pf")

            fileInputStream?.copyTo(uncompressedImageFile.outputStream())
            fileInputStream?.close()

            val compressedImageFile = Compressor.compress(context, uncompressedImageFile) {
                this.size(maxSize)
            }

            val compressedImageByteArray = compressedImageFile.readBytes()
            compressedImageFile.delete()
            uncompressedImageFile.delete()

            return@withContext compressedImageByteArray
        }
}