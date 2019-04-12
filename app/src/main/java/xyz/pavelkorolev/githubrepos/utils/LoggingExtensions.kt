package xyz.pavelkorolev.githubrepos.utils

import timber.log.Timber

fun Any.logd(message: Any?) {
    Timber.tag("DEV_LOG ${javaClass.simpleName}")
    Timber.d("$message")
}