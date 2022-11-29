package com.example.wapapp2.view.calculation.rushcalc

interface ICheckedRecipient<T> {
    fun onCheckedChange(e: T, isChecked: Boolean)
}