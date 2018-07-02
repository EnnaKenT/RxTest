package com.vlad.rxtest.javaTest;

import android.util.Log;

import com.vlad.rxtest.entity.response.Hit;
import com.vlad.rxtest.entity.response.SearchByDate;

import java.util.ArrayList;
import java.util.List;

import rx.Single;
import rx.Subscriber;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class JavaClass {

    public void initTask1() {
        Single.zip(getPage(1), getPage(2), (Func2<SearchByDate, SearchByDate, List<Hit>>) (firstPageResponse, secondPageResponse) -> {
            Log.i("fuck", "call");
            List<Hit> list = new ArrayList<>();
            list.addAll(firstPageResponse.component1());
            list.addAll(secondPageResponse.component1());
            return list;
        })
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        Log.i("fuck", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("fuck", "onError " + e.toString());
                    }

                    @Override
                    public void onNext(Object o) {
                        List<Hit> list = null;
                        if (o instanceof List) {
                            list = (List) o;
                        }
                        if (list != null) {
                            for (Hit hit : list) {
                                Log.i("fuck", hit.getTitle());
                            }
                        }
                    }
                });

//        Observable.from(makeIntArray())
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Func1<Integer, Observable<SearchByDate>>() {
//                    @Override
//                    public Observable<SearchByDate> call(Integer integer) {
//                        Log.i("fuck", "page");
//                        return getPage(integer).toObservable();
//                    }
//                })
//                .filter(new Func1<SearchByDate, Boolean>() {
//                    @Override
//                    public Boolean call(SearchByDate searchByDate) {
//                        return searchByDate != null;
//                    }
//                })
//                .flatMap(new Func1<SearchByDate, Observable<List<Hit>>>() {
//                    @Override
//                    public Observable<List<Hit>> call(SearchByDate searchByDate) {
//                        return Observable.just(searchByDate.component1());
//                    }
//                })
//                .flatMap(new Func1<List<Hit>, Observable<Hit>>() {
//                    @Override
//                    public Observable<Hit> call(List<Hit> hits) {
//                        return Observable.from(hits);
//                    }
//                })
//                .filter(new Func1<Hit, Boolean>() {
//                    @Override
//                    public Boolean call(Hit hit) {
//                        return hit != null;
//                    }
//                })
//                .flatMap(new Func1<Hit, Observable<String>>() {
//                    @Override
//                    public Observable<String> call(Hit hit) {
//                        return Observable.just(hit.getTitle());
//                    }
//                })
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String title) {
//
//                        Log.i("fuck", "title: " + title);
//                    }
//                })
//        ;
    }

//    public void initTask2() {
//        Observable.just(getPage(0))
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Func1<Single<SearchByDate>, Observable<SearchByDate>>() {
//                    @Override
//                    public Observable<SearchByDate> call(Single<SearchByDate> single) {
//                        return single.toObservable();
//                    }
//                })
//                .flatMap(new Func1<SearchByDate, Observable<List<Hit>>>() {
//                    @Override
//                    public Observable<List<Hit>> call(SearchByDate searchByDate) {
//                        return Observable.just(searchByDate.component1());
//                    }
//                })
//                .flatMap(new Func1<List<Hit>, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(List<Hit> hits) {
//                        return Observable.from(hits);
//                    }
//                })
//                .flatMap(new Func1<Object, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(Object o) {
//                        Hit hit = null;
//                        if (o instanceof Hit) {
//                            hit = (Hit) o;
//                        }
//                        if (hit != null) {
//                            return Observable.just(hit.getTitle());
//                        }
//                        return null;
//                    }
//                })
//                .subscribe(new Action1<Object>() {
//                    @Override
//                    public void call(Object o) {
//
//                        String title = (String) o;
//                        Log.i("fuck", "title: " + title);
//                    }
//                })
//        ;
//    }

    private List<Integer> makeIntArray() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);

        return list;
    }

    private Single getPage(int page) {
        return AlgoliaApiServiceJava.Factory.create().getSearchByDate(page, "story");
    }

    private Single getUser(String user) {
        return AlgoliaApiServiceJava.Factory.create().getUser(user);
    }

}
