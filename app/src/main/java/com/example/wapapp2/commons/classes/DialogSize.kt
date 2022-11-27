package com.example.wapapp2.commons.classes

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect

class DialogSize {
    companion object {
        fun setDialogSize(dialog: Dialog, widthPercentage: Int, heightPercentage: Int) {
            val widthPercent = widthPercentage.toFloat() / 100
            val heightPercent = heightPercentage.toFloat() / 100

            val dm = Resources.getSystem().displayMetrics

            val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
            val percentWidth = rect.width() * widthPercent
            val percentHeight = rect.height() * heightPercent

            dialog.window?.setLayout(percentWidth.toInt(), percentHeight.toInt())
        }

    }
}