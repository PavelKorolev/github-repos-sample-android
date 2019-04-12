package xyz.pavelkorolev.githubrepos.ui.base

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import xyz.pavelkorolev.githubrepos.models.OptionalWrapper
import xyz.pavelkorolev.githubrepos.utils.addDisposableTo
import xyz.pavelkorolev.githubrepos.utils.mapNotNull

interface BaseIntent
interface BaseAction
interface BaseViewState

abstract class BaseViewModel<I : BaseIntent, A : BaseAction, S : BaseViewState> : ViewModel() {

    private val uiLifetime = CompositeDisposable()
    protected val disposable = CompositeDisposable()
    protected val intentRelay: PublishRelay<I> = PublishRelay.create()
    protected val stateRelay: BehaviorRelay<S> = BehaviorRelay.create()

    override fun onCleared() {
        disposable.clear()
        uiLifetime.clear()
        super.onCleared()
    }

    private fun currentState(): Observable<S> = Observable
        .just(OptionalWrapper(stateRelay.value))
        .mapNotNull()

    open fun stateUpdatesOn(scheduler: Scheduler): Observable<S> = stateRelay
        .distinctUntilChanged()
        .skip(1)
        .observeOn(scheduler)
        .startWith(currentState())

    open fun processIntents(intents: Observable<I>) {
        uiLifetime.clear()
        intents
            .subscribe(intentRelay)
            .addDisposableTo(uiLifetime)
    }

}
