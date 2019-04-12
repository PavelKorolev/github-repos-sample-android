package xyz.pavelkorolev.githubrepos.utils

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.tinted(color: Int): Drawable {
    val wrapDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(wrapDrawable, color)
    DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN)
    return wrapDrawable
}

fun Drawable.copy(): Drawable? = constantState?.newDrawable()