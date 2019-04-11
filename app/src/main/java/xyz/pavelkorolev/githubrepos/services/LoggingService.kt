package xyz.pavelkorolev.githubrepos.services

import timber.log.Timber

interface LoggingService {
    fun setup()
}

class LoggingServiceImpl(private val debug: Boolean) : LoggingService {

    override fun setup() {
        if (debug) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
