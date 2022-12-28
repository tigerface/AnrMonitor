package com.tigerface.perf.anrmonitor.collectors;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

import java.util.ArrayList;

public class StackTraceCollector implements Collector {
    public static final String SEPARATOR = "\r\n";
    private static final String TAG = "ANR_" + StackTraceCollector.class.getSimpleName();

    @Override
    public String collect(Context context, BoxMessage message) {
        Log.i(TAG, "collect stack info,msgId:" + message.getId());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("msgId: ")
                .append(message.getId())
                .append(SEPARATOR);
        for (StackTraceElement stackTraceElement : Looper.getMainLooper().getThread().getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString())
                    .append(SEPARATOR);
        }
        if(message.getStackTraceList() == null){
            message.setStackTraceList(new ArrayList<>());
        }
        message.getStackTraceList().add(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
