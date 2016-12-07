package com.mbh.mbutils.thread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Createdby MBH on 26/11/2016.
 */

public class MBRepeatForever {
    private final RepeatForeverRunnable runnable;
    private Thread thread;
    private boolean stopOnError = false;
    private long sleepDelay = 1000;
    private long startDelay = 1000;
    private AtomicBoolean running = new AtomicBoolean(false);

    private MBRepeatForever(Builder builder) {
        this.runnable = builder.mRunnable;
        this.sleepDelay = builder.mSleepDelay;
        this.startDelay = builder.mStartDelay;
        this.stopOnError = builder.mStopOnError;
    }

    public void start() {
        if (running.get() || thread != null && thread.isAlive()) {
            return;
        }
        if (runnable == null) return;
        thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                running.set(true);
                if (startDelay > 10) {
                    MBThreadUtils.TryToSleepFor(startDelay);
                }
                while (isRunning()) {
                    try {
                        runnable.run();
                        if (sleepDelay < 50) {
                            sleepDelay = 50;
                        }
                        if (isRunning()) {
                            MBThreadUtils.TryToSleepFor(sleepDelay);
                        }
                    } catch (Exception ex) {
                        runnable.error(ex);
                        if (stopOnError) {
                            break;
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop(){
        if (thread != null) {
            if (thread.isAlive()) {
                thread = null;
                running.set(false);
            } else {
                thread = null;
                running.set(false);
            }
        }
    }

    public interface RepeatForeverRunnable extends Runnable {
        void error(Exception ex);
    }

    public final static class Builder {
        private boolean mStopOnError;
        private long mSleepDelay;
        private long mStartDelay;
        private MBRepeatForever.RepeatForeverRunnable mRunnable;

        public Builder setStopOnError(boolean stopOnError) {
            mStopOnError = stopOnError;
            return this;
        }

        public Builder setSleepDelay(long sleepDelay) {
            mSleepDelay = sleepDelay;
            return this;
        }

        public Builder setStartDelay(long startDelay) {
            mStartDelay = startDelay;
            return this;
        }

        public Builder setRunnable(MBRepeatForever.RepeatForeverRunnable runnable) {
            mRunnable = runnable;
            return this;
        }

        public MBRepeatForever build() {
            return new MBRepeatForever(this);
        }
    }
}
