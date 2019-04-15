package xyz.pavelkorolev.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

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
    get() = context?.packageManager

fun Fragment.versionText(): String {
    val packageInfo = packageManager?.getPackageInfo(context?.packageName, 0)
    val versionName = packageInfo?.versionName
        ?: return "Unknown version"
    return "v$versionName"
}

fun Intent.withClearBackStack(): Intent =
    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)