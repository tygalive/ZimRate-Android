package com.tyganeutronics.myratecalculator.utils.traits

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Recycler View Holder
 */
fun <T : View?> RecyclerView.ViewHolder.findViewById(@IdRes id: Int): T {
    return getView().findViewById(id)
}

fun RecyclerView.ViewHolder.getView(): View {
    return itemView
}

fun RecyclerView.ViewHolder.getContext(): Context {
    return getView().context
}

fun RecyclerView.ViewHolder.getResources(): Resources {
    return getView().resources
}

fun RecyclerView.ViewHolder.getTheme(): Resources.Theme {
    return getContext().theme
}