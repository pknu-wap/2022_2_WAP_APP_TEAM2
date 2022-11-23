package com.example.wapapp2.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.wapapp2.repository.interfaces.ReceiptImgRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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

    override suspend fun deleteReceiptImg(imgUrl: String) = suspendCoroutine<Boolean> { continuation ->
        Firebase.storage.getReferenceFromUrl(imgUrl).delete()
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
                }
    }

    override suspend fun downloadReceiptImg(imgUrl: String): Bitmap? = suspendCoroutine<Bitmap?> { continuation ->
        storage.reference.child("receiptimgs").child(imgUrl).getBytes(1280 * 1280).addOnCompleteListener {
            if (it.isSuccessful) {
                val inputStream = it.result.inputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                continuation.resume(bitmap)
            } else {
                continuation.resumeWithException(it.exception!!)
            }
        }

    }
}