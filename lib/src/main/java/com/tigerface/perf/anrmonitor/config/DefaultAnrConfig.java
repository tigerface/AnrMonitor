package com.tigerface.perf.anrmonitor.config;

import com.tigerface.perf.anrmonitor.Constants;
import com.tigerface.perf.anrmonitor.ICustomListener;
import com.tigerface.perf.anrmonitor.collectors.Collector;
import com.tigerface.perf.anrmonitor.collectors.CpuCollector;
import com.tigerface.perf.anrmonitor.collectors.MemoryCollector;
import com.tigerface.perf.anrmonitor.collectors.StackTraceCollector;

import java.util.Arrays;
import java.util.List;

public class DefaultAnrConfig implements AnrConfig {
    @Override
    public ICustomListener getCustomListener() {
        return null;
    }

    @Override
    public long getMaxGapMills() {
        return Constants.MAX_GAP_MILLS;
    }

    @Override
    public int getJankFrames() {
        return Constants.MAX_JANK_FRAMES;
    }

    @Override
    public long getWarnTime() {
        return Constants.MAX_WARN_MILLS;
    }

    @Override
    public long getCheckTimeMills() {
        return Constants.CHECK_DURATION_MILLS;
    }

    @Override
    public long getMaxMessageDuration() {
        return Constants.MAX_MESSAGES_DURATION;
    }

    @Override
    public List<Collector> getCollectors() {
        return Arrays.asList(
                new CpuCollector(),
                new MemoryCollector(),
                new StackTraceCollector());
    }
}
