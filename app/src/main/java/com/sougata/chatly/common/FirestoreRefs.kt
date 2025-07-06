package com.sougata.chatly.common

object FirestoreCollections {

    const val CHATS = "chats"
    const val FRIEND_REQUESTS = "friendRequests"
    const val GROUP_CHATS = "groupChats"
    const val USERS = "users"

    object Chat {
        const val MESSAGES = "messages"
    }

    object GroupChat {
        const val MEMBERS = "members"
        const val MESSAGES = "messages"
    }

    object User {
        const val CHATS = "chats"
        const val FRIEND_REQUESTS_RECEIVED = "friendRequestsReceived"
        const val FRIEND_REQUESTS_SENT = "friendRequestsSent"
        const val FRIENDS = "friends"
        const val GROUP_CHATS = "groupChats"
    }
}