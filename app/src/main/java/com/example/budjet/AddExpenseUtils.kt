package com.example.budjet

object AddExpenseUtils {

    fun hasRequiredFields(
        category: String,
        amount: String,
        description: String,
        date: String
    ): Boolean {
        return category.isNotBlank() &&
                amount.isNotBlank() &&
                description.isNotBlank() &&
                date.isNotBlank()
    }

    fun parseAmount(amount: String): Double? {
        return amount.toDoubleOrNull()
    }
}