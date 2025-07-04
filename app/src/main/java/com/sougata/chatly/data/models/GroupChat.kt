package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupChat(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var timestamp: Timestamp? = null
): Parcelable
