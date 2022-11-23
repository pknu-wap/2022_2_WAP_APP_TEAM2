package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.repository.interfaces.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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

        fun getINSTANCE() = INSTANCE!!
    }

    override suspend fun findUsers(email: String) = suspendCoroutine<MutableSet<UserDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
        collection.whereGreaterThanOrEqualTo("email", email)
                .whereLessThan("email", "${email}z")
                .get().addOnCompleteListener {
                    continuation.resume(convertToUserDTOSet(it.result.documents, true))
                }
    }

    override suspend fun getUsers(ids: MutableList<String>) = suspendCoroutine<MutableList<UserDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
        collection.whereIn(FieldPath.documentId(), ids)
                .get().addOnCompleteListener {
                    continuation.resume(convertToUserDTOSet(it.result.documents, false).toMutableList())
                }
    }

    override suspend fun getUser(userId: String) = suspendCoroutine<UserDTO?> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
        collection.document(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val dto = it.result.toObject<UserDTO>()!!
                dto.id = it.result.id
                continuation.resume(dto)
            } else {
                continuation.resume(null)
            }
        }
    }

    override suspend fun removeCalcRoomId(roomId: String) {
        fireStore.collection(FireStoreNames.users.name).document(auth.currentUser!!.uid)
                .update("myCalcRoomIds", FieldValue.arrayRemove(roomId))
    }

    private fun convertToUserDTOSet(documents: List<DocumentSnapshot>, ignoreMyId: Boolean): MutableSet<UserDTO> {
        val dtoSet = mutableSetOf<UserDTO>()
        var dto: UserDTO? = null
        val myId = auth.currentUser?.uid

        for (v in documents) {
            if (ignoreMyId) {
                if (myId != v.id) {
                    dto = v.toObject<UserDTO>()!!
                    dto.id = v.id
                    dtoSet.add(dto)
                }
            } else {
                dto = v.toObject<UserDTO>()!!
                dto.id = v.id
                dtoSet.add(dto)
            }

        }
        return dtoSet
    }


}