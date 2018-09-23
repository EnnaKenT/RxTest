package com.vlad.rxtest

import android.support.annotation.NonNull
import android.util.Log
import com.vlad.rxtest.entity.response.Hit
import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import com.vlad.rxtest.retrofit.AlgoliaApiService
import com.vlad.rxtest.retrofit.SearchRepository
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
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
//        io.reactivex.Single.
    }

    override fun initTask62() {

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

        safeSubscribe(d)
    }

    override fun initTask7() {
        val d = Observable.intervalRange(0, 11, 0, 1, TimeUnit.SECONDS)
                .buffer(2)
                .flatMap {
                    Log.i("duck", it.toString())
                    val counter = if (it.size == 1) {
                        it[0]
                    } else {
                        it[0] + it[1]
                    }
                    getSearchByDate(counter.toInt()).toObservable()
                }
                .subscribe {
                    Log.i("duck", it.page.toString())
                }


        safeSubscribe(d)
    }

    private fun safeSubscribe(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun makeIntArray(): List<*> {
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