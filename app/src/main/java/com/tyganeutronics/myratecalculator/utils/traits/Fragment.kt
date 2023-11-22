package com.tyganeutronics.myratecalculator.utils.traits

import android.os.Build
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Fragment
 */
fun Fragment.setTitle(title: String?) {
    requireActivity().title = title
}

fun Fragment.setTitle(@StringRes title: Int) {
    requireActivity().setTitle(title)
}

fun <T : View?> Fragment.findViewById(@IdRes id: Int): T? {
    return view?.findViewById(id)
}

fun <T : View?> Fragment.requireViewById(@IdRes id: Int): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        requireView().requireViewById<T>(id)
    } else {
        findViewById<T>(id)!!
    }
}

fun Fragment.onBackPressed() {
    requireActivity().onBackPressedDispatcher.onBackPressed()
}

fun Fragment.invalidateOptionsMenu() {
    requireActivity().invalidateOptionsMenu()
}

fun Fragment.hideKeyBoard() {
    requireActivity().hideKeyBoard()
}

fun Fragment.displayBackButton() {
    requireActivity().displayBackButton()
}

fun Fragment.hideBackButton() {
    requireActivity().hideBackButton()
}