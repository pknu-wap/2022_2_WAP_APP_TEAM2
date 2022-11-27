package com.example.wapapp2.commons.classes

import android.widget.CompoundButton
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.*

abstract class DelayCheckBoxListener(
        private val delayMillis: Long = 3500L,
) : CompoundButton.OnCheckedChangeListener, MaterialCheckBox.OnCheckedStateChangedListener {
    private var lastState = false
    private var debounceJob: Job? = null
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        debounceJob?.cancel()

        if (lastState != isChecked) {
            lastState = isChecked

            debounceJob = uiScope.launch {
                delay(delayMillis)
                onCheckedChanged(lastState)
            }
        }
    }


    override fun onCheckedStateChangedListener(checkBox: MaterialCheckBox, state: Int) {
        debounceJob?.cancel()
        val isChecked = state == MaterialCheckBox.STATE_CHECKED

        if (lastState != isChecked) {
            lastState = isChecked

            debounceJob = uiScope.launch {
                delay(delayMillis)
                onCheckedChanged(lastState)
            }
        }
    }

    /**
     * 한번 상태가 변경된 직후에는 설정한 시간동안 상태변경 불가
     */
    abstract fun onCheckedChanged(isChecked: Boolean)
}