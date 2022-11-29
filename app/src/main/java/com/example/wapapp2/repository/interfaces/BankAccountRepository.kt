package com.example.wapapp2.repository.interfaces

import com.example.wapapp2.model.BankAccountDTO

interface BankAccountRepository {

    suspend fun getBankAccounts(userId: String): MutableList<BankAccountDTO>

}