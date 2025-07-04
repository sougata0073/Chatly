package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    var id: String? = null,
    var userId1: String? = null,
    var userId2: String? = null,
    var timestamp: Timestamp? = null
): Parcelable
