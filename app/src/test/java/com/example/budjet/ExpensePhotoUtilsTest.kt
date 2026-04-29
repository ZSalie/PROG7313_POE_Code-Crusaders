package com.example.budjet

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpensePhotoUtilsTest {

    // Tests that an expense is treated as having a photo when a saved photo path exists.
    @Test
    fun hasPhoto_returnsTrue_whenPhotoPathExists() {
        val result = ExpensePhotoUtils.hasPhoto(
            "/storage/emulated/0/Android/data/com.example.budjet/files/Pictures/EXP_20260428.jpg"
        )

        assertTrue(result)
    }

    // Tests that an expense is treated as having no photo when the photo path is missing.
    @Test
    fun hasPhoto_returnsFalse_whenPhotoPathIsNull() {
        val result = ExpensePhotoUtils.hasPhoto(null)

        assertFalse(result)
    }
}