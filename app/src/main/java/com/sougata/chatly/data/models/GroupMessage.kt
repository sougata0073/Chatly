package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupMessage(
    var id: String? = null,
    var senderId: String? = null,
    var text: String? = null,
    var media: Map<String, String>? = null,
    var edited: Boolean? = null,
    var timestamp: Timestamp? = null
): Parcelable
