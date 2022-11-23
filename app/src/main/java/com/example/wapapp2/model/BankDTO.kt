package com.example.wapapp2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class BankDTO(val bankName: String, val iconId: Int, val uid: String) : Parcelable
