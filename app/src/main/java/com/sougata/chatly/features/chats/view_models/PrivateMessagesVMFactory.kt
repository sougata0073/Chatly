package com.sougata.chatly.features.chats.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sougata.chatly.data.models.PrivateChat

@Suppress("UNCHECKED_CAST")
class PrivateMessagesVMFactory(private val privateChat: PrivateChat): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(PrivateMessagesVM::class.java) ->
                PrivateMessagesVM(privateChat) as T

            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }

}