package com.tigerface.perf.anrmonitor.collectors;

import android.content.Context;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

/**
 * @author tiger
 */
public interface Collector {
    String collect(Context context, BoxMessage message);
}
