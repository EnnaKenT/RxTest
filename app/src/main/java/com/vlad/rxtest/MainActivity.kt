package com.vlad.rxtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.vlad.rxtest.entity.response.Hit
import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.retrofit.AlgoliaApiService
import com.vlad.rxtest.retrofit.JavaClass
import com.vlad.rxtest.retrofit.SearchRepository
import com.vlad.rxtest.utils.InternetUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import rx.Observable
import rx.Single
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var searchRepository: SearchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchRepository = SearchRepository(AlgoliaApiService.create())
    }

    override fun onStart() {
        super.onStart()

        initTask1()
    }

    private fun initTask1() {
        Observable.from(makeIntArray())
                .subscribeOn(Schedulers.io())
                .flatMap(object : Func1<Any?, Observable<SearchByDate>> {
                    override fun call(t: Any?): Observable<SearchByDate> {
                        Log.i("fuck", "page")
                        return getSearchByDate(t as Int).toObservable()
                    }
                })
//                .filter(object : Func1<SearchByDate, Boolean> {
//                    override fun call(t: SearchByDate?): Boolean {
//                        return t != null
//                    }
//                })
                .flatMap(object : Func1<SearchByDate, Observable<List<Hit>>> {
                    override fun call(searchByDate: SearchByDate?): Observable<List<Hit>> {
                        return Observable.just(searchByDate?.hits)
                    }
                })
                .flatMap(object : Func1<List<Hit>, Observable<*>> {
                    override fun call(list: List<Hit>?): Observable<*> {
                        return Observable.from(list)
                    }
                })
                .flatMap(object : Func1<Any?, Observable<String>> {
                    override fun call(hit: Any?): Observable<String> {
                        return Observable.just((hit as Hit).title)
                    }
                })
//                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Action1<String> {
                    override fun call(title: String?) {
                        Log.i("fuck", title)
                    }
                })
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
}