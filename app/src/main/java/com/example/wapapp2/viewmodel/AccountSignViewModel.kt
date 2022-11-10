package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.UserDTO
import com.google.firebase.auth.FirebaseUser

class AccountSignViewModel : ViewModel() {
    var isSignIn = false
    var signInUser: FirebaseUser? = null

}