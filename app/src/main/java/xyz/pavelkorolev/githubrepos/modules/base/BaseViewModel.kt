package xyz.pavelkorolev.githubrepos.modules.base

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import xyz.pavelkorolev.githubrepos.entities.OptionalWrapper
import xyz.pavelkorolev.githubrepos.helpers.mapNotNull

interface BaseIntent
interface BaseAction
interface BaseViewState

abstract class BaseViewModel<I : BaseIntent, A : BaseAction, S : BaseViewState> : ViewModel() {

    protected val viewModelLifetime = CompositeDisposable()
    protected val uiLifetime = CompositeDisposable()
    protected val actionSubject: PublishRelay<A> = PublishRelay.create()
    protected val stateSubject: BehaviorRelay<S> = BehaviorRelay.create()

    override fun onCleared() {
        viewModelLifetime.clear()
        uiLifetime.clear()
        super.onCleared()
    }

    private fun currentState(): Observable<S> = Observable
            .just(OptionalWrapper(stateSubject.value))
            .mapNotNull()

    open fun stateUpdatesOn(scheduler: Scheduler): Observable<S> = stateSubject
            .distinctUntilChanged()
            .skip(1)
            .observeOn(scheduler)
            .startWith(currentState())

    open fun processIntents(intents: Observable<I>) {
        uiLifetime.clear()
    }

}
