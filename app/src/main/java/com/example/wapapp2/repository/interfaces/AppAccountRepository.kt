package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.UserDTO

interface AppAccountRepository {
    fun saveCurrentUser(userDTO: UserDTO)
}