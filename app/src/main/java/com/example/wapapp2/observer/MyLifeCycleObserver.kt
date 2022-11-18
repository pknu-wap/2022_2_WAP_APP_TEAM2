package com.example.wapapp2.observer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.content.PackageManagerCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.wapapp2.utils.ImageUtils.createImageFile
import com.example.wapapp2.utils.ImageUtils.currentPhotoPath
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Pack200


class MyLifeCycleObserver(private val registry: ActivityResultRegistry, private val context: Context)
    : DefaultLifecycleObserver {
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private var pickImgCallback: ActivityResultCallback<Uri>? = null
    private var permissionsCallback: ActivityResultCallback<Map<String, Boolean>>? = null
    private var onCameraCallback: OnCameraCallback? = null

    override fun onCreate(owner: LifecycleOwner) {
        pickImageLauncher = registry.register("image", owner, ActivityResultContracts.GetContent()) {
            pickImgCallback?.onActivityResult(it)
        }

        cameraLauncher = registry.register("camera", owner, ActivityResultContracts.StartActivityForResult()) {
            var uri: Uri? = null

            it.data?.apply {
                val file = File(Environment.getExternalStorageDirectory(), getStringExtra("fileName")!!)
                uri = FileProvider.getUriForFile(context, context.packageName.toString() + ".provider", file)
            }

            onCameraCallback?.onResult(uri)
        }

        permissionsLauncher = registry.register("permissions", owner, ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsCallback?.onActivityResult(it)
        }
    }

    fun pickImage(activity: Activity, callback: ActivityResultCallback<Uri>) {
        pickImgCallback = callback

        if (!checkStoragePermissions(activity)) {
            Toast.makeText(activity, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
        } else {
            pickImageLauncher.launch("image/*")
        }
    }

    fun camera(activity: Activity, cameraCallback: OnCameraCallback) {
        this.onCameraCallback = cameraCallback

        if (!checkCameraPermission(activity)) {
            Toast.makeText(activity, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity.applicationContext.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile(activity.applicationContext)
                    } catch (ex: IOException) {
                        null
                    }
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                activity.applicationContext, "com.example.wapapp2.provider", it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        takePictureIntent.putExtra("fileName", it.name)
                    }
                }
            }
            cameraLauncher.launch(intent)
        }
    }

    private fun checkStoragePermissions(activity: Activity): Boolean {
        val hasWritePerm =
                PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
        val hasReadPerm =
                PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED

        return if (!hasWritePerm || !hasReadPerm) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            false
        } else {
            true
        }
    }

    private fun checkCameraPermission(activity: Activity): Boolean {
        val cameraPerm =
                PermissionChecker.checkSelfPermission(activity, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED
        return if (!cameraPerm) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
            false
        } else {
            true
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun interface OnCameraCallback {
        fun onResult(uri: Uri?)
    }
}
