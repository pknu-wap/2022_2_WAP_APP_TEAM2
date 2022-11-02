package com.example.wapapp2.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel

class CalendarDialogViewModel(application: Application) : AndroidViewModel(application) {
    var firstSelectedDay: String = ""
    var arguments: Bundle? = null
}