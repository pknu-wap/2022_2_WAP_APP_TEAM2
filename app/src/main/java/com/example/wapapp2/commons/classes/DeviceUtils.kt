package com.example.wapapp2.commons.classes

import android.content.Context
import android.os.PowerManager
import android.view.inputmethod.InputMethodManager


class DeviceUtils {
    companion object {
        /**
         * 휴대폰 화면 깨움
         */
        fun wakeLock(context: Context) {
            val powerManager =
                    context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
                    "TAG:WAKE_NOTIFICATION")
            wakeLock.acquire(5000L)
            wakeLock.release()
        }

        fun showingKeyboard(context: Context): Boolean {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            return inputMethodManager.isAcceptingText
        }
    }
}