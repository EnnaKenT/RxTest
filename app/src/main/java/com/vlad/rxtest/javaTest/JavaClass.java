package com.vlad.rxtest.javaTest;

import android.util.Log;

import com.vlad.rxtest.entity.response.Hit;
import com.vlad.rxtest.entity.response.SearchByDate;
import com.vlad.rxtest.entity.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class JavaClass {

    public void initTask1() {
        Single.zip(getPage(1), getPage(2), new BiFunction<SearchByDate, SearchByDate, List<Hit>>() {
            @Override
            public List<Hit> apply(SearchByDate searchByDate, SearchByDate searchByDate2) throws Exception {
                Log.i("duck", "call");
                List<Hit> list = new ArrayList<>();
                list.addAll(searchByDate.component1());
                list.addAll(searchByDate2.component1());
                return list;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Hit>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Hit> hits) {
                        if (hits != null) {
                            for (Hit hit : hits) {
                                Log.i("duck", hit.getTitle());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void initTask2() {
        Observable.just(getPage(0))
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Single<SearchByDate>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Single<SearchByDate> searchByDateSingle) throws Exception {
                        return searchByDateSingle.toObservable();
                    }
                })
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return Observable.fromIterable(((SearchByDate) o).getHits());
                    }
                })
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        String s = ((Hit) o).getAuthor();
                        Log.i("duck", s);
                        return getUser(s).toObservable();
                    }
                })
                .toList()
                .map(new Function<List<Object>, Object>() {
                    @Override
                    public Object apply(List<Object> objects) throws Exception {
                        Log.i("duck", "4");
                        List<UserResponse> responsesList = new ArrayList<>();
                        for (Object o : objects) {
                            UserResponse userResponse = (UserResponse) o;
                            if (userResponse.getKarma() > 3000) {
                                responsesList.add(userResponse);
                            }

                        }
                        return responsesList;
                    }
                })
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("duck", "onSubscribe");
                    }

                    @Override
                    public void onSuccess(Object o) {
                        List<UserResponse> list = (List<UserResponse>) o;
                        for (UserResponse item : list) {
                            Log.i("duck", item.getKarma() + "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("duck", "error " + e.toString());
                    }
                })
        ;
    }

    public void initTask3() {
        Observable.just(getPage(0))
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Single<SearchByDate>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Single<SearchByDate> searchByDateSingle) throws Exception {
                        return searchByDateSingle.toObservable();
                    }
                })
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return Observable.fromIterable(((SearchByDate) o).getHits());
                    }
                })
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        String s = ((Hit) o).getAuthor();
                        Log.i("duck", s);
                        return getUser(s).toObservable();
                    }
                })
                .toList()
                .map(new Function<List<Object>, Object>() {
                    @Override
                    public Object apply(List<Object> objects) throws Exception {
                        Log.i("duck", "4");
                        List<UserResponse> responsesList = new ArrayList<>();
                        for (Object o : objects) {
                            UserResponse userResponse = (UserResponse) o;
                            if (userResponse.getKarma() > 3000) {
                                responsesList.add(userResponse);
                            }

                        }
                        return responsesList;
                    }
                })
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("duck", "onSubscribe");
                    }

                    @Override
                    public void onSuccess(Object o) {
                        List<UserResponse> list = (List<UserResponse>) o;
                        for (UserResponse item : list) {
                            Log.i("duck", item.getKarma() + "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("duck", "error " + e.toString());
                    }
                })
        ;
    }

    public void initTask4() {
        Observable.just(getPage(0))
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Single<SearchByDate>, ObservableSource<SearchByDate>>() {
                    @Override
                    public ObservableSource<SearchByDate> apply(Single<SearchByDate> searchByDateObservable) throws Exception {
                        Log.i("duck", "1");
                        return searchByDateObservable.toObservable();
                    }
                })
                .flatMap(new Function<SearchByDate, ObservableSource<SearchByDate>>() {
                    @Override
                    public ObservableSource<SearchByDate> apply(SearchByDate searchByDate) throws Exception {
                        Log.i("duck", "2");
                        return getPage(1).toObservable();
                    }
                }, new BiFunction<SearchByDate, SearchByDate, ArrayList<SearchByDate>>() {
                    @Override
                    public ArrayList<SearchByDate> apply(SearchByDate searchByDate, SearchByDate searchByDate2) throws Exception {
                        Log.i("duck", "3");
                        ArrayList<SearchByDate> list = new ArrayList<>();
                        list.add(searchByDate);
                        list.add(searchByDate2);
                        return list;
                    }
                })
                .subscribe(new DisposableObserver<ArrayList<SearchByDate>>() {
                    @Override
                    public void onNext(ArrayList<SearchByDate> searchByDates) {
                        for (SearchByDate searchByDate : searchByDates) {
                            Log.i("duck", searchByDate.getPage() + " page");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Single<SearchByDate> getPage(int page) {
        return AlgoliaApiServiceJava.Factory.create().getSearchByDate(page, "story");
    }

    private Single<UserResponse> getUser(String user) {
        return AlgoliaApiServiceJava.Factory.create().getUser(user);
    }

}
