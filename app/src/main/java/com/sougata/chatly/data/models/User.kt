package com.sougata.chatly.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var gender: String? = null,
    var dob: Timestamp? = null,
    var bio: String? = null,
    var location: @RawValue GeoPoint? = null,
    var profileImageUrl: String? = null,
    var timestamp: Timestamp? = null
): Parcelable
