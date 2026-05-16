package com.example.budjet.data

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    var userId: String = "",
    var username: String = "",
    var email: String = ""
)