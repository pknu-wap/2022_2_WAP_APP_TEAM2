package com.example.wapapp2.commons.classes

import android.widget.CompoundButton
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.coroutines.*

abstract class MyCheckBoxListener<T>(
        private val data: T,
) : CompoundButton.OnCheckedChangeListener, MaterialCheckBox.OnCheckedStateChangedListener {

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        onCheckedChanged(data, isChecked)
    }

    override fun onCheckedStateChangedListener(checkBox: MaterialCheckBox, state: Int) {
        onCheckedChanged(data, state == MaterialCheckBox.STATE_CHECKED)
    }

    abstract fun onCheckedChanged(e: T, isChecked: Boolean)
}