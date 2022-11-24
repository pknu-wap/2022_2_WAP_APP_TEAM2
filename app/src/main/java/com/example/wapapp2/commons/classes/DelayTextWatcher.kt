package com.example.wapapp2.commons.classes

import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.*

abstract class DelayTextWatcher : TextWatcher {
    private var lastInput = ""
    private val delayMillis = 500L
    private var debounceJob: Job? = null
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun afterTextChanged(editable: Editable?) {
        editable?.apply {
            debounceJob?.cancel()
            val newInput = toString()

            if (lastInput != newInput) {
                lastInput = newInput

                debounceJob = uiScope.launch {
                    delay(delayMillis)
                    onFinalText(lastInput)
                }
            }
        }
    }

    abstract fun onFinalText(text: String)

    override fun beforeTextChanged(cs: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {}

    fun isInt(text: String): Boolean {
        return try {
            text.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }

    }
}