package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendRequestSent(
    val id: String? = null,
    val friendRequestId: String? = null,
    val timestamp: Timestamp? = null
) : Parcelable
