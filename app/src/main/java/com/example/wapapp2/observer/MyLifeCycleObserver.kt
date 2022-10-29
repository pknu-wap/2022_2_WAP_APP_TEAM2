package com.example.wapapp2.observer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.wapapp2.BuildConfig
import com.example.wapapp2.utils.ImageUtils
import java.io.File


class MyLifeCycleObserver(private val registry: ActivityResultRegistry)
    : DefaultLifecycleObserver {
    private lateinit var pickImgLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private var pickImgCallback: ActivityResultCallback<Uri>? = null
    private var cameraCallback: ActivityResultCallback<Boolean>? = null
    private var imageUri: Uri? = null

    override fun onCreate(owner: LifecycleOwner) {
        pickImgLauncher = registry.register("image", owner, ActivityResultContracts.GetContent()) {
            pickImgCallback?.onActivityResult(it)
        }

        cameraLauncher = registry.register("camera", owner, ActivityResultContracts.TakePicture()) {
            cameraCallback?.onActivityResult(it)
        }
    }

    fun selectImage(callback: ActivityResultCallback<Uri>) {
        this.pickImgCallback = callback
    }


    fun camera(context: Activity, callback: ActivityResultCallback<Boolean>) {

        ImageUtils.createImageFile(context = context)?.also {
            imageUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    it
            )
            cameraLauncher.launch(imageUri)
        }
    }

    fun onTakePhotoClick(context: Activity, callback: ActivityResultCallback<Boolean>) {
        this.cameraCallback = callback

        /*
        if (PermissionChecker.checkPermission(context, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            onRequestCameraClick(callback = takePicture)
        } else {
            camera(context, callback)
        }

         */
    }

    fun checkPermissions(activity: Activity) {
        val hasCamPerm = PermissionChecker.checkSelfPermission(activity, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED
        val hasWritePerm = PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
        val hasReadPerm = PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
        if (!hasCamPerm || !hasWritePerm || !hasReadPerm)
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    fun onRequestCameraClick(context: Activity, callback: ActivityResultCallback<Boolean>) {
        /*
        context.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            // update image
            val message = if (isGranted) {
            } else {
            }

            if (isGranted) {
                camera(context, callback)
            }
        }.launch(Manifest.permission.CAMERA)

         */
    }

}
