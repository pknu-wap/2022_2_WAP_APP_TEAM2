package com.example.wapapp2.repository

import android.net.Uri
import androidx.core.net.toFile
import com.example.wapapp2.repository.interfaces.ReceiptImgRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime

class ReceiptImgRepositoryImpl private constructor() : ReceiptImgRepository {

    private val storage = FirebaseStorage.getInstance()

    companion object {
        private var INST: ReceiptImgRepositoryImpl? = null
        val INSTANCE get() = INST!!

        fun initialize() {
            INST = ReceiptImgRepositoryImpl()
        }

    }

    override suspend fun uploadReceiptImg(uri: Uri, calcRoomId: String): Boolean {
        val fileName = "${calcRoomId}${DateTime.now().toString()}.jpg"
        var result = false

        storage.reference.child("receiptimgs").child(fileName).putFile(uri)
                .addOnSuccessListener {
                    result = true
                }.addOnFailureListener { }
                .await()

        return result
    }

    override suspend fun deleteReceiptImg(fileName: String): Boolean {
        var result = false

        storage.reference.child("receiptimgs").child(fileName).delete()
                .addOnSuccessListener {
                    result = true
                }.addOnFailureListener { }
                .await()

        return result
    }
}