package com.example.wapapp2.repository.interfaces

import android.content.Context
import android.net.Uri

interface ReceiptImgRepository {
    suspend fun uploadReceiptImg(uri: Uri, calcRoomId: String): Boolean
    suspend fun deleteReceiptImg(fileName: String): Boolean
    suspend fun downloadReceiptImg(fileName: String, context: Context): Uri?
}