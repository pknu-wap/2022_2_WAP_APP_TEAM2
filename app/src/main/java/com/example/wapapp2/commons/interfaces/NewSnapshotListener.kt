package com.example.wapapp2.commons.interfaces

import com.google.firebase.firestore.FirebaseFirestoreException

fun interface NewSnapshotListener<T> {
    fun onEvent(item: T, e: FirebaseFirestoreException?)
}