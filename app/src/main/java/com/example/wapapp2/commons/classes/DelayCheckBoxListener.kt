package com.example.wapapp2.commons.classes

import android.widget.CompoundButton
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.*

abstract class DelayCheckBoxListener(
        private val delayMillis: Long = 3000L,
) : CompoundButton.OnCheckedChangeListener, MaterialCheckBox.OnCheckedStateChangedListener {
    private var lastState = false
    private var debounceJob: Job? = null
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        buttonView?.isClickable = false

        debounceJob = uiScope.launch {
            delay(delayMillis)
            buttonView?.isClickable = true
        }
        onCheckedChanged(isChecked)
    }


    override fun onCheckedStateChangedListener(checkBox: MaterialCheckBox, state: Int) {
        checkBox.isClickable = false

        debounceJob = uiScope.launch {
            delay(delayMillis)
            checkBox.isClickable = true
        }
        val isChecked = state == MaterialCheckBox.STATE_CHECKED
        onCheckedChanged(isChecked)
    }

    /**
     * 한번 상태가 변경된 직후에는 설정한 시간동안 상태변경 불가
     */
    abstract fun onCheckedChanged(isChecked: Boolean)
}