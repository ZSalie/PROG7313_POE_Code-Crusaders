package com.example.budjet.data

import com.google.firebase.firestore.DocumentId

data class Expense(
    @DocumentId
    var expenseId: String = "",

    var userId: String = "",
    var category: String = "",

    var amount: Double = 0.0,
    var description: String = "",

    var date: String = "",
    var startTime: String = "",
    var endTime: String = "",

    var photoPath: String? = null
)