package com.tigerface.perf.anrmonitor.collectors;

import android.content.Context;
import android.util.Log;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CpuCollector implements Collector {
    private static final int BUFFER_SIZE = 1024;
    private static final String TAG = "ANR_" + CpuCollector.class.getSimpleName();

    private int mPid = 0;
    private long mUserLast = 0;
    private long mSystemLast = 0;
    private long mIdleLast = 0;
    private long mIoWaitLast = 0;
    private long mTotalLast = 0;
    private long mAppCpuTimeLast = 0;

    @Override
    public String collect(Context context, BoxMessage message) {
        Log.i(TAG, "collect cpu info,msgId:" + message.getId());
        reset();
        BufferedReader cpuReader = null;
        BufferedReader pidReader = null;
        String result = "";
        try {
            cpuReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), BUFFER_SIZE);
            String cpuRate = cpuReader.readLine();
            if (cpuRate == null) {
                cpuRate = "";
            }

            if (mPid == 0) {
                mPid = android.os.Process.myPid();
            }
            pidReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + mPid + "/stat")), BUFFER_SIZE);
            String pidCpuRate = pidReader.readLine();
            if (pidCpuRate == null) {
                pidCpuRate = "";
            }

            result = parse(cpuRate, pidCpuRate);
        } catch (Throwable throwable) {
            Log.e(TAG, "doSample: ", throwable);
        } finally {
            try {
                if (cpuReader != null) {
                    cpuReader.close();
                }
                if (pidReader != null) {
                    pidReader.close();
                }
            } catch (IOException exception) {
                Log.e(TAG, "doSample: ", exception);
            }
        }
        if (message.getCpuInfoList() == null) {
            message.setCpuInfoList(new ArrayList<>());
        }
        message.getCpuInfoList().add(result);
        return result;
    }

    private void reset() {
        mUserLast = 0;
        mSystemLast = 0;
        mIdleLast = 0;
        mIoWaitLast = 0;
        mTotalLast = 0;
        mAppCpuTimeLast = 0;
    }

    private String parse(String cpuRate, String pidCpuRate) {
        String[] cpuInfoArray = cpuRate.split(" ");
        if (cpuInfoArray.length < 9) {
            return "";
        }

        long user = Long.parseLong(cpuInfoArray[2]);
        long nice = Long.parseLong(cpuInfoArray[3]);
        long system = Long.parseLong(cpuInfoArray[4]);
        long idle = Long.parseLong(cpuInfoArray[5]);
        long ioWait = Long.parseLong(cpuInfoArray[6]);
        long total = user + nice + system + idle + ioWait
                + Long.parseLong(cpuInfoArray[7])
                + Long.parseLong(cpuInfoArray[8]);

        String[] pidCpuInfoList = pidCpuRate.split(" ");
        if (pidCpuInfoList.length < 17) {
            return "";
        }

        long appCpuTime = Long.parseLong(pidCpuInfoList[13])
                + Long.parseLong(pidCpuInfoList[14])
                + Long.parseLong(pidCpuInfoList[15])
                + Long.parseLong(pidCpuInfoList[16]);
        StringBuilder stringBuilder = new StringBuilder();
        if (mTotalLast != 0) {
            long idleTime = idle - mIdleLast;
            long totalTime = total - mTotalLast;

            stringBuilder
                    .append("cpu:")
                    .append((totalTime - idleTime) * 100L / totalTime)
                    .append("% ")
                    .append("app:")
                    .append((appCpuTime - mAppCpuTimeLast) * 100L / totalTime)
                    .append("% ")
                    .append("[")
                    .append("user:").append((user - mUserLast) * 100L / totalTime)
                    .append("% ")
                    .append("system:").append((system - mSystemLast) * 100L / totalTime)
                    .append("% ")
                    .append("ioWait:").append((ioWait - mIoWaitLast) * 100L / totalTime)
                    .append("% ]");
        }
        mUserLast = user;
        mSystemLast = system;
        mIdleLast = idle;
        mIoWaitLast = ioWait;
        mTotalLast = total;

        mAppCpuTimeLast = appCpuTime;
        return new String(stringBuilder);
    }

}
