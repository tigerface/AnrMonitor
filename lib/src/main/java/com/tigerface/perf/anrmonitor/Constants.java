package com.tigerface.perf.anrmonitor;

public interface Constants {
    long SOLID_INTERVAL = 1000;//定时发送哨兵消息，以查看系统分发情况
    long MAX_WARN_MILLS = 3000;//ANR超时时间
    long CHECK_DURATION_MILLS = 3000;//采样间隔
    long MAX_GAP_MILLS = 50;//2个消息的gap时间
    int MAX_MESSAGES_DURATION = 300;//聚合消息最大时长
    int MAX_JANK_FRAMES = 30;//三大流程掉帧数 超过这个值判定为jank
}
