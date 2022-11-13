package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.BankAccountDTO

interface MyBankAccountRepository {
    suspend fun addMyBankAccount(bankAccountDTO: BankAccountDTO): Boolean
    suspend fun modifyMyBankAccount(documentId: String, map: MutableMap<String, Any?>): Boolean
    suspend fun getMyBankAccounts(): MutableList<BankAccountDTO>
    suspend fun removeMyBankAccount(id: String): Boolean
}