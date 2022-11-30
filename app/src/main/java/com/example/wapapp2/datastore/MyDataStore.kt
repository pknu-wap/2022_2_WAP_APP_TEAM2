package com.example.wapapp2.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wapapp2.firebase.FireStoreNames
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class MyDataStore private constructor(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: MyDataStore? = null

        fun initialize(context: Context) {
            if (INSTANCE == null)
                INSTANCE = MyDataStore(context)
        }

        fun getINSTANCE() = INSTANCE!!
    }

    private val Context.dataStore by preferencesDataStore(name = "fcm_token")

    private val TOKEN_KEY = stringPreferencesKey("token")


    suspend fun checkFcmToken() {
        context.dataStore.data.collect { value ->
            val savedToken = value[TOKEN_KEY]

            FirebaseMessaging.getInstance()
                    .token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentToken = task.result

                            if (savedToken != currentToken) {

                            }
                        }
                    }
        }
    }


    suspend fun updateFcmToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }

        FirebaseAuth.getInstance().currentUser?.apply {
            FirebaseFirestore.getInstance()
                    .collection(FireStoreNames.users.name)
                    .document(uid).update(mapOf("fcmToken" to token))
        }

    }

    suspend fun removeFcmToken() {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
        }
    }
}