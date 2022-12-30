package com.tigerface.perf.anrmonitor.utils;

import android.util.Log;

import com.tigerface.perf.anrmonitor.AnrMonitor;

/**
 * @author lihu
 */
public class LogUtil {
    public static void d(String tag, String msg) {
        if (AnrMonitor.get().isDebuggable()) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (AnrMonitor.get().isDebuggable()) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (AnrMonitor.get().isDebuggable()) {
            Log.i(tag, msg);
        }
    }
}
