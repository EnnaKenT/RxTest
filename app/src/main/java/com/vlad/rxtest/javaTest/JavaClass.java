package com.vlad.rxtest.javaTest;

import android.util.Log;

import com.vlad.rxtest.entity.response.Hit;
import com.vlad.rxtest.entity.response.SearchByDate;
import com.vlad.rxtest.entity.response.UserResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
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
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
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

    public void initTask2() {
        Observable.just(getPage(0))
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Single<SearchByDate>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Single<SearchByDate> searchByDateSingle) {
                        return searchByDateSingle.toObservable();
                    }
                })
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object searchByDate) {
                        return Observable.from(((SearchByDate) searchByDate).getHits());
                    }
                })
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        return getUser(((Hit) o).getAuthor()).toObservable();
                    }
                })
                .toList()
                .map(new Func1<List<Object>, Object>() {
                    @Override
                    public Object call(List<Object> objects) {
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
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.i("fuck", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("fuck", "error " + e.toString());
                    }

                    @Override
                    public void onNext(Object o) {
                        List<UserResponse> list = (List<UserResponse>) o;
                        for (UserResponse item : list) {
                            Log.i("fuck", item.getKarma() + "");
                        }
                    }
                })
        ;
    }

    class CustomSingleSource implements SingleSource {
        @Override
        public void subscribe(SingleObserver observer) {
            Log.i("fuck", "fuck");
        }
    }

    private Object getSource() {
        if (new Random().nextBoolean()) {
            return "Bang!";
        } else {
            return new IllegalArgumentException();
        }
    }

    private List<Integer> makeIntArray() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);

        return list;
    }

    private Single<SearchByDate> getPage(int page) {
        return AlgoliaApiServiceJava.Factory.create().getSearchByDate(page, "story");
    }

    private Single<UserResponse> getUser(String user) {
        return AlgoliaApiServiceJava.Factory.create().getUser(user);
    }

}
