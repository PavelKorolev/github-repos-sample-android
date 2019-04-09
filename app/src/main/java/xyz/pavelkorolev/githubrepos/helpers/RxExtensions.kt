package xyz.pavelkorolev.githubrepos.helpers

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import xyz.pavelkorolev.githubrepos.entities.OptionalWrapper
import java.util.concurrent.TimeUnit

fun Disposable.addDisposableTo(compositeDisposable: CompositeDisposable) = compositeDisposable.add(this).let { Unit }

inline fun <T1, T2, R> Observable<T1>.withLatestFrom(observable: Observable<T2>, crossinline combiner: (T1, T2) -> R): Observable<R> =
        withLatestFrom(observable, BiFunction { t1, t2 -> combiner.invoke(t1, t2) })

class Observables private constructor() {
    companion object {
        fun <T1, T2> combineLatest(o1: Observable<T1>, o2: Observable<T2>): Observable<Pair<T1, T2>> =
                Observable.combineLatest(o1, o2, BiFunction { t1, t2 -> t1 to t2 })
    }
}

fun <T, T2> Observable<T>.combineMapToLatestFrom(observable: Observable<T2>): Observable<T2> =
        Observables.combineLatest(this, observable).map { (_, t2) -> t2 }

fun <T, T2> Observable<T>.mapToLatestFrom(observable: Observable<T2>): Observable<T2> =
        withLatestFrom(observable) { _, t2 -> t2 }

fun <T> Observable<T>.buttonThrottle(): Observable<T> =
        this.throttleFirst(300, TimeUnit.MILLISECONDS)

fun <T> Observable<T>.debounceText(): Observable<T> =
        this.debounce(200, TimeUnit.MILLISECONDS)

fun <T> Observable<T>.routeThrottle(): Observable<T> =
        this.throttleFirst(500, TimeUnit.MILLISECONDS)

fun <T> Observable<T>.debugDelay(): Observable<T> = Observable.just(Unit)
        .delay(300, TimeUnit.MILLISECONDS)
        .flatMap { this }

fun <T> Observable<T>.logNext(message: String? = null): Observable<T> = doOnNext {
    if (message == null) {
        logd(it)
    } else {
        logd("$message $it")
    }
}

fun <T> Observable<T>.logEach(message: String? = null): Observable<T> = doOnEach {
    if (message == null) {
        logd(it)
    } else {
        logd("$message $it")
    }
}

fun <T> Observable<T>.logErrors(): Observable<T> = doOnError {
    logd(it.localizedMessage)
    it.printStackTrace()
}

fun <T> ConnectableObservable<T>.connectInto(compositeDisposable: CompositeDisposable) = this
        .connect()
        .addDisposableTo(compositeDisposable)

fun <T> Observable<T>.toListObservable(): Observable<List<T>> = this.toList().toObservable()

fun <T> Observable<OptionalWrapper<T>>.mapNotNull(): Observable<T> = flatMap {
    val value = it.value
    if (value != null) {
        Observable.just(value)
    } else {
        Observable.empty()
    }
}

fun <IN, OUT> Observable<IN>.filterNotNull(valueClosure: (IN) -> OUT?): Observable<OUT> = flatMap {
    val value = valueClosure(it)
    if (value != null) {
        Observable.just(value)
    } else {
        Observable.empty()
    }
}

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
fun <T> Observable<T>.skipOnError(): Observable<T> = onErrorResumeNext { throwable: Throwable ->
    Observable.empty()
}

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
fun <T> Observable<T>.resumeValueOnError(handleThrowable: (Throwable) -> T): Observable<T> = onErrorResumeNext { throwable: Throwable ->
    Observable.just(handleThrowable(throwable))
}

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
fun <T> Observable<T>.resumeObservableOnError(handleThrowable: (Throwable) -> Observable<T>): Observable<T> = onErrorResumeNext { throwable: Throwable ->
    handleThrowable(throwable)
}
