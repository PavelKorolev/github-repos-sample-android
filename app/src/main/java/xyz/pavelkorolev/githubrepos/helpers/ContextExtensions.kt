package xyz.pavelkorolev.githubrepos.helpers

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Context.compatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)
fun Context.compatColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Fragment.compatDrawable(@DrawableRes id: Int) = requireContext().compatDrawable(id)
fun Fragment.compatColor(@ColorRes id: Int) = requireContext().compatColor(id)

fun View.compatDrawable(@DrawableRes id: Int) = context.compatDrawable(id)
fun View.compatColor(@ColorRes id: Int) = context.compatColor(id)