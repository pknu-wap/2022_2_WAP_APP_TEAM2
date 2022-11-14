package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.interfaces.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl : UserRepository {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: UserRepositoryImpl? = null

        fun initialize() {
            INSTANCE = UserRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE
    }

    override suspend fun findUsers(email: String) = suspendCoroutine<MutableSet<UserDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
        collection.whereGreaterThanOrEqualTo("email", email)
                .whereLessThan("email", "${email}z")
                .get().addOnCompleteListener {
                    continuation.resume(convertToUserDTOSet(it.result.documents))
                }
    }

    override suspend fun getUser(userId: String) = suspendCoroutine<UserDTO?> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
        val data = collection.document(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(it.result.toObject<UserDTO>())
            } else {
                continuation.resume(null)
            }
        }
    }


    private fun convertToUserDTOSet(documents: List<DocumentSnapshot>): MutableSet<UserDTO> {
        val dtoSet = mutableSetOf<UserDTO>()
        var dto: UserDTO? = null
        val myId = auth.currentUser?.uid

        for (v in documents) {
            if (myId != v.id) {
                dto = v.toObject<UserDTO>()!!
                dto.id = v.id
                dtoSet.add(dto)
            }
        }
        return dtoSet
    }

}