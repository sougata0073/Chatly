package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendRequestReceivedReference(
    var id: String? = null,
    var friendRequestId: String? = null,
    var timestamp: Timestamp? = null
) : Parcelable
