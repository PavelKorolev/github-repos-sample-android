package xyz.pavelkorolev.githubrepos.helpers

import android.graphics.Color
import java.util.*

fun randomColor(): Int {
    val rnd = Random()
    return Color.argb(255,
            rnd.nextInt(256),
            rnd.nextInt(256),
            rnd.nextInt(256))
}

fun Int.withAlpha(alpha: Int): Int {
    require(alpha in 0..0xFF)
    return this and 0x00FFFFFF or (alpha shl 24)
}

fun Int.withOpacity(opacity: Int): Int {
    require(opacity in 0..100)
    val ratio = opacity / 100F
    return Color.argb(Math.round(Color.alpha(this) * ratio),
            Color.red(this),
            Color.green(this),
            Color.blue(this))
}