package com.example.wapapp2.model

import java.io.Serializable

data class BankAccountDTO(val bankDTO: BankDTO, val accountNumber: String, val accountHolder: String) : Serializable {
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
