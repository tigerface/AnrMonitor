package com.tigerface.perf.anrmonitor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 聚合消息
 */
public class BoxMessage implements Serializable {
    private long id;
    private int type;
    private long startWallTime;//开始处理时间
    private long endWallTime;//结束处理时间
    private long wallTime;
    private long startCpuTime;//CPU开始处理时间
    private long endCpuTime;//
    private long cpuTime;
    private String when;
    private List<BoxMessage> msgList;

    private String callbackName;
    private int messageWhat;
    private String handlerName;
    private String handlerAddress;

    //System
    private List<String> cpuInfoList;
    private List<String> memoryInfoList;
    private List<String> stackTraceList;


    public BoxMessage() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStartWallTime() {
        return startWallTime;
    }

    public void setStartWallTime(long startWallTime) {
        this.startWallTime = startWallTime;
    }

    public long getEndWallTime() {
        return endWallTime;
    }

    public void setEndWallTime(long endWallTime) {
        this.endWallTime = endWallTime;
    }

    public long getWallTime() {
        return wallTime;
    }

    public void setWallTime(long wallTime) {
        this.wallTime = wallTime;
    }

    public long getStartCpuTime() {
        return startCpuTime;
    }

    public void setStartCpuTime(long startCpuTime) {
        this.startCpuTime = startCpuTime;
    }

    public long getCpuTime() {
        return cpuTime;
    }

    public void setCpuTime(long cpuTime) {
        this.cpuTime = cpuTime;
    }

    public List<BoxMessage> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<BoxMessage> msgList) {
        this.msgList = msgList;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    public int getMessageWhat() {
        return messageWhat;
    }

    public void setMessageWhat(int messageWhat) {
        this.messageWhat = messageWhat;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerAddress() {
        return handlerAddress;
    }

    public void setHandlerAddress(String handlerAddress) {
        this.handlerAddress = handlerAddress;
    }

    public long getEndCpuTime() {
        return endCpuTime;
    }

    public void setEndCpuTime(long endCpuTime) {
        this.endCpuTime = endCpuTime;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public List<String> getCpuInfoList() {
        return cpuInfoList;
    }

    public void setCpuInfoList(List<String> cpuInfoList) {
        this.cpuInfoList = cpuInfoList;
    }

    public List<String> getMemoryInfoList() {
        return memoryInfoList;
    }

    public void setMemoryInfoList(List<String> memoryInfoList) {
        this.memoryInfoList = memoryInfoList;
    }

    public List<String> getStackTraceList() {
        return stackTraceList;
    }

    public void setStackTraceList(List<String> stackTraceList) {
        this.stackTraceList = stackTraceList;
    }

    @Override
    public String toString() {
        return "BoxMessage{" +
                "id=" + id +
                ", type=" + type +
                ", startWallTime=" + startWallTime +
                ", endWallTime=" + endWallTime +
                ", wallTime=" + wallTime +
                ", startCpuTime=" + startCpuTime +
                ", endCpuTime=" + endCpuTime +
                ", cpuTime=" + cpuTime +
                ", callbackName='" + callbackName + '\'' +
                ", messageWhat=" + messageWhat +
                ", handlerName='" + handlerName + '\'' +
                ", handlerAddress='" + handlerAddress + '\'' +
                '}';
    }
}
