package com.example.user.rxjavademo;

import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "-------";
    private Disposable mInterval;
    private TextView click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        click = (TextView) findViewById(R.id.button);
//        test();
//        concatTest();
//        intervalTest();
//        zipText();
//        bufferTest();
//        timerTest();
//        distinct();
//        last();
//        reduce();
        debounce();
    }

    private void debounce() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext("haha");
                    }
                });
            }
        }).debounce(2,TimeUnit.SECONDS)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void reduce() {//处理前两个数得出最后结果，scan输出每次第一个数
        Observable.just(1,2,4,6)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        log("integer="+integer+";integer2="+integer2);
                        return integer+integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                log("integer="+integer);
            }
        });
    }

    private void last() {
        Observable.just(1,2,4,5)
                .last(0)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {

                        log("integer="+integer);
                    }
                });
    }

    private void distinct() {
        Observable.just(1,2,3,2,3,4,5,6,7,8)
                .distinct(new Function<Integer, String>() {
                    @Override
                    public String apply(@NonNull Integer integer) throws Exception {
                        Log.i(TAG,"distinct="+integer);
                        return "integer="+integer;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {

                        Log.i(TAG,"最后="+integer);
                    }
                });
    }

    private void timerTest() {
        Log.i(TAG,"time="+ System.currentTimeMillis());
        Observable.timer(2,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Log.i(TAG,"time="+ System.currentTimeMillis()+";thread="+Thread.currentThread().getName());
                        Log.i(TAG,"accept="+aLong);
                    }
                });
    }

    private void bufferTest() {
        Observable.just(1,2,3,4,5,6,7,8,9,0)
                .buffer(3,4)//选3个(1,2,3)跳过4个(5,6,7)   也可以是时间在固定时间内把所有事件合并成一个list集合
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(@NonNull List<Integer> integers) throws Exception {
                        for (Integer integer:integers){
                            Log.i(TAG,"integer="+integer);
                        }
                        Log.i(TAG,"listSize="+integers.size());
                    }
                });
    }

    private void zipText() {
        Observable<Integer> observableInteger = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {

                e.onNext(5);
                e.onNext(6);
            }
        });
        Observable<String> observableString = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("haha");
                e.onNext("hehe");
            }
        });
        Observable.zip(observableInteger, observableString, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer.toString()+"+++"+s;
            }
        })
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.i(TAG,s);
            }
        });
    }

    private void intervalTest() {
        mInterval = Flowable.interval(1, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                        Log.i(TAG,"doOnNext");
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                        Log.i(TAG,"subscribe");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInterval.dispose();
    }

    private void concatTest() {

        final int flag = 1;
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                if (flag == 2){
                    e.onComplete();
                }else {
                    Log.i(TAG,"observable1");
                    e.onNext(flag);
//                    e.onComplete();
                }

            }
        });
        Observable<Integer> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                if (flag == 2){
                    e.onComplete();
                }else {
                    Log.i(TAG,"observable2");
//                    e.onNext(flag);
                    e.onComplete();
                }
            }
        });

        Observable.concat(observable1,observable2)
                .subscribe(new Consumer<Integer>() {
                               @Override
                               public void accept(@NonNull Integer integer) throws Exception {
                                    Log.i(TAG,integer.toString());
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });
    }

    private void test() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                log("-------subscribe1=" + Thread.currentThread().getName());
                e.onNext(1);

                log("-------subscribe2=" + Thread.currentThread().getName());
                e.onNext(2);

                log("-------subscribe,onComplete=" + Thread.currentThread().getName());
                e.onComplete();

                log("-------subscribe3=" + Thread.currentThread().getName());
                e.onNext(3);
            }
        })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
//                        if (integer == 2){
                            log("----------------11doOnNext"+integer+"="+Thread.currentThread().getName());
//                        }
                    }
                })
//                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
//                        if (integer == 1){
                            log("----------------22doOnNext"+integer+"="+Thread.currentThread().getName());
//                        }
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                d.dispose();
                    }

                    @Override
                    public void onNext(@NonNull Integer o) {
                        log("-------onNext=" + Thread.currentThread().getName());
                        log(o.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        log(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        log("-------onComplete=" + Thread.currentThread().getName());
                        log("onComplete");
                    }
                });
    }

    public void log(String s) {
        Log.i(TAG, s);
    }
}
