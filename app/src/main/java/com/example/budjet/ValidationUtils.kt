package com.example.budjet

object ValidationUtils {

    fun isValidSignup(
        name: String,
        email: String,
        password: String
    ): Boolean {

        return name.isNotBlank() &&
                email.isNotBlank() &&
                password.length >= 4
    }
}