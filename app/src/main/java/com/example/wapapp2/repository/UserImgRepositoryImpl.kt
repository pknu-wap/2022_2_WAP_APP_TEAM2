package com.example.wapapp2.repository

import android.net.Uri
import com.example.wapapp2.repository.interfaces.UserImgRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.joda.time.DateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserImgRepositoryImpl : UserImgRepository{
    private val storage = FirebaseStorage.getInstance()
    companion object {
        private var INSTANCE: UserImgRepositoryImpl? = null

        fun initialize() {
            INSTANCE = UserImgRepositoryImpl()
        }

        fun getINSTANCE() = INSTANCE!!
    }

    override suspend fun uploadProfileUri(myId : String ,uri: Uri) = suspendCoroutine<String?> { continuation ->
        val fileName = "${myId}${DateTime.now().toString()}.png"
        val ref = storage.reference.child("profileimgs").child(fileName)
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

    override suspend fun deleteProfileUri(imgUrl : String) = suspendCoroutine<Boolean> { continuation ->
        Firebase.storage.getReferenceFromUrl(imgUrl).delete()
            .addOnCompleteListener {
                continuation.resume(it.isSuccessful)
            }
    }
}