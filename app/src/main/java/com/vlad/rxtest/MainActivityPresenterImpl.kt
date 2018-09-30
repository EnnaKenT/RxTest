package com.vlad.rxtest

import android.annotation.SuppressLint
import android.util.Log
import com.vlad.rxtest.entity.response.Hit
import com.vlad.rxtest.entity.response.NewModel
import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import com.vlad.rxtest.retrofit.AlgoliaApiService
import com.vlad.rxtest.retrofit.SearchRepository
import com.vlad.rxtest.utils.customSchedulers
import com.vlad.rxtest.utils.logThread
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.AsyncSubject






class MainActivityPresenterImpl(var mView: MainActivityView) : MainActivityPresenter {

    private lateinit var searchRepository: SearchRepository
    private var compositeDisposable = CompositeDisposable()

    override fun bindView(view: MainActivityView) {
        mView = view
        searchRepository = SearchRepository(AlgoliaApiService.create())
    }

    override fun unbindView() {
        compositeDisposable.dispose()
    }

    override fun initMap() {
        Observable.just(1, 2, 3)
                .map { x -> 10 * x }
                .subscribe { x ->
                    Log.i("duck", "$x")
                }
    }

    override fun initFlatMap() {
        val d = Observable.just("A", "B", "C")
                .flatMap { letter ->
                    Observable.just("$letter 1 ", "$letter 2 ", "$letter 3")
                }
                .subscribe { word ->
                    Log.i("duck", word)
                }

        compositeDisposable.add(d)
    }

    override fun initFlatMapBi() {
        val d = Observable.just("A", "B", "C")
                .flatMap({ letter ->
                    Observable.just(1, 2, 3)
                }, { letter: String, num: Int ->
                    "$letter $num"
                })
                .subscribe { result ->
                    Log.i("duck", result)
                }

        compositeDisposable.add(d)
    }

    override fun initCreate() {
        val values = Observable.create<String> { o ->
            o.onNext("Hello")
            o.onComplete()
        }
        val subscription = values.subscribe(
                { v -> Log.i("duck", "onNext: $v") }, //onNext
                { e -> Log.i("duck", "onError: $e") }, //onError
                { Log.i("duck", "onCompete") } //onCompete
        )

        compositeDisposable.add(subscription)
    }

    override fun initRange() {
        val d = Observable.range(0, 10)
                .subscribe { Log.i("duck", "$it") }

        compositeDisposable.add(d)
    }

    override fun initInterval() {
        val d = Observable.interval(1, TimeUnit.SECONDS)
                .filter {
                    it < 11
                }
                .subscribe { Log.i("duck", "$it") }

        compositeDisposable.add(d)
    }

    override fun initTake() {
        val d = Observable.interval(1, TimeUnit.SECONDS)
                .take(10)
//                .takeUntil { it < 11 }
                .subscribe { Log.i("duck", "$it") }

        compositeDisposable.add(d)
    }

    override fun initReduce() {
        val d = Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe { Log.i("duck", "onSubscribe") }
                .take(10)
                .reduce { t1: Long, t2: Long -> t1 + t2 }
                .subscribe { Log.i("duck", "$it") }

        compositeDisposable.add(d)
    }

    override fun initSkip() {
        val d = Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe { Log.i("duck", "onSubscribe") }
                .skip(2)
                .take(10)
                .subscribe { Log.i("duck", "$it") }

        compositeDisposable.add(d)
    }

    override fun initBufferAndFilter() {
        val d = Observable.interval(1, TimeUnit.SECONDS)
                .filter {
                    it < 11
                }
                .map {
                    Log.i("duck", "interval $it")
                    return@map it
                }
                .buffer(2)
                .map { list ->
                    var result: Long = 0
                    list.forEach {
                        result += it
                    }
                    return@map result
                }
                .subscribe { Log.i("duck", "onNext $it") }

        compositeDisposable.add(d)
    }

    override fun initTimer() {
        val d = Observable.timer(5, TimeUnit.SECONDS)
                .doOnSubscribe { Log.i("duck", "onSubscribe") }
                .subscribe(
                        { v -> Log.i("duck", "onNext: $v") }, //onNext
                        { e -> Log.i("duck", "onError: $e") }, //onError
                        { Log.i("duck", "onCompete") } //onCompete
                )
        compositeDisposable.add(d)
    }

    override fun initFrom() {
        val list = arrayListOf(1, 2, 3)
        val d = Observable.fromIterable(list)
                .doOnSubscribe { Log.i("duck", "onSubscribe") }
                .subscribe(
                        { v -> Log.i("duck", "onNext: $v") }, //onNext
                        { e -> Log.i("duck", "onError: $e") }, //onError
                        { Log.i("duck", "onCompete") } //onCompete
                )
        compositeDisposable.add(d)
    }

    override fun initFlatMapAndIterable() {
        val d = Observable.just(0)
                .doOnSubscribe { Log.i("duck", "onSubscribe") }
                .flatMap { Observable.just(getSearchByDate(it)) }
                .flatMap { it.toObservable() }
                .flatMap { Observable.fromIterable(it.hits) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Log.i("duck", "title ${it.title}") }
    }

