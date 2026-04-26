package com.example.budjet

import org.junit.Assert.*
import org.junit.Test

class SignupValidationTest {


    @Test
    fun emptyFields_returnsFalse() {
        // checks that signup fails  when all input fields are empty.
        val result = ValidationUtils.isValidSignup("", "", "")
        // Expected result = false
        assertFalse(result)
    }

    @Test
    fun validInput_returnsTrue() {
        //checks that signup passes when all fields are populated
        val result = ValidationUtils.isValidSignup(
            "john",
            "john@gmail.com",
            "1234"
        )
        // Expected result = true
        assertTrue(result)
    }
}