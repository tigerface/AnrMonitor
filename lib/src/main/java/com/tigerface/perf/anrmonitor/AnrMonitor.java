package com.tigerface.perf.anrmonitor;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

import com.tigerface.perf.anrmonitor.collectors.Collector;
import com.tigerface.perf.anrmonitor.collectors.CpuCollector;
import com.tigerface.perf.anrmonitor.collectors.MemoryCollector;
import com.tigerface.perf.anrmonitor.collectors.StackTraceCollector;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.utils.BoxMessageUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class AnrMonitor implements Printer, ISystemAnrObserver {
    private static final String TAG = "ANR_MONITOR";
    private static AtomicLong MESSAGE_ID_CREATOR = new AtomicLong();
    private final Context mContext;
    private final AtomicBoolean mSwitch;
    private BoxMessage mCurrentMessage;
    private Config mConfig;
    private HandlerThread mSampleThread;
    private final Handler mMainHandler;
    private Handler mCheckHandler;
    private int mCheckCount = 0;
    private boolean mInit = false;

    private AnrMonitor(Context context, Config config) {
        SystemAnrMonitor.init(AnrMonitor.this);
        this.mContext = context;
        this.mConfig = config;
        this.mSwitch = new AtomicBoolean(false);
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        if (mContext == null) {
            throw new RuntimeException("not set Context");
        }
        mInit = true;
        //TODO adapter
        Looper.getMainLooper().setMessageLogging(this);
        startSampleThread();
        sendSolidMessage();
    }

    /**
     * 发送哨兵消息
     * 哨兵处理的时间差值越大说明调度能力越差，只发送20秒哨兵消息消息。
     */
    private void sendSolidMessage() {
        Runnable checkThreadRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = SystemClock.uptimeMillis();
                if (mCheckCount >= 20) {//只统计初始化的前20秒的数据
                    return;
                }
                mCheckCount++;
                mMainHandler.postAtTime(this, "solid", currentTime + mConfig.getSolidTime());
            }
        };
        mMainHandler.removeCallbacksAndMessages(null);
        mMainHandler.post(checkThreadRunnable);
    }

    /**
     * 开启采样线程
     */
    private void startSampleThread() {
        if (mSampleThread != null) {
            mSampleThread.quit();
        }
        mSampleThread = new HandlerThread("SampleThread");
        mSampleThread.start();
        if (mCheckHandler != null) {
            mCheckHandler.removeCallbacksAndMessages(null);
        }
        mCheckHandler = new Handler(mSampleThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //Collect block info
                Dispatcher.getInstance().collectSampleData(mContext, mCurrentMessage, mConfig);
                mCheckHandler.sendMessageDelayed(Message.obtain(), mConfig.getCheckTime());
            }
        };
    }

    @Override
    public void println(String x) {
        if (!mInit) {
            return;
        }
        if (x.contains("<<<<< Finished to") && !mSwitch.get()) {//忽略第一次执行拿到的是处理完成消息
            return;
        }
        if (!mSwitch.get()) {
            msgStart(x);
        } else {
            msgEnd();
        }
        mSwitch.set(!mSwitch.get());
    }

    private void msgStart(String msg) {
        BoxMessage lastMessage = mCurrentMessage;
        this.mCurrentMessage = BoxMessageUtils.parseLooperStart(msg);

        preDealGapMessage(lastMessage);
        mCurrentMessage.setId(MESSAGE_ID_CREATOR.getAndIncrement());
        //开启超时监控
        startTimeoutCheck();
    }

    /**
     * 预处理 Gap 特别长的消息
     *
     * @param lastMessage
     */

    private void preDealGapMessage(BoxMessage lastMessage) {
        if (exceedMaxGap(lastMessage, mCurrentMessage)) {
            BoxMessage gapMessage = BoxMessageUtils.createGapMessage();
            gapMessage.setId(MESSAGE_ID_CREATOR.getAndIncrement());
            gapMessage.setStartWallTime(lastMessage.getEndWallTime());
            gapMessage.setEndWallTime(mCurrentMessage.getStartWallTime());
            gapMessage.setWallTime(mCurrentMessage.getStartWallTime() - lastMessage.getEndWallTime());
            Dispatcher.getInstance().dispatch(lastMessage, gapMessage, mConfig);
        }
    }

    //2个消息间隔，超过50ms
    private boolean exceedMaxGap(BoxMessage lastMessage, BoxMessage currentMessage) {
        if (lastMessage != null && currentMessage != null) {
            return currentMessage.getStartWallTime() - lastMessage.getEndWallTime() > mConfig.getMaxGapTime();
        }
        return false;
    }

    private void startTimeoutCheck() {
        mCheckHandler.removeCallbacksAndMessages(null);
        mCheckHandler.sendMessageDelayed(Message.obtain(), mConfig.getCheckTime());
    }


    private void msgEnd() {
        mCheckHandler.removeCallbacksAndMessages(null);
        updateEndMessage();
        Dispatcher.getInstance().dispatch(null, mCurrentMessage, mConfig);
    }

    private void updateEndMessage() {
        long endWallTime = SystemClock.elapsedRealtime();
        long endCpuTime = SystemClock.currentThreadTimeMillis();
        mCurrentMessage.setEndWallTime(endWallTime);
        mCurrentMessage.setEndCpuTime(endCpuTime);
        long wallTime = endWallTime - mCurrentMessage.getStartWallTime();
        mCurrentMessage.setWallTime(wallTime);
        mCurrentMessage.setCpuTime(endCpuTime - mCurrentMessage.getStartCpuTime());
    }

    public void stop() {
        mInit = false;
        mCurrentMessage = null;
        if (mSampleThread != null) {
            mSampleThread.quit();
        }
        if (mCheckHandler != null) {
            mCheckHandler.removeCallbacks(null);
        }
    }

    @Override
    public void onSystemAnr() {
        Log.e(TAG, "onSystemAnr happen");
        Dispatcher.getInstance().collectANRData(mContext, mCurrentMessage, mConfig);
        if (mConfig.getCustomListener() != null) {
            mConfig.getCustomListener().onTimeOutWarn(mCurrentMessage);
        }
    }

    public static class Config {
        private final Context mContext;
        private ICustomListener iCustomListener;
        private final List<Collector> collectors = Arrays.asList(
                new CpuCollector(),
                new MemoryCollector(),
                new StackTraceCollector());

        public Config(Context context) {
            this.mContext = context;
        }

        public void setICustomListener(ICustomListener iCustomListener) {
            this.iCustomListener = iCustomListener;
        }

        public ICustomListener getCustomListener() {
            return iCustomListener;
        }

        public AnrMonitor build() {
            if (mContext == null) {
                throw new RuntimeException("not set Appllication ");
            }
            AnrMonitor anrMonitor = new AnrMonitor(mContext, this);
            return anrMonitor;
        }

        public long getMaxGapTime() {
            return AnrConfigs.MAX_GAP_MILLS;
        }

        public int getJankFrames() {
            return AnrConfigs.MAX_JANK_FRAMES;
        }

        public long getWarnTime() {
            return AnrConfigs.MAX_WARN_MILLS;
        }

        public int getMaxMessageDuration() {
            return AnrConfigs.MAX_MESSAGES_DURATION;
        }

        public long getCheckTime() {
            return AnrConfigs.CHECK_DURATION_MILLS;
        }

        public long getSolidTime() {
            return AnrConfigs.SOLID_INTERVAL;
        }

        public List<Collector> getCollectors() {
            return collectors;
        }
    }

}
