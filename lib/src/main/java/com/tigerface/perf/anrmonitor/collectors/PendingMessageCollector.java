package com.tigerface.perf.anrmonitor.collectors;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取当前消息队列的情况
 */
public class PendingMessageCollector {
    public static final String PREF = "***";

    public String collect(Context context, BoxMessage message) {
        StringBuilder sb = new StringBuilder();
        Looper.getMainLooper().dump(new Printer() {
            @Override
            public void println(String x) {
                sb.append(x);
            }
        }, PREF);
        return sb.toString();
    }

    /**
     * ANR_DISPATCHER:
     * anr pendingResult,
     * ***  Looper (main, tid 1) {7a35f3f}
     * ***  Message 0: { when=-10s20ms callback=com.example.test.MainActivity$5$1 target=android.os.Handler }
     * ***  Message 1: { when=-10s20ms callback=com.example.test.MainActivity$5$1 target=android.os.Handler }
     * ***  Message 2: { when=-10s20ms callback=com.example.test.MainActivity$5$1 target=android.os.Handler }
     * ***  Message 3: { when=-10s20ms callback=com.example.test.MainActivity$5$1 target=android.os.Handler }
     * ***  Message 4: { when=-10s20ms callback=com.example.test.MainActivity$5$1 target=android.os.Handler }
     * ***  Message 5: { when=-10s20ms callback=android.app.LoadedApk$ReceiverDispatcher$Args target=android.app.ActivityThread$H }
     * ***  Message 6: { when=-9s818ms callback=com.tigerface.perf.anrmonitor.AnrMonitor$1 obj=solid target=android.os.Handler }
     * ***  (Total messages: 7, polling=false, quitting=false)
     *
     * @param result
     * @return
     */
    private static String regex = "\\{([^}]*)\\}";

    public static List<BoxMessage> getPendingList(String result) {
        List<BoxMessage> messageList = new ArrayList<>();
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(result);
        int id = 0;
        while (matcher.find()) {
            String group = matcher.group();
            if (!group.contains("when=")) {
                continue;
            }
            BoxMessage message = new BoxMessage();
            message.setId(id++);
            message.setType(MessageType.PENDING);
            message.setWhen(group);
            messageList.add(message);
        }
        return messageList;
    }
}
