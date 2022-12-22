package com.tigerface.perf.anrmonitor.entity;

import java.io.Serializable;
import java.util.ArrayList;

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
    private ArrayList<BoxMessage> msgList;

    private String callbackName;
    private int messageWhat;
    private String handlerName;
    private String handlerAddress;


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

    public ArrayList<BoxMessage> getMsgList() {
        return msgList;
    }

    public void setMsgList(ArrayList<BoxMessage> msgList) {
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
