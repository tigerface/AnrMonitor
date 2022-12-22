package com.tigerface.perf.anrmonitor.collectors;

import android.content.Context;
import android.os.Looper;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

public class StackTraceCollector implements Collector {
    public static final String SEPARATOR = "\r\n";

    @Override
    public String collect(Context context, BoxMessage message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("msgId: ")
                .append(message.getId())
                .append(SEPARATOR);
        for (StackTraceElement stackTraceElement : Looper.getMainLooper().getThread().getStackTrace()) {
            stringBuilder.append(stackTraceElement.toString())
                    .append(SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
