package com.example.wapapp2.repository

import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.main.MyApplication
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.repository.interfaces.BankAccountRepository
import com.example.wapapp2.repository.interfaces.MyBankAccountRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BankAccountRepositoryImpl private constructor() : BankAccountRepository {
    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var INSTANCE: BankAccountRepositoryImpl? = null

        fun initialize() {
            INSTANCE = BankAccountRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE!!
    }

    override suspend fun getBankAccounts(userId: String) = suspendCoroutine<MutableList<BankAccountDTO>> { continuation ->
        val collection = fireStore.collection(FireStoreNames.users.name)
                .document(userId)
                .collection(FireStoreNames.bankAccounts.name)

        collection.orderBy("bankId", Query.Direction.ASCENDING).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val dtoList = mutableListOf<BankAccountDTO>()
                var bankAccountDTO: BankAccountDTO? = null

                for (v in it.result.documents) {
                    bankAccountDTO = v.toObject<BankAccountDTO>()!!
                    bankAccountDTO.bankDTO = MyApplication.BANK_MAPS[bankAccountDTO.bankId]
                    dtoList.add(bankAccountDTO)
                }
                continuation.resume(dtoList)
            } else
                continuation.resume(mutableListOf<BankAccountDTO>())
        }
    }

}