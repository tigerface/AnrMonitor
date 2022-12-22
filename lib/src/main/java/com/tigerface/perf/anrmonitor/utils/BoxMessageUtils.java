package com.tigerface.perf.anrmonitor.utils;


import android.os.SystemClock;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

public class BoxMessageUtils {

    /**
     * 处理Looper 发出的消息  消息样例： >>>>> Dispatching to " + msg.target + " " +
     * msg.callback + ": " + msg.what
     * >>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab: 0
     */
    public static BoxMessage parseLooperStart(String msg) {
        BoxMessage boxMessage;
        try {
            msg = msg.trim();
            String[] msgA = msg.split(":");
            int what = Integer.parseInt(msgA[1].trim());
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler) {3346d43} com.example.test.MainActivity$1@7250fab
            msgA = msgA[0].split("\\{.*\\}");
            String callback = msgA[1];
            //>>>>> Dispatching to Handler (android.view.ViewRootImpl$ViewRootHandler)
            msgA = msgA[0].split("\\(");
            msgA = msgA[1].split("\\)");
            String handler = msgA[0];
            msgA = msg.split("\\{");
            msgA = msgA[1].split("\\}");

            boxMessage = new BoxMessage();
            boxMessage.setHandlerName(handler);
            boxMessage.setCallbackName(callback);
            boxMessage.setMessageWhat(what);
            boxMessage.setHandlerAddress(msgA[0]);

            boxMessage.setStartWallTime(SystemClock.elapsedRealtime());
            boxMessage.setStartCpuTime(SystemClock.currentThreadTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
            boxMessage = new BoxMessage();
        }
        return boxMessage;
    }

    public static BoxMessage createGapMessage() {
        BoxMessage boxMessage = new BoxMessage();
        boxMessage.setType(MessageType.GAP);
        return boxMessage;
    }

    /**
     * 判断某条消息是不是在更新ui
     */
    public static boolean isBoxMessageDoFrame(BoxMessage message) {
        return message != null && "android.view.Choreographer$FrameHandler".equals(message.getHandlerName()) && message.getCallbackName().contains("android.view.Choreographer$FrameDisplayEventReceiver");
    }

    public static boolean isBoxMessageActivityThread(BoxMessage message) {
        return message != null && "android.app.ActivityThread$H".equals(message.getHandlerName());
    }

}
