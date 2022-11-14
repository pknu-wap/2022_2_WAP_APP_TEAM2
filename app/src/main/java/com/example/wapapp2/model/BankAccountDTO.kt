package com.example.wapapp2.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class BankAccountDTO(
        @get:Exclude
        var id: String,
        @get:Exclude
        var bankDTO: BankDTO?,
        @PropertyName("accountNumber")
        var accountNumber: String,
        @PropertyName("accountHolder")
        var accountHolder: String,
        @PropertyName("bankId")
        var bankId: String
) : Parcelable {
    constructor() : this("", null, "", "", "")

    fun toClipboardData(): String = "$accountNumber ${bankDTO!!.bankName}"

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
