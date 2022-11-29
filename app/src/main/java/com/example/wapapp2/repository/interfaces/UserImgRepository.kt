package com.example.wapapp2.repository.interfaces

import android.net.Uri

interface UserImgRepository {
    suspend fun uploadProfileUri(myId : String, uri : Uri) : String?
    suspend fun deleteProfileUri(imgUrl : String) : Boolean
}