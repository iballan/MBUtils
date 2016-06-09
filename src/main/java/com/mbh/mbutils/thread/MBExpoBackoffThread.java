package com.mbh.mbutils.thread;

/**
 * CreatedBy MBH on 2016-06-04.
 */
public class MBExpoBackoffThread {

    private Thread expoThread;
    private volatile boolean running = false;
    private int MAX_REFRESH_DELAY = 10 * 60 * 1000; // Milliseconds 10 minutes
    private int START_DELAY = 5 * 1000;
    private int currentDelay = 0;
    private int tempDelay = 0;
    private boolean stopOnError = false;
    private boolean resetDelayOnError = true;
    private boolean resetDelayOnFalseWork = true;

    private OnExpoFailure onExpoFailure;
    private OnExpoDoWork mOnExpoDoWork;


    public MBExpoBackoffThread(int START_DELAY,
                               int MAX_REFRESH_DELAY,
                               boolean stopOnError,
                               boolean resetDelayOnError,
                               boolean resetDelayOnFalseWork,
                               OnExpoDoWork onExpoDoWork,
                               OnExpoFailure onExpoFailure) {
        this.START_DELAY = START_DELAY;
        this.MAX_REFRESH_DELAY = MAX_REFRESH_DELAY;
        this.stopOnError = stopOnError;
        this.resetDelayOnError = resetDelayOnError;
        this.resetDelayOnFalseWork = resetDelayOnFalseWork;
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
        if (expoThread != null && expoThread.isAlive() && running) {
            return true;
        } else {
            return false;
        }
    }

    private void initializeThread() {
        expoThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (!running) {
                        break;
                    }
                    try {
                        if (mOnExpoDoWork != null) {
                            if(mOnExpoDoWork.ExpoDoWork()){
                                currentDelay = tempDelay;
                                tempDelay = 0;
                            }else {
                                if(resetDelayOnFalseWork)
                                    currentDelay = 0;
                            }
                        }
                    } catch (Exception exception) {
                        if(resetDelayOnError) currentDelay = 0;
                        if(stopOnError) {
                            throw exception;
                        } else {
                            if (onExpoFailure != null) onExpoFailure.OnExpoFailed(exception);
                        }
                    }
                    try {
                        Thread.sleep(getNextDelay());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            tempDelay = START_DELAY;
        } else {
            tempDelay *= 2;
            if (tempDelay > MAX_REFRESH_DELAY)
                tempDelay = MAX_REFRESH_DELAY;
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
         * @return true if work went successfully
         */
        boolean ExpoDoWork();
    }

    public static class Builder {
        private int maxRefreshDelay = 10 * 60 * 1000; // Milliseconds 10 minutes
        private int startDelay = 10 * 1000;
        private boolean isStopOnError = false;
        private boolean isResetDelayOnError = true;
        private boolean isResetDelayOnFalseWork = true;
        private OnExpoFailure onExpFailure;
        private OnExpoDoWork onExpWork;

        public Builder() {

        }
        // region builder functions
        public Builder setMaxRefreshDelay(int milliseconds){
            this.maxRefreshDelay = milliseconds;
            return this;
        }

        public Builder setStartDelay(int milliseconds){
            this.startDelay = milliseconds;
            return this;
        }

        public Builder setStopOnError(boolean stopOnError){
            this.isStopOnError = stopOnError;
            return this;
        }
        public Builder setResetDelayOnError(boolean isResetDelayOnError){
            this.isResetDelayOnError = isResetDelayOnError;
            return this;
        }
        public Builder setResetDelayOnFalseWork(boolean isResetDelayOnFalseWork){
            this.isResetDelayOnFalseWork = isResetDelayOnFalseWork;
            return this;
        }

        public Builder setOnExpoFailure(OnExpoFailure onExpFailure){
            this.onExpFailure = onExpFailure;
            return this;
        }

        public Builder setOnExpoDoWord(OnExpoDoWork onExpoDoWord){
            this.onExpWork = onExpoDoWord;
            return this;
        }
        // endregion

        public MBExpoBackoffThread build() {
            return new MBExpoBackoffThread(
                    startDelay,
                    maxRefreshDelay,
                    isStopOnError,
                    isResetDelayOnError,
                    isResetDelayOnFalseWork,
                    onExpWork,
                    onExpFailure);
        }
    }
}
