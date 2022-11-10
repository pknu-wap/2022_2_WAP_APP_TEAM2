package com.example.wapapp2.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class BankAccountDTO(
        @Exclude
        val bankDTO: BankDTO,
        @PropertyName("accountNumber")
        val accountNumber: String,
        @PropertyName("accountHolder")
        val accountHolder: String,
        @PropertyName("bankId")
        val bankId: String
) : Serializable {
    fun toClipboardData(): String = "$accountNumber ${bankDTO.bankName}"

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = bankDTO.hashCode()
        result = 31 * result + accountNumber.hashCode()
        result = 31 * result + accountHolder.hashCode()
        return result
    }
}
