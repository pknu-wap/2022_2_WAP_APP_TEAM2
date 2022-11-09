package com.example.wapapp2.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.wapapp2.repository.interfaces.ReceiptImgRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File

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
        val fileName = "${calcRoomId}${DateTime.now().toString()}.png"
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

    override suspend fun downloadReceiptImg(fileName: String, context: Context): Uri? {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        dir?.apply {
            if (!dir.exists())
                dir.mkdir()
        }

        val file = File(dir!!.absolutePath + "/.receipts/${fileName}.png")
        file.createNewFile()

        var result: Uri? = null

        storage.reference.child("receiptimgs").child(fileName).getFile(file)
                .addOnSuccessListener {
                    result = file.toUri()
                }.addOnFailureListener { }
                .await()

        return result

    }
}