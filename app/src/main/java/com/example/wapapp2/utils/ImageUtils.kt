package com.example.wapapp2.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    fun createImageFile(context: Context): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}