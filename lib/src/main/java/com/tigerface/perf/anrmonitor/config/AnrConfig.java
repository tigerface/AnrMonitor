package com.tigerface.perf.anrmonitor.config;

import com.tigerface.perf.anrmonitor.ICustomListener;
import com.tigerface.perf.anrmonitor.collectors.Collector;
import com.tigerface.perf.anrmonitor.collectors.CpuCollector;
import com.tigerface.perf.anrmonitor.collectors.MemoryCollector;
import com.tigerface.perf.anrmonitor.collectors.StackTraceCollector;

import java.util.Arrays;
import java.util.List;

public interface AnrConfig {
    public ICustomListener getCustomListener();

    public long getMaxGapMills();

    public int getJankFrames();

    public long getWarnTime();


    public long getCheckTimeMills();


    public long getMaxMessageDuration();

    public List<Collector> getCollectors();
}
