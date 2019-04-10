package xyz.pavelkorolev.githubrepos.helpers

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class SwipeToRefreshObservable(private val refresher: SwipeRefreshLayout) : Observable<Unit>() {

    override fun subscribeActual(observer: Observer<in Unit>) {
        val listener = Listener(refresher, observer)
        observer.onSubscribe(listener)
        refresher.setOnRefreshListener(listener)
    }

    class Listener(
        private val refresher: SwipeRefreshLayout,
        private val observer: Observer<in Unit>
    ) : MainThreadDisposable(),
            SwipeRefreshLayout.OnRefreshListener {

        override fun onRefresh() {
            observer.onNext(Unit)
        }

        override fun onDispose() = refresher.setOnRefreshListener(null)
    }
}

fun SwipeRefreshLayout.refreshes(): Observable<Unit> {
    return SwipeToRefreshObservable(this)
}
