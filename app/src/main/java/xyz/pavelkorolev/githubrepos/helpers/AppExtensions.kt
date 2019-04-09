package xyz.pavelkorolev.githubrepos.helpers

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import xyz.pavelkorolev.githubrepos.application.App

val Activity.app: App
    get() = application as App
val Service.app: App
    get() = application as App
val Fragment.app: App
    get() = activity?.application as App

fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    val view = currentFocus ?: return
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T> assertNotNull(obj: T?): T {
    if (obj == null) {
        throw AssertionError("Object cannot be null")
    }
    return obj
}

val Fragment.packageManager: PackageManager?
    get() = activity?.packageManager

fun Fragment.versionText(): String {
    val packageInfo = packageManager?.getPackageInfo(activity?.packageName, 0)
    val versionName = packageInfo?.versionName
            ?: return "Unknown version"
    return "v$versionName"
}

fun Intent.withClearBackStack(): Intent =
        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)