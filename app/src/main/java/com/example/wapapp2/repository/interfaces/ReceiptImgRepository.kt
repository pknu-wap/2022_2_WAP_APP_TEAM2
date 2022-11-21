package com.example.wapapp2.repository.interfaces

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

interface ReceiptImgRepository {
    suspend fun uploadReceiptImg(uri: Uri, calcRoomId: String): String?
    suspend fun deleteReceiptImg(fileName: String): Boolean
    suspend fun downloadReceiptImg(imgUrl: String): Bitmap
}