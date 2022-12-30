package com.tigerface.perf.anrmonitor;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.tigerface.perf.anrmonitor.collectors.Collector;
import com.tigerface.perf.anrmonitor.collectors.PendingMessageCollector;
import com.tigerface.perf.anrmonitor.config.AnrConfig;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.entity.MessageType;
import com.tigerface.perf.anrmonitor.interceptors.AmsInterceptor;
import com.tigerface.perf.anrmonitor.interceptors.DefaultInterceptor;
import com.tigerface.perf.anrmonitor.interceptors.GapInterceptor;
import com.tigerface.perf.anrmonitor.interceptors.Interceptor;
import com.tigerface.perf.anrmonitor.interceptors.JankInterceptor;
import com.tigerface.perf.anrmonitor.interceptors.TimeoutWarnInterceptor;
import com.tigerface.perf.anrmonitor.utils.AppExecutors;
import com.tigerface.perf.anrmonitor.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lihu
 */
public class Dispatcher {
    private static final String TAG = "ANR_DISPATCHER";
    private static volatile Dispatcher mInstance;
    private final AtomicBoolean mIsSampling;
    private Gson gson = new Gson();
    private MessageSaver messageSaver = new MessageSaver();

    private List<Interceptor> mInterceptors;

    private Dispatcher() {
        mIsSampling = new AtomicBoolean(false);

        //优先级从前到后
        mInterceptors = new ArrayList<>();
        mInterceptors.add(new TimeoutWarnInterceptor());
        mInterceptors.add(new AmsInterceptor());
        mInterceptors.add(new JankInterceptor());
        mInterceptors.add(new GapInterceptor());
        mInterceptors.add(new DefaultInterceptor());
    }

    public static Dispatcher getInstance() {
        if (mInstance == null) {
            synchronized (Dispatcher.class) {
                if (mInstance == null) {
                    mInstance = new Dispatcher();
                }
            }
        }
        return mInstance;
    }

    /**
     * @param lastMessage    上一条消息
     * @param currentMessage 当前消息
     * @param config
     * @return true，该消息是否已经被处理
     */
    public boolean dispatch(BoxMessage lastMessage, BoxMessage currentMessage, AnrConfig config) {
        if (config == null) {
            Log.w(TAG, "not set config !");
            return false;
        }
        for (int i = 0; i < mInterceptors.size(); i++) {
            Interceptor interceptor = mInterceptors.get(i);
            if (interceptor.accept(lastMessage, currentMessage, config)) {
                boolean result = interceptor.deal(currentMessage, config, messageSaver);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public void collectSampleData(Context context) {
        if (mIsSampling.get()) {
            Log.i(TAG, "sampling, ignore.");
            return;
        }
        BoxMessage message = AnrMonitor.get().getCurrentMessage();
        AnrConfig configs = AnrMonitor.get().getConfig();
        List<Collector> collectors = configs.getCollectors();
        Log.i(TAG, "block happens, begin collect system information.");
        mIsSampling.set(true);
        CountDownLatch latch = new CountDownLatch(collectors.size());
        for (Collector collector : collectors) {
            AppExecutors.getInstance().networkIO().execute(() -> {
                collector.collect(context, message);
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mIsSampling.set(false);
        Log.i(TAG, "collect sample system information end.");
    }

    public void collectANRData(Context context) {
        BoxMessage message = AnrMonitor.get().getCurrentMessage();
        AnrConfig configs = AnrMonitor.get().getConfig();
        collectSampleData(context);
        Log.i(TAG, "anr happens, begin collect system information.");
        CountDownLatch latch = new CountDownLatch(1);
        CopyOnWriteArrayList list = new CopyOnWriteArrayList();
        list.addAll(messageSaver.getAllMessage());
        if (!list.contains(message)) {
            message.setType(MessageType.ANR);
            Log.e(TAG, "ANR add current message:" + message.getId());
            list.add(message);
        }

        String pendingResult = new PendingMessageCollector().collect(context, null);
        List<BoxMessage> pendingMessageList = PendingMessageCollector.getPendingList(pendingResult);
        list.addAll(pendingMessageList);
        AppExecutors.getInstance().networkIO().execute(() -> {
            String result = gson.toJson(list);
            FileUtil.getInstance(context).saveAnrData(result);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "collect anr system information end.");
    }
}
