package xyz.pavelkorolev.githubrepos.services

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider {
    fun main(): Scheduler
    fun io(): Scheduler
    fun computation(): Scheduler
}

class AppSchedulerProvider : SchedulerProvider {
    override fun main(): Scheduler = AndroidSchedulers.mainThread()
    override fun io(): Scheduler = Schedulers.io()
    override fun computation(): Scheduler = Schedulers.computation()
}
