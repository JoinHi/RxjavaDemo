package com.example;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import sun.rmi.runtime.Log;

public class MyClass {
    public static void main (String[] strings){
        test();
    }

    private static void test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                System.out.println("-------subscribe="+Thread.currentThread().getName());
                e.onNext(1);

                e.onNext(2);

                e.onComplete();

                e.onNext(3);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
//                d.dispose();
            }

            @Override
            public void onNext(@NonNull Integer o) {
                System.out.println("-------onNext="+Thread.currentThread().getName());
                System.out.println(o.toString());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println(e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("-------onComplete="+Thread.currentThread().getName());
                System.out.println("onComplete");
            }
        });
    }
}
