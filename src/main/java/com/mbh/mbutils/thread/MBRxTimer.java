package com.mbh.mbutils.thread;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created By MBH on 2016-07-27.
 */
public class MBRxTimer {
    /* Example
     This function will return Subscription
     rxInfoTimer = MBRxTimer.GetTimerRunOnUI(0 /start after/ , 3/repeat every/, TimeUnit.SECONDS, new Runnable() {
        @Override
        public void run() {
            // Here is what you want to do every 3 seconds
        }
        });
     */

    /**
     * Will create and start a timer that will repeat forever doing a task on Main UI
     * @param initialDelay: (in milliseconds) the first delay before starting the timer
     * @param interval: Unit that will repeat on it
     * @param timeUnit: Time unit to convert the previous parameter to milliseconds
     * @param runnable: The task that we want to repeat every certain time
     * @return Rx Subscription where we can cancel the timer
     */
    public static Subscription GetTimerRunOnUI(long initialDelay, long interval, TimeUnit timeUnit, final Runnable runnable) {
        return GetTimer(initialDelay, interval, timeUnit, runnable, true);
    }

    /**
     * Will create and start a timer that will repeat forever doing a task on Background worker
     * @param initialDelay: (in milliseconds) the first delay before starting the timer
     * @param interval: Unit that will repeat on it
     * @param timeUnit: Time unit to convert the previous parameter to milliseconds
     * @param runnable: The task that we want to repeat every certain time
     * @return Rx Subscription where we can cancel the timer
     */
    public static Subscription GetTimerRunOnWroker(long initialDelay, long interval, TimeUnit timeUnit, final Runnable runnable) {
        return GetTimer(initialDelay, interval, timeUnit, runnable, false);
    }

    /**
     * Creating timer that will repeat on certain time forever
     * @param initialDelay: (in milliseconds) the first delay before starting the timer
     * @param interval: Unit that will repeat on it
     * @param timeUnit: Time unit to convert the previous parameter to milliseconds
     * @param runnable: The task that we want to repeat every certain time
     * @param isOnUi: selecting the worker type on which that task will be excuted
     * @return Rx Subscription where we can cancel the timer
     */
    private static Subscription GetTimer(long initialDelay, long interval, TimeUnit timeUnit, final Runnable runnable, boolean isOnUi) {
        Scheduler scheduler = isOnUi?AndroidSchedulers.mainThread():Schedulers.io();
        return Observable.interval(initialDelay, interval, timeUnit)
                .subscribeOn(Schedulers.io())
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

    public static class RetryWithDelay implements
            Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount;

        public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
            this.retryCount = 0;
        }

        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            throwable.printStackTrace();
                            if (++retryCount < maxRetries) {
                                return Observable.timer(retryDelayMillis,
                                        TimeUnit.MILLISECONDS);
                            }
                            return Observable.error(throwable);
                        }
                    });
        }
    }
}
