package com.example.wapapp2.commons.classes

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.wapapp2.databinding.ProgressViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class LoadingDialogView private constructor() {
    companion object {
        private val dialogList = mutableListOf<AlertDialog>()

        fun showDialog(activity: Activity, msg: String) {
            if (!activity.isFinishing && activity.isDestroyed) {
                return
            }

            val binding = ProgressViewBinding.inflate(activity.layoutInflater)
            binding.progressMsg.text = msg

            val dialog = MaterialAlertDialogBuilder(activity)
                    .setCancelable(false).setView(binding.root)
                    .create()

            clearDialogs()
            dialogList.add(dialog)
            dialog.show()

            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window!!.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }

        }

        fun clearDialogs() {
            for (dialog in dialogList) {
                dialog.window?.apply {
                    dialog.dismiss()
                }
            }
            dialogList.clear()
        }

    }

}