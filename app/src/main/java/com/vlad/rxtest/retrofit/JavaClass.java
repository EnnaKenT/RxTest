package com.vlad.rxtest.retrofit;

import android.util.Log;

import com.vlad.rxtest.entity.response.Hit;
import com.vlad.rxtest.entity.response.SearchByDate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class JavaClass {

    public void setObserver() {
        Observable.from(makeIntArray())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<SearchByDate>>() {
                    @Override
                    public Observable<SearchByDate> call(Integer integer) {
                        Log.i("fuck", "page");
                        return getPage(integer).toObservable();
                    }
                })
                .filter(new Func1<SearchByDate, Boolean>() {
                    @Override
                    public Boolean call(SearchByDate searchByDate) {
                        return searchByDate != null;
                    }
                })
                .flatMap(new Func1<SearchByDate, Observable<List<Hit>>>() {
                    @Override
                    public Observable<List<Hit>> call(SearchByDate searchByDate) {
                        return Observable.just(searchByDate.component1());
                    }
                })
                .flatMap(new Func1<List<Hit>, Observable<?>>() {
                    @Override
                    public Observable<?> call(List<Hit> hits) {
                        return Observable.from(hits);
                    }
                })
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        Hit hit = null;
                        if (o instanceof Hit) {
                            hit = (Hit) o;
                        }
                        if (hit != null) {
                            return Observable.just(hit.getTitle());
                        }
                        return null;
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                        String title = (String) o;
                        Log.i("fuck", "title: " + title);
                    }
                })
        ;
    }

    private List<Integer> makeIntArray() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);

        return list;
    }

    private Single getPage(int page) {
        return AlgoliaApiServiceJava.Factory.create().getSearchByDate(page, "story");
    }

}
