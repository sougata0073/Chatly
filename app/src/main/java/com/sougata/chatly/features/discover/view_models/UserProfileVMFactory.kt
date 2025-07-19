package com.sougata.chatly.features.discover.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class UserProfileVMFactory(private val userId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserProfileVM::class.java) ->
                UserProfileVM(this.userId) as T

            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }

}