package com.tyganeutronics.myratecalculator.utils.traits

import android.app.Activity
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

fun Activity.hideKeyBoard() {
    this.currentFocus?.let { view ->
        if (view is TextInputEditText) {
            view.onEditorAction(EditorInfo.IME_ACTION_DONE)
            view.clearFocus()
        }
    }
}

fun Activity.displayBackButton() {
    if (this is AppCompatActivity) {
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    } else {
        this.actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

fun Activity.hideBackButton() {
    if (this is AppCompatActivity) {
        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    } else {
        this.actionBar?.setDisplayHomeAsUpEnabled(false)
    }
}