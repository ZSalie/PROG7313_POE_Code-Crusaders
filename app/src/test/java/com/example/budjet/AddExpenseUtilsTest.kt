package com.example.budjet

import org.junit.Assert.*
import org.junit.Test

class AddExpenseUtilsTest {

    @Test
    fun validFields_returnsTrue() {
        assertTrue(
            AddExpenseUtils.hasRequiredFields(
                category = "Groceries",
                amount = "50",
                description = "Milk",
                date = "2026-04-28"
            )
        )
    }

    @Test
    fun missingFields_returnsFalse() {
        assertFalse(
            AddExpenseUtils.hasRequiredFields(
                category = "",
                amount = "50",
                description = "Milk",
                date = "2026-04-28"
            )
        )
    }

    @Test
    fun validAmount_returnsDouble() {
        assertEquals(
            45.99,
            AddExpenseUtils.parseAmount("45.99")!!,
            0.01
        )
    }

    @Test
    fun invalidAmount_returnsNull() {
        assertNull(
            AddExpenseUtils.parseAmount("abc")
        )
    }
}