package com.vlad.rxtest

import android.support.annotation.NonNull
import android.util.Log
import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import com.vlad.rxtest.retrofit.AlgoliaApiService
import com.vlad.rxtest.retrofit.SearchRepository
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import rx.Observable
import rx.Single
import rx.schedulers.Schedulers
import java.util.*

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
        Single.zip(getSearchByDate(1), getSearchByDate(2)) { t1, t2 ->
            listOf(t1, t2)
        }.subscribeOn(Schedulers.io())
                .subscribe { searByDateList ->
                    for (list in searByDateList) {
                        for (hit in list.hits) {
                            Log.i("fuck", hit.title)
                        }
                    }
                }

//        Observable.from(makeIntArray())
//                .subscribeOn(Schedulers.io())
//                .flatMap(object : Func1<Any?, Observable<SearchByDate>> {
//                    override fun call(t: Any?): Observable<SearchByDate> {
//                        Log.i("fuck", "page")
//                        return getSearchByDate(t as Int).toObservable()
//                    }
//                })
//                .flatMap(object : Func1<SearchByDate, Observable<List<Hit>>> {
//                    override fun call(searchByDate: SearchByDate?): Observable<List<Hit>> {
//                        return Observable.just(searchByDate?.hits)
//                    }
//                })
//                .flatMap(object : Func1<List<Hit>, Observable<Hit>> {
//                    override fun call(list: List<Hit>?): Observable<Hit> {
//                        return Observable.from(list)
//                    }
//                })
//                .flatMap(object : Func1<Hit, Observable<String>> {
//                    override fun call(hit: Hit): Observable<String> {
//                        return Observable.just(hit.title)
//                    }
//                })
//                .subscribe(object : Action1<String> {
//                    override fun call(title: String?) {
//                        Log.i("fuck", title)
//                    }
//                })
    }

    override fun initTask2() {
        Observable.just(getSearchByDate(0))
                .subscribeOn(Schedulers.io())
                .flatMap { it.toObservable() }
                .flatMap { Observable.from(it.hits) }
                .flatMap { getUser(it.author).toObservable() }
                .toList()
                .map {
                    val list = it
                    for (karmaLess in list) {
                        if (karmaLess.karma < 3000) {
                            it.remove(karmaLess)
                        }
                    }
                    return@map it
                }
                .subscribe {
                    for (karmaEnough in it) {
                        Log.i("fuck", "${karmaEnough.karma}")
                    }
                }
//        Observable.just(getSearchByDate(0))
//                .subscribeOn(Schedulers.io())
//                .flatMap(object : Func1<Single<SearchByDate>, Observable<SearchByDate>> {
//                    override fun call(single: Single<SearchByDate>): Observable<SearchByDate> {
//                        return single.toObservable()
//                    }
//                })
//                .flatMap(object : Func1<SearchByDate, Observable<List<Hit>>> {
//                    override fun call(searchByDate: SearchByDate?): Observable<List<Hit>> {
//                        return Observable.just(searchByDate?.hits)
//                    }
//                })
//                .flatMap(object : Func1<List<Hit>, Observable<Hit>> {
//                    override fun call(list: List<Hit>?): Observable<Hit> {
//                        return Observable.from(list)
//                    }
//                })
//                .flatMap(object : Func1<Hit, Observable<String>> {
//                    override fun call(hit: Hit): Observable<String> {
//                        return Observable.just(hit.author)
//                    }
//                })
//                .flatMap(object : Func1<String, Observable<UserResponse>> {
//                    override fun call(author: String?): Observable<UserResponse> {
//                        return getUser(author.toString()).toObservable()
//                    }
//                })
//                .flatMap(object : Func1<UserResponse, Observable<Int>> {
//                    override fun call(response: UserResponse?): Observable<Int> {
//                        return Observable.just(response?.karma)
//                    }
//                })
//                .filter(object : Func1<Int, Boolean> {
//                    override fun call(karma: Int?): Boolean? {
//                        return karma.let { it!! > 3000 }
//
//                    }
//                })
//                .subscribe(object : Action1<Int> {
//                    override fun call(karma: Int) {
//                        Log.i("fuck", karma.toString())
//                    }
//                })
    }

    override fun initTask3() {
        Maybe.create(MaybeOnSubscribe<Any> { emitter ->
            if (Random().nextBoolean()) {
                emitter.onSuccess("Bang!")
            } else {
                emitter.onError(IllegalArgumentException("illegal arg"))
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MaybeObserver<Any> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(o: Any) {
                        Log.i("fuck", o as String)
                    }

                    override fun onError(e: Throwable) {
                        Log.i("fuck", e.toString())
                    }

                    override fun onComplete() {

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
                        Log.i("fuck", o as String)
                    }

                    override fun onError(e: Throwable) {
                        Log.i("fuck", e.toString())
                    }

                    override fun onComplete() {
                        Log.i("fuck", "onComplete")
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
                .subscribe { word -> Log.i("fuck", word as String?) }
    }

    override fun initTask6() {
        Maybe.create(MaybeOnSubscribe<Any> { emitter ->
            if (Random().nextBoolean()) {
                emitter.onSuccess("Bang!")
            } else {
                emitter.onComplete()
            }
        })
                .toSingle("You're live")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { word -> Log.i("fuck", word as String?) }
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