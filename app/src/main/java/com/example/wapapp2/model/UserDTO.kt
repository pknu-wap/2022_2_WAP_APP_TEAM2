package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class UserDTO(
        @get:Exclude
        var id: String,
        @PropertyName("email")
        var email: String,
        @PropertyName("gender")
        var gender: String,
        @PropertyName("imgUri")
        var imgUri: String,
        @PropertyName("name")
        var name: String
)
