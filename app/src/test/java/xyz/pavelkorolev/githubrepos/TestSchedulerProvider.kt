package xyz.pavelkorolev.githubrepos

import io.reactivex.schedulers.Schedulers
import xyz.pavelkorolev.githubrepos.services.SchedulerProvider

class TestSchedulerProvider : SchedulerProvider {
    override fun main() = Schedulers.trampoline()
    override fun io() = Schedulers.trampoline()
    override fun computation() = Schedulers.trampoline()
}
