package xyz.pavelkorolev.githubrepos

import io.reactivex.observers.TestObserver
import kotlin.test.assertEquals

fun <T> TestObserver<T>.last(): T = values().last()

fun <T> TestObserver<T>.assertLatestEquals(expected: T) = assertEquals(expected, last())