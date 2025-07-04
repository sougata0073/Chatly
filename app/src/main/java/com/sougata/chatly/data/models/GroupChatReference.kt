package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupChatReference(
    var id: String? = null,
    var groupChatId: String? = null,
    var timestamp: Timestamp? = null
): Parcelable
