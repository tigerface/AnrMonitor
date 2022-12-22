package com.tigerface.perf.anrmonitor.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final int THREAD_COUNT = 3;
    private AppExecutors appExecutors;

    private final Executor diskIO;

    private final Executor networkIO;

    private final MainThreadExecutor mainThread;

    @VisibleForTesting
    AppExecutors(Executor diskIO, Executor networkIO, MainThreadExecutor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

//    public Executor diskIO() {
//        return diskIO;
//    }

    public Executor networkIO() {
        return networkIO;
    }

    public MainThreadExecutor mainThread() {
        return mainThread;
    }

    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }

        public void executeDelay(@NonNull Runnable command, long ms) {
            mainThreadHandler.postDelayed(command, ms);
        }

    }

    public static AppExecutors getInstance() {
        return AppExecutorsHolder.appExecutors;
    }

    static class AppExecutorsHolder {
        static AppExecutors appExecutors = new AppExecutors();
    }
}
