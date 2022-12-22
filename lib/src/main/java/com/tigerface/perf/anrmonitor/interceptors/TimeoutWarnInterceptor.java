package com.tigerface.perf.anrmonitor.interceptors;

import android.util.Log;

import com.tigerface.perf.anrmonitor.AnrMonitor;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

public class TimeoutWarnInterceptor extends Interceptor {
    private static final String TAG = "ANR_" + TimeoutWarnInterceptor.class.getSimpleName();

    @Override
    public boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrMonitor.Config config) {
        if (currentMessage.getType() != MessageType.GAP
                && currentMessage.getWallTime() > config.getWarnTime()) {
            currentMessage.setType(MessageType.WARN);
            return true;
        }
        return false;
    }

    @Override
    public boolean deal(BoxMessage message, AnrMonitor.Config config, MessageSaver messageSaver) {
        if (config.getCustomListener() != null) {
            config.getCustomListener().onTimeOutWarn(message);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("ANR may happens,")
                .append("  msg : ")
                .append(message);
        Log.w(TAG, new String(builder));
        messageSaver.save(message);
        return true;
    }
}
