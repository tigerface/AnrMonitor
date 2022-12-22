package com.tigerface.perf.anrmonitor.interceptors;

import com.tigerface.perf.anrmonitor.AnrMonitor;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;

public abstract class Interceptor {
    public abstract boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrMonitor.Config config);

    public abstract boolean deal(BoxMessage message, AnrMonitor.Config config, MessageSaver messageSaver);
}
