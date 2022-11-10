package com.example.wapapp2.observer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


class MyLifeCycleObserver(private val registry: ActivityResultRegistry)
    : DefaultLifecycleObserver {
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private var pickImgCallback: ActivityResultCallback<Uri>? = null
    private var cameraCallback: ActivityResultCallback<ActivityResult>? = null
    private var permissionsCallback: ActivityResultCallback<Map<String, Boolean>>? = null

    override fun onCreate(owner: LifecycleOwner) {
        pickImageLauncher = registry.register("image", owner, ActivityResultContracts.GetContent()) {
            pickImgCallback?.onActivityResult(it)
        }

        cameraLauncher = registry.register("camera", owner, ActivityResultContracts.StartActivityForResult()) {
            cameraCallback?.onActivityResult(it)
        }

        permissionsLauncher = registry.register("permissions", owner, ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsCallback?.onActivityResult(it)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }


    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }

    fun pickImage(activity: Activity, callback: ActivityResultCallback<Uri>) {
        pickImgCallback = callback

        if (!checkStoragePermissions(activity)) {
            Toast.makeText(activity, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
        } else {
            pickImageLauncher.launch("image/*")
        }
    }

    fun camera(activity: Activity, callback: ActivityResultCallback<ActivityResult>) {
        cameraCallback = callback

        if (!checkCameraPermission(activity)) {
            Toast.makeText(activity, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
        } else {
            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
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
}
