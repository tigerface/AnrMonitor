package com.tigerface.perf.anrmonitor.interceptors;

import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.config.AnrConfig;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

/**
 * @author lihu
 */
public class GapInterceptor extends Interceptor {
    @Override
    public boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrConfig config) {
        return currentMessage.getType() == MessageType.GAP;
    }

    @Override
    public boolean deal(BoxMessage message, AnrConfig config, MessageSaver messageSaver) {
        messageSaver.save(message);
        return true;
    }
}
