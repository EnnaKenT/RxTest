package com.vlad.rxtest

import android.annotation.SuppressLint
import android.support.annotation.NonNull
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
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivityPresenterImpl(var view: MainActivityView) : MainActivityPresenter {

    private lateinit var searchRepository: SearchRepository
    private var compositeDisposable = CompositeDisposable()

    override fun bindView(@NonNull mainActivity: MainActivity) {
        view = mainActivity
        searchRepository = SearchRepository(AlgoliaApiService.create())
    }

    override fun unbindView() {
        compositeDisposable.dispose()
    }

    override fun initTask1() {
        Single.zip(getSearchByDate(1), getSearchByDate(2), BiFunction<SearchByDate, SearchByDate, List<Hit>> { (hits), (hits2) ->
            Log.i("duck", "BiFunction")
            val list = ArrayList<Hit>()
            list.addAll(hits)
            list.addAll(hits2)
            list
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Hit>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSuccess(t: List<Hit>) {
                        t.forEach {
                            Log.i("duck", it.title)
                        }
                    }
                })
    }

    override fun initTask2() {
        Observable.just<Single<SearchByDate>>(getSearchByDate(0))
                .subscribeOn(Schedulers.io())
                .flatMap { searchByDateSingle -> searchByDateSingle.toObservable() }
                .flatMap { (hits) -> Observable.fromIterable(hits) }
                .flatMap { (_, _, _, s) ->
                    getUser(s).toObservable()
                }
                .toList()
                .map { objects ->
                    val responsesList = ArrayList<UserResponse>()
                    for (o in objects) {
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
    override fun initTask8() {
        val executorService1 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)
        val executorService2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)
        val executorService3 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)

        Observable.just<Single<SearchByDate>>(getSearchByDate(0))
                .subscribeOn(Schedulers.from(executorService1))
                .flatMap { searchByDateObservable ->
                    Log.i("duck", "1")
                    Log.i("duck", Thread.currentThread().toString())
                    searchByDateObservable.toObservable()
                }
                .observeOn(Schedulers.from(executorService2))
                .flatMap(Function<SearchByDate, ObservableSource<SearchByDate>> { (hits, nbHits, page, nbPages, hitsPerPage, processingTimeMS, exhaustiveNbHits, query, params) ->
                    Log.i("duck", "2")
                    Log.i("duck", Thread.currentThread().toString())
                    getSearchByDate(1).toObservable()
                }, BiFunction<SearchByDate, SearchByDate, ArrayList<SearchByDate>> { searchByDate, searchByDate2 ->
                    Log.i("duck", "3")
                    Log.i("duck", Thread.currentThread().toString())

                    val list = ArrayList<SearchByDate>()
                    list.add(searchByDate)
                    list.add(searchByDate2)
                    list
                })
                .observeOn(Schedulers.from(executorService3))
                .subscribe { arrayList ->
                    Log.i("duck", "4")
                    Log.i("duck", Thread.currentThread().toString())
                    arrayList.forEach {
                        Log.i("duck", "${it.page} page")
                    }
                }
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

        source.subscribe(getSubjectObserver())

        source.onNext(4)
        source.onNext(5)
        source.onNext(6)

    }

    override fun initTask11() {
        Observable.just<Single<SearchByDate>>(getSearchByDate(0))
                .subscribeOn(Schedulers.io())
                .flatMap { it.toObservable() }
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

    private fun getSubjectObserver(): Observer<Int> {
        return object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.d("duck", "onSubscribe : " + d.isDisposed)
            }

            override fun onNext(value: Int) {
                Log.d("duck", "onNext value : $value")
            }

            override fun onError(e: Throwable) {
                Log.d("duck", " onError : " + e.message)
            }

            override fun onComplete() {
                Log.d("duck", "onComplete")
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