package com.tigerface.perf.anrmonitor.collectors;

import android.content.Context;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

public interface Collector {
    public String collect(Context context, BoxMessage message);

}
