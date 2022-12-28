package com.tigerface.perf.anrmonitor.interceptors;

import android.util.Log;
import android.view.Choreographer;

import com.tigerface.perf.anrmonitor.config.AnrConfig;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;
import com.tigerface.perf.anrmonitor.utils.BoxMessageUtils;
import com.tigerface.perf.anrmonitor.utils.ReflectUtils;

public class JankInterceptor extends Interceptor {
    private static final String TAG = "ANR_JANK";
    private final float mFrameIntervalNanos = ReflectUtils.reflectLongField(Choreographer.getInstance(), Choreographer.class, "mFrameIntervalNanos", 16000000) * 0.000001f;

    @Override
    public boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrConfig config) {
        if (BoxMessageUtils.isBoxMessageDoFrame(currentMessage) && (currentMessage.getWallTime() >
                mFrameIntervalNanos * config.getJankFrames())) {
            currentMessage.setType(MessageType.JANK);
            return true;
        }
        return false;
    }

    @Override
    public boolean deal(BoxMessage message, AnrConfig config, MessageSaver messageSaver) {
        if (config.getCustomListener() != null) {
            config.getCustomListener().onJank(message);
        }
        messageSaver.save(message);
        StringBuilder builder = new StringBuilder();
        builder.append("ANR jank happens,")
                .append("  msg : ")
                .append(message);
        Log.w(TAG, new String(builder));
        return true;
    }
}
