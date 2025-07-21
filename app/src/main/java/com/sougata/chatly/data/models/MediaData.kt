package com.sougata.chatly.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MediaData(
    @SerialName("bucket_id") val bucketId: String? = null,
    @SerialName("path") val path: String? = null,
    @SerialName("type") val type: String? = null
): Parcelable