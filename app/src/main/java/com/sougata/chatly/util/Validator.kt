package com.sougata.chatly.util

import android.view.View
import com.sougata.chatly.common.ValidationResult

object Validator {
    private val emailRegex =
        Regex("^[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}\$")

    private val phoneNumberRegex =
        Regex("^(?:\\+?(\\d{1,3})?\\s?)?(?:\\(?(\\d{3})\\)?[-\\s]?)?(\\d{3})[-\\s]?(\\d{4})$")

    fun validateEmail(email: String): ValidationResult {
        if (!this.emailRegex.matches(email)) {
            return ValidationResult(false, "Enter a valid email")
        }
        return ValidationResult(true, "Email is valid")
    }

    fun validatePhoneNumber(phone: String): ValidationResult {
        if (!this.phoneNumberRegex.matches(phone)) {
            return ValidationResult(false, "Enter a valid phone number")
        }
        return ValidationResult(true, "Phone number is valid")
    }
}