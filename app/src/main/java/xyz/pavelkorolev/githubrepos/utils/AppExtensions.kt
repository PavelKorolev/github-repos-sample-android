package xyz.pavelkorolev.githubrepos.utils

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment
import xyz.pavelkorolev.githubrepos.application.App

val Activity.app: App
    get() = application as App
val Service.app: App
    get() = application as App
val Fragment.app: App
    get() = activity?.application as App