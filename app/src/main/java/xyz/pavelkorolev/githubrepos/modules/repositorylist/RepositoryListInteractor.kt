package xyz.pavelkorolev.githubrepos.modules.repositorylist

import io.reactivex.Observable
import xyz.pavelkorolev.githubrepos.entities.Repository

interface RepositoryListInteractor {
    fun loadRepositoryList(): Observable<List<Repository>>
}

class RepositoryListInteractorImpl : RepositoryListInteractor {

    override fun loadRepositoryList(): Observable<List<Repository>> = Observable.defer {
        Observable.fromCallable {
            listOf(
                Repository("One"),
                Repository("Two"),
                Repository("Three"),
                Repository("Four"),
                Repository("Five")
            )
        }
    }

}
