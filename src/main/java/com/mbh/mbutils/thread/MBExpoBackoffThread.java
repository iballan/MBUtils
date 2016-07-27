package com.mbh.mbutils.thread;

/**
 * Implementation of Exponential Backoff thread, which will increase the sleep delay if not needed
 * CreatedBy MBH on 2016-06-04.
 */
public class MBExpoBackoffThread {

    private Thread expoThread;
    private volatile boolean running = false;
    private int maxRefreshDelay = 10 * 60 * 1000; // Milliseconds 10 minutes
    private int firstDelay = 5 * 1000;
    private int startAfter = 0; // start Immediately
    private boolean isStartAfterUsed = false;
    private int currentDelay = 0;
    private int tempDelay = 0;
    private boolean stopOnError = false;
    private boolean resetDelayOnError = true;

    private OnExpoFailure onExpoFailure;
    private OnExpoDoWork mOnExpoDoWork;

    public MBExpoBackoffThread(int startAfter,
                               int firstDelay,
                               int maxRefreshDelay,
                               boolean stopOnError,
                               boolean resetDelayOnError,
                               OnExpoDoWork onExpoDoWork,
                               OnExpoFailure onExpoFailure) {
        this.startAfter = startAfter;
        this.firstDelay = firstDelay;
        this.maxRefreshDelay = maxRefreshDelay;
        this.stopOnError = stopOnError;
        this.resetDelayOnError = resetDelayOnError;
        mOnExpoDoWork = onExpoDoWork;
        this.onExpoFailure = onExpoFailure;
    }

    public void start() {
        if (expoThread != null && expoThread.isAlive()) {
            return;
        }
        running = true;

        initializeThread();

        expoThread.start();
    }

    public boolean isAlive() {
        return expoThread != null && expoThread.isAlive() && running;
    }

    private void initializeThread() {
        expoThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (!running) {
                        break;
                    }
                    if(!isStartAfterUsed){
                        isStartAfterUsed = true;
                        if(startAfter != 0){
                            if(startAfter > 100){
                                MBThreadUtils.TryToSleepFor(startAfter);
                            }
                        }
                    }
                    try {
                        if (mOnExpoDoWork != null) {
                            if (mOnExpoDoWork.ExpoDoWork()) {
                                currentDelay = tempDelay;
                                tempDelay = 0;
                            } else {
                                currentDelay = 0;
                            }
                        }
                    } catch (Exception exception) {
                        if (resetDelayOnError) {
                            currentDelay = 0;
                        }
                        if (stopOnError) {
                            throw new RuntimeException(exception);
                        }
                        if (onExpoFailure != null) onExpoFailure.OnExpoFailed(exception);
                    }
                    MBThreadUtils.TryToSleepFor(getNextDelay());
                    if (!running) {
                        break;
                    }
                }
            }
        };
    }

    private int getNextDelay() {
        tempDelay = currentDelay;
        if (tempDelay == 0) {
            tempDelay = firstDelay;
        } else {
            tempDelay *= 2;
            if (tempDelay > maxRefreshDelay)
                tempDelay = maxRefreshDelay;
        }
        return tempDelay;
    }

    public void stop() {
        running = false;
        if (expoThread != null) {
            if (expoThread.isAlive()) {
                running = false;
                expoThread = null;
            } else {
                running = false;
                expoThread = null;
            }
        }
    }

    public interface OnExpoFailure {
        void OnExpoFailed(Exception exception);
    }

    public interface OnExpoDoWork {
        /**
         * Implementation of Work will be done on exponential time delay
         *
         * @return true if work went successfully
         */
        boolean ExpoDoWork() throws Exception;
    }

    public static class Builder {
        private int maxRefreshDelay = 10 * 60 * 1000; // Milliseconds 10 minutes
        private int firstDelay = 10 * 1000;
        private int startAfter = 0;
        private boolean isStopOnError = false;
        private boolean isResetDelayOnError = true;
        private OnExpoFailure onExpFailure;
        private OnExpoDoWork onExpWork;

        public Builder() {

        }

        /**
         * Set the maximum delay that will thread sleep it after a while
         * @param milliseconds: how many seconds should sleep at max in milliseconds
         * @return builder
         */
        // region builder functions
        public Builder setMaxDelay(int milliseconds) {
            this.maxRefreshDelay = milliseconds;
            return this;
        }


        /**
         * Set the first delay that will the thread sleep it before start multiplying the sleep time
         * @param milliseconds: time in milliseconds
         * @return builder
         */
        public Builder setFirstDelay(int milliseconds) {
            this.firstDelay = milliseconds;
            return this;
        }

        /**
         * Set the time that will the thread wait before it start first time.
         * @param milliseconds: time in milliseconds
         * @return builder
         */
        public Builder setStartAfter(int milliseconds) {
            this.startAfter = milliseconds;
            return this;
        }

        /**
         * Set whether the thread should stop if any error happened
         * @param stopOnError: true to stop on error, false to continue
         * @return builder
         */
        public Builder setStopOnError(boolean stopOnError) {
            this.isStopOnError = stopOnError;
            return this;
        }

        /**
         * Set whether the delay should reset on error
         * @param isResetDelayOnError: true to reset delay to first delay on error, false to not
         * @return builder
         */
        public Builder setResetDelayOnError(boolean isResetDelayOnError) {
            this.isResetDelayOnError = isResetDelayOnError;
            return this;
        }

        /**
         * Set work that should be done on any failure happened
         * @param onExpFailure: work to be done on failure
         * @return builder
         */
        public Builder setOnExpoFailure(OnExpoFailure onExpFailure) {
            this.onExpFailure = onExpFailure;
            return this;
        }

        /**
         * Set work that should be done on
         * @param onExpoDoWord: work to be done on
         * @return builder
         */
        public Builder setOnExpoDoWord(OnExpoDoWork onExpoDoWord) {
            this.onExpWork = onExpoDoWord;
            return this;
        }
        // endregion

        /**
         * This function will build the Exponential thread and return it
         * @return MBExpoBackoffThread: Exponential Thread that will do the work
         */
        public MBExpoBackoffThread build() {
            return new MBExpoBackoffThread(
                    startAfter,
                    firstDelay,
                    maxRefreshDelay,
                    isStopOnError,
                    isResetDelayOnError,
                    onExpWork,
                    onExpFailure);
        }
    }
}
