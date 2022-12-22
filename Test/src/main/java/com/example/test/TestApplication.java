package com.example.test;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.tigerface.perf.anrmonitor.AnrMonitor;

public class TestApplication extends Application {
    private final String TAG = TestApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        int pid = Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        if (processName.equals(getPackageName())) {
            Log.d(TAG, "init BlockMonitor");
            new AnrMonitor.Config(this).build().start();
        }

    }
}
