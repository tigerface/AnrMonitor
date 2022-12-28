package com.tigerface.perf.anrmonitor.interceptors;

import android.util.Log;

import com.tigerface.perf.anrmonitor.config.AnrConfig;
import com.tigerface.perf.anrmonitor.MessageSaver;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

import java.util.ArrayList;

public class DefaultInterceptor extends Interceptor {

    private static final String TAG = "ANR_DefaultInterceptor";

    @Override
    public boolean accept(BoxMessage lastMessage, BoxMessage currentMessage, AnrConfig config) {
        currentMessage.setType(MessageType.INFO);
        return true;
    }

    @Override
    public boolean deal(BoxMessage message, AnrConfig config, MessageSaver messageSaver) {
        BoxMessage lastMessage = messageSaver.getLastInfoMessage();
        //1、最后一条Info消息
        if (lastMessage == null) {
            messageSaver.save(message);
            return true;
        }
        //2、本次消息超过300ms
        if (message.getWallTime() > config.getMaxMessageDuration()) {
            messageSaver.save(message);
            return true;
        }

        //3、加上本次消息耗时超过300ms，单独记录
        if (lastMessage.getWallTime() + message.getWallTime() > config.getMaxMessageDuration()) {
            messageSaver.save(message);
            return true;
        }

        //4、聚合消息
        if (lastMessage.getMsgList() == null) {
            lastMessage.setMsgList(new ArrayList<>());
        }
        lastMessage.setCpuTime(lastMessage.getCpuTime() + message.getCpuTime());
        lastMessage.setWallTime(lastMessage.getWallTime() + message.getWallTime());
        Log.i(TAG, lastMessage.getId() + " lastMessage.getMsgList().size: " + lastMessage.getMsgList().size() + " wall time " + lastMessage.getWallTime() + " cpu " + lastMessage.getCpuTime());
        lastMessage.getMsgList().add(message);
        return true;
    }
}
