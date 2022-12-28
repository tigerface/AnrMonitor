package com.tigerface.perf.anrmonitor.interceptors;

import com.tigerface.perf.anrmonitor.config.AnrConfig;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;

public abstract class Interceptor {
    public abstract boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrConfig config);

    public abstract boolean deal(BoxMessage message, AnrConfig config, MessageSaver messageSaver);
}
