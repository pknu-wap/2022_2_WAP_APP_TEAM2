package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.repository.interfaces.MyBankAccountRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MyBankAccountRepositoryImpl private constructor() : MyBankAccountRepository {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: MyBankAccountRepositoryImpl? = null

        fun initialize() {
            INSTANCE = MyBankAccountRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE!!
    }


    override suspend fun addMyBankAccount(bankAccountDTO: BankAccountDTO) = suspendCoroutine<Boolean> { continuation ->
        val document = fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.bankAccounts.name)
                .document()

        document.set(bankAccountDTO).addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
    }

    override suspend fun modifyMyBankAccount(documentId: String,
                                             map: MutableMap<String, Any?>) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.bankAccounts.name)

        collection.document(documentId)
                .update(map)
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
                }
    }

    override suspend fun getMyBankAccounts() = suspendCoroutine<MutableList<BankAccountDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.bankAccounts.name)

        collection.orderBy("bankId", Query.Direction.ASCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val dtoList = mutableListOf<BankAccountDTO>()

                for (v in it.result.documents) {
                    dtoList.add(v.toObject<BankAccountDTO>()!!)
                }
                continuation.resume(dtoList)
            } else
                continuation.resume(mutableListOf<BankAccountDTO>())
        }
    }

    override suspend fun removeMyBankAccount(id: String) = suspendCoroutine<Boolean> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
                .document(auth.currentUser?.uid!!)
                .collection(FireStoreNames.bankAccounts.name)

        collection.document(id)
                .delete()
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
                }
    }
}