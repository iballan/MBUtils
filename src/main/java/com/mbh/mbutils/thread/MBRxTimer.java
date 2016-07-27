package com.mbh.mbutils.thread;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created By MBH on 2016-07-27.
 */
public class MBRxTimer {
    public static Subscription GetTimerRunOnUI(long initialDelay, long interval, TimeUnit timeUnit, final Runnable runnable) {
        return GetTimer(initialDelay, interval, timeUnit, runnable, true);
    }

    public static Subscription GetTimerRunOnWroker(long initialDelay, long interval, TimeUnit timeUnit, final Runnable runnable) {
        return GetTimer(initialDelay, interval, timeUnit, runnable, false);
    }

    private static Subscription GetTimer(long initialDelay, long interval, TimeUnit timeUnit, final Runnable runnable, boolean isOnUi) {
        Scheduler scheduler = isOnUi?AndroidSchedulers.mainThread():Schedulers.computation();
        return Observable.interval(initialDelay, interval, timeUnit)
                .subscribeOn(Schedulers.newThread())
                .observeOn(scheduler)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        // here is the task that should repeat
                        runnable.run();
                    }
                });

        // Older subscriber
//                .subscribe(new Subscriber<Long>() {
//                    @Override
//                    public void onCompleted() {
//                        Timber.i("OnComplete");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e(e, "OnError");
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Timber.i("OnNext");
//                        initInfos();
//                    }
//                });
    }
}
