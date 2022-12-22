package com.tigerface.perf.anrmonitor.interceptors;

import com.tigerface.perf.anrmonitor.AnrMonitor;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;
import com.tigerface.perf.anrmonitor.utils.BoxMessageUtils;

public class AmsInterceptor extends Interceptor {
    private static final String TAG = "ANR_AMS";

    @Override
    public boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrMonitor.Config config) {
        return BoxMessageUtils.isBoxMessageActivityThread(currentMessage);
    }

    @Override
    public boolean deal(BoxMessage message, AnrMonitor.Config config, MessageSaver messageSaver) {
        message.setType(MessageType.AMS);
        messageSaver.save(message);
        return true;
    }
}
