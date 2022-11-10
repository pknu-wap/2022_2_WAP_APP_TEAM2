package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class UserDTO(
        @Exclude
        val id: String,
        @PropertyName("email")
        val email: String,
        @PropertyName("gender")
        val gender: String,
        @PropertyName("imgUri")
        val imgUri: String,
        @PropertyName("name")
        val name: String
)
