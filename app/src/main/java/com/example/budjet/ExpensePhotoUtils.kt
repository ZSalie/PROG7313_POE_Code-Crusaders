package com.example.budjet

object ExpensePhotoUtils {

    fun hasPhoto(photoPath: String?): Boolean {
        return !photoPath.isNullOrBlank()
    }
}