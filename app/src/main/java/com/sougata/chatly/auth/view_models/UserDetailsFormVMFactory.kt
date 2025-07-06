package com.sougata.chatly.auth.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sougata.chatly.data.models.User

@Suppress("UNCHECKED_CAST")
class UserDetailsFormVMFactory(private val user: User) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserDetailsFormVM::class.java) -> UserDetailsFormVM(this.user) as T
            else -> throw IllegalArgumentException("Unknown viewmodel")
        }
    }

}