package com.tigerface.perf.anrmonitor.config;

import com.tigerface.perf.anrmonitor.ICustomListener;
import com.tigerface.perf.anrmonitor.collectors.Collector;

import java.util.List;

public interface AnrConfig {
    /**
     * 设置自定义监听
     *
     * @return
     */
    public ICustomListener getCustomListener();

    /**
     * 获取两个消息的最大间隔
     *
     * @return
     */
    public long getMaxGapMills();

    /**
     * 获取绘制消息的最大丢帧数
     *
     * @return
     */
    public int getJankFrames();

    /**
     * 获取消息处理的最大超时市场
     *
     * @return
     */
    public long getWarnTime();


    /**
     * 获取堆栈采样的间隔次数
     *
     * @return
     */
    public long getCheckTimeMills();


    /**
     * 获取聚合消息的最大存储市场
     *
     * @return
     */
    public long getMaxMessageDuration();

    /**
     * 获取发送ANR时的收集器，如CPUCollector等
     *
     * @return
     */
    public List<Collector> getCollectors();
}
