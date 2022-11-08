package com.example.wapapp2.repository.interfaces

import android.net.Uri

interface ReceiptImgRepository {
    suspend fun uploadReceiptImg(uri: Uri, calcRoomId: String): Boolean
    suspend fun deleteReceiptImg(fileName: String): Boolean
}