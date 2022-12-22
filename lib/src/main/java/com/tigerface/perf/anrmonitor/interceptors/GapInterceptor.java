package com.tigerface.perf.anrmonitor.interceptors;

import com.tigerface.perf.anrmonitor.AnrMonitor;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

public class GapInterceptor extends Interceptor {
    @Override
    public boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrMonitor.Config config) {
        return currentMessage.getType() == MessageType.GAP;
    }

    @Override
    public boolean deal(BoxMessage message, AnrMonitor.Config config, MessageSaver messageSaver) {
        messageSaver.save(message);
        return true;
    }
}
