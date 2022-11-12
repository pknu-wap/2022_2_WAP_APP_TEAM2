package com.example.wapapp2.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.interfaces.AppAccountRepository
import java.util.prefs.Preferences

class AppAccountRepositoryImpl : AppAccountRepository {
    override fun saveCurrentUser(userDTO: UserDTO) {
        TODO("Not yet implemented")
    }
}