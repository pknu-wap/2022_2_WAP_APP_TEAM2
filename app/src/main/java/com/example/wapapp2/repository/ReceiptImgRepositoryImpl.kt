package com.example.wapapp2.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.example.wapapp2.repository.interfaces.ReceiptImgRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReceiptImgRepositoryImpl private constructor() : ReceiptImgRepository {

    private val storage = FirebaseStorage.getInstance()

    companion object {
        private var INST: ReceiptImgRepositoryImpl? = null
        val INSTANCE get() = INST!!

        fun initialize() {
            INST = ReceiptImgRepositoryImpl()
        }

    }

    override suspend fun uploadReceiptImg(uri: Uri, calcRoomId: String) = suspendCoroutine<String?> { continuation ->
        val fileName = "${calcRoomId}${DateTime.now().toString()}.png"
        val ref = storage.reference.child("receiptimgs").child(fileName)
        var result: String? = null
        val uploadTask = ref.putFile(uri)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful)
                continuation.resume(task.result.toString())
            else
                continuation.resume(null)
        }
    }

    override suspend fun deleteReceiptImg(fileName: String): Boolean {
        var result = false

        storage.reference.child("receiptimgs").child(fileName).delete()
                .addOnSuccessListener {
                    result = true
                }.addOnFailureListener {
                    var state = it.message
                }
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