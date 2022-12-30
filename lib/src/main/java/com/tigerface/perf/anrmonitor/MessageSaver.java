package com.tigerface.perf.anrmonitor;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息存储器
 *
 * @author lihu
 */
public class MessageSaver {
    private final int MAX_SIZE = 100;
    private List<BoxMessage> messageList = new ArrayList<>();

    public void save(BoxMessage message) {
        messageList.add(message);
        while (messageList.size() > MAX_SIZE) {
            messageList.remove(0);
        }
    }

    public BoxMessage getLastInfoMessage() {
        BoxMessage message;
        if (messageList.size() > 0) {
            message = messageList.get(messageList.size() - 1);
            return message.getType() == MessageType.INFO ? message : null;
        } else {
            return null;
        }
    }

    public List<BoxMessage> getAllMessage() {
        return messageList;
    }
}