    override fun initZip() {
        val d = Observable.zip(
                getSearchByDate(1).toObservable(),
                getSearchByDate(2).toObservable(),
                BiFunction<SearchByDate, SearchByDate, List<Hit>> { firstIn, secondIn ->
                    Log.i("duck", "BiFunction")
                    val listOut = mutableListOf<Hit>()
                    listOut.addAll(firstIn.hits)
                    listOut.addAll(secondIn.hits)
                    listOut
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { v ->
                            //onNext
                            v.forEach {
                                Log.i("duck", "title ${it.title}")
                            }
                        },
                        { e -> Log.i("duck", "onError: $e") }, //onError
                        { Log.i("duck", "onCompete") } //onCompete
                )

        compositeDisposable.add(d)
    }

    override fun initMerge() {
        val d = Observable.merge(
                Observable.interval(1, TimeUnit.SECONDS)
                        .map { id -> "A$id" }
                        .take(5),
                Observable.interval(1, TimeUnit.SECONDS)
                        .map { id -> "B$id" }
                        .take(5))
                .subscribe { Log.i("duck", it) }

        compositeDisposable.add(d)
    }

    override fun initMergeFirst() {
        val d = Observable.merge(getSearchByDate(0).toObservable(),
                getSearchByDate(1).toObservable(),
                getSearchByDate(2).toObservable(),
                getSearchByDate(3).toObservable())
                .map { it.page }
                .first(-1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { Log.i("duck", "onSubscribe") }
                .doOnError { Log.i("duck", "onError") }
                .subscribe { t1, _ -> Log.i("duck", "$t1") }

        compositeDisposable.add(d)
    }

    override fun initConcat() {
        val d = Observable.concat(
                Observable.interval(1, TimeUnit.SECONDS)
                        .map { id -> "A$id" }
                        .take(5),
                Observable.interval(1, TimeUnit.SECONDS)
                        .map { id -> "B$id" }
                        .take(5))
                .subscribe { Log.i("duck", it) }

        compositeDisposable.add(d)
    }

    override fun initPublishSubject() {
        val source = PublishSubject.create<Int>()

        source.subscribe(getFirstSubjectObserver())

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        source.subscribe(getSecondSubjectObserver())

        source.onNext(4)
        source.onComplete()
    }

    override fun initReplaySubject() {
        val source = ReplaySubject.create<Int>()

        source.subscribe(getFirstSubjectObserver())
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)
        source.onNext(4)
        source.onComplete()

        source.subscribe(getSecondSubjectObserver())
    }

    override fun initBehaviorSubject() {
        val source = BehaviorSubject.create<Int>()

        source.subscribe(getFirstSubjectObserver())
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        source.subscribe(getSecondSubjectObserver())
        source.onNext(4)
        source.onComplete()
    }

    override fun initAsyncSubject() {
        val source = AsyncSubject.create<Int>()

        source.subscribe(getFirstSubjectObserver())
        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        source.subscribe(getSecondSubjectObserver())
        source.onNext(4)
        source.onComplete()
    }

    override fun initTask22() {
        Observable.just<Single<SearchByDate>>(getSearchByDate(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap { searchByDateSingle -> searchByDateSingle.toObservable() }
                .flatMap { (hits) -> Observable.fromIterable(hits) }
                .flatMap { (_, _, _, s) ->
                    getUser(s).toObservable()
                }
                .toList()
                .map {
                    val responsesList = ArrayList<UserResponse>()
                    for (o in it) {
                        val userResponse = o as UserResponse
                        if (userResponse.karma > 3000) {
                            responsesList.add(userResponse)
                        }

                    }
                    responsesList
                }
                .subscribe(object : SingleObserver<Any> {
                    override fun onSubscribe(d: Disposable) {
                        Log.i("duck", "onSubscribe")
                    }

                    override fun onSuccess(o: Any) {
                        val list = o as List<UserResponse>
                        for ((_, _, _, karma) in list) {
                            Log.i("duck", karma.toString() + "")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", "error " + e.toString())
                    }
                })

    }

    override fun initTask3() {
        Maybe.create(MaybeOnSubscribe<Any> { emitter ->
            if (Random().nextBoolean()) {
                emitter.onSuccess("Bang!")
            } else {
                emitter.onError(IllegalArgumentException("illegal arg"))
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MaybeObserver<Any> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(o: Any) {
                        Log.i("duck", o as String)
                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", e.toString())
                    }

                    override fun onComplete() {
                        Log.i("duck", "onComplete")
                    }
                })
    }

    override fun initTask4() {
        Maybe.create(MaybeOnSubscribe<Any> { emitter ->
            if (Random().nextBoolean()) {
                emitter.onSuccess("Bang!")
            } else {
                emitter.onComplete()
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MaybeObserver<Any> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(o: Any) {
                        Log.i("duck", o as String)
                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", e.toString())
                    }

                    override fun onComplete() {
                        Log.i("duck", "onComplete")
                    }
                })
    }

    override fun initTask5() {
        Maybe.create(MaybeOnSubscribe<Any> { emitter ->
            if (Random().nextBoolean()) {
                emitter.onSuccess("Bang!")
            } else {
                emitter.onComplete()
            }
        })
                .toSingle("You're live")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { word -> Log.i("duck", word as String?) }
    }

    override fun initTask61() {
        Single.just(1)
                .subscribeOn(Schedulers.io())
                .logThread()
                .observeOn(AndroidSchedulers.mainThread())
                .logThread()
                .subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(t: Int) {
                        Log.i("duck", "onSuccess $t")
                        Log.i("duck", "${Thread.currentThread()}")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.i("duck", "onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", "onError $e")
                    }
                })
    }

    override fun initTask62() {
        Single.just(1)
                .logThread()
                .customSchedulers()
                .logThread()
                .subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(t: Int) {
                        Log.i("duck", "onSuccess $t")
                        Log.i("duck", "${Thread.currentThread()}")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.i("duck", "onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", "onError $e")
                    }
                })
    }

    override fun initTask63() {
        val d = Observable.just(getSearchByDate(0))
                .subscribeOn(Schedulers.io())
                .flatMap { it.toObservable() }
                .flatMap { Observable.fromIterable(it.hits) }
                .flatMap { getUser(it.author).toObservable() }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val iterator = it.iterator()
                    while (iterator.hasNext()) {
                        val item = iterator.next()
                        if (item.karma < 3000) {
                            iterator.remove()
                        }
                    }

                    it
                }
                .subscribeWith(object : DisposableSingleObserver<Any>() {
                    override fun onSuccess(o: Any) {
                        Log.i("duck", "onSuccess")

                        val list = o as List<UserResponse>
                        list.forEach { Log.i("duck", it.about) }
                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", "onError")
                    }
                })

        safeSubscribe { d }
    }

    private fun safeSubscribe(disposableFunction: () -> Disposable) {
        compositeDisposable.add(disposableFunction())
    }

    override fun initTask7() {
        val d = Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .buffer(2)
                .flatMap { list ->
                    Log.i("duck", list.toString())
                    var counter = 0
                    list.forEach { counter += it.toInt() }
                    getSearchByDate(counter).toObservable()
                }
                .subscribe {
                    Log.i("duck", it.page.toString())
                }


        safeSubscribe(d)
    }

    @SuppressLint("CheckResult")
    override fun initTask9() {
        Observable.intervalRange(0, 11, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .filter {
                    if (it.toInt() != 7) {
                        true
                    } else {
                        throw Exception("Your error message exception")
//                        error("Your error message exception")
                    }
                }
//                .takeUntil {
//                    it < 7
//                }
//                .doOnSubscribe {
//                    Log.d("duck", "subscribed")
//                }
                .subscribeWith(object : Observer<Long> {
                    override fun onSubscribe(d: Disposable) {
                        Log.d("duck", "subscribed")
                    }

                    override fun onNext(aLong: Long) {
                        Log.d("duck", aLong.toString())
                    }

                    override fun onError(e: Throwable) {
                        Log.d("duck", "error: $e")
                    }

                    override fun onComplete() {
                        Log.d("duck", "onComplete")
                    }
                })
    }

    override fun initTask10() {
        val source = BehaviorSubject.create<Int>()

        source.onNext(1)
        source.onNext(2)
        source.onNext(3)

        source.subscribe(getFirstSubjectObserver())

        source.onNext(4)
        source.onNext(5)
        source.onNext(6)

    }

    override fun initTask11() {
        Observable.just(getSearchByDate(0).toObservable())
                .subscribeOn(Schedulers.io())
                .flatMap { it }
                .filter { it.hits.size > 2 }
                .flatMap { Observable.just(it.hits[2]) }
                .filter { it.title.isNotEmpty() && it.author.isNotEmpty() }
                .flatMap({ (_, _, _, authorName) ->
                    getUser(authorName).toObservable()
                }, { (_, title), (_, authorName, _, karma) ->
                    NewModel(authorName, karma, title)
                })
                .subscribe(object : DisposableObserver<NewModel>() {
                    override fun onNext(newModel: NewModel) {
                        Log.i("duck", newModel.toString())

                    }

                    override fun onError(e: Throwable) {
                        Log.i("duck", "error: $e")
                    }

                    override fun onComplete() {
                        Log.i("duck", "onComplete")
                    }
                })

    }

    private fun getFirstSubjectObserver(): Observer<Int> {
        return object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("duck", "First onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("duck", "First onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("duck", "First onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("duck", "First onComplete")
            }
        }
    }

    private fun getSecondSubjectObserver(): Observer<Int> {
        return object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("duck", "Second onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("duck", "Second onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("duck", "Second onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("duck", "Second onComplete")
            }
        }
    }

    private fun safeSubscribe(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun makeIntArray(): List<Int> {
        val list = ArrayList<Int>()
        list.add(1)
        list.add(2)
        return list
    }

    private fun getSearchByDate(page: Int): Single<SearchByDate> {
        return searchRepository.searchByDate(page)
    }

    private fun getUser(user: String): Single<UserResponse> {
        Log.i("fuck", "user $user")
        return searchRepository.getUser(user)
    }

}