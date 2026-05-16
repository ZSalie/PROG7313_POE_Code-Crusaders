package com.example.budjet.data

import com.google.firebase.firestore.DocumentId

data class Goal(
    @DocumentId
    var goalId: String = "",

    var userId: String = "",
    var month: String = "",

    var minGoal: Double = 0.0,
    var maxGoal: Double = 0.0
)