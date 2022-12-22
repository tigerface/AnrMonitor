package com.tigerface.perf.anrmonitor.entity;

public interface MessageType {
    int INFO = 0;//正常消息
    int AMS = 1;//组件消息
    int GAP = 2;//消息间隔
    int JANK = 3;//UI卡顿
    int WARN = 4;//历史超时 消息
    int ANR = 5;//ANR 消息
    int PENDING = 6;//消息队列剩余的消息
}
