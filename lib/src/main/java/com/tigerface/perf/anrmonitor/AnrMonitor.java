package com.tigerface.perf.anrmonitor;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Printer;

import com.tigerface.perf.anrmonitor.config.AnrConfig;
import com.tigerface.perf.anrmonitor.config.DefaultAnrConfig;
import com.tigerface.perf.anrmonitor.entity.BoxMessage;
import com.tigerface.perf.anrmonitor.utils.BoxMessageUtils;
import com.tigerface.perf.anrmonitor.utils.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lihu
 */
public class AnrMonitor implements Printer, ISystemAnrObserver {
    private static final String TAG = "ANR_MONITOR";
    private static AnrMonitor sInstance;
    private static final AtomicLong MESSAGE_ID_CREATOR = new AtomicLong();
    private Context mContext;
    private final AtomicBoolean mSwitch;
    private BoxMessage mCurrentMessage;
    private AnrConfig mConfig = new DefaultAnrConfig();
    //采样线程
    private HandlerThread mSampleThread;
    private Handler mSampleHandler;

    //Checktime线程
    private HandlerThread mCheckTimeThread;
    private Handler mCheckTimeHandler;

    private boolean mInit = false;

    private boolean debuggable;

    private AnrMonitor() {
        SystemAnrMonitor.init(AnrMonitor.this);
        this.mSwitch = new AtomicBoolean(false);
    }

    public static AnrMonitor install(Context context, AnrConfig config) {
        AnrMonitor anrMonitor = get();
        if (context == null) {
            throw new RuntimeException("AnrMonitor not set Context");
        }
        anrMonitor.setContext(context.getApplicationContext());
        anrMonitor.setConfig(config);
        return anrMonitor;
    }

    private void setContext(Context context) {
        this.mContext = context;
    }

    private void setConfig(AnrConfig config) {
        if (config != null) {
            this.mConfig = config;
        }
    }

    public static AnrMonitor get() {
        if (sInstance == null) {
            synchronized (AnrMonitor.class) {
                if (sInstance == null) {
                    sInstance = new AnrMonitor();
                }
            }
        }
        return sInstance;
    }

    public void start() {
        if (mContext == null) {
            throw new RuntimeException("not set Context");
        }
        mInit = true;
        //TODO adapter
        Looper.getMainLooper().setMessageLogging(this);
        startSampleThread();
        //TODO
        sendCheckTimeMessage();
        LogUtil.i(TAG, "start()");
    }

    /**
     * 发送CheckTime消息
     * 哨兵处理的时间差值越大说明调度能力越差，只发送20秒哨兵消息消息。
     */
    private void sendCheckTimeMessage() {
        if (mCheckTimeThread != null) {
            mCheckTimeThread.quit();
        }
        mCheckTimeThread = new HandlerThread("CheckTimeThread");
        mCheckTimeThread.start();
        if (mCheckTimeHandler != null) {
            mCheckTimeHandler.removeCallbacksAndMessages(null);
        }
        mCheckTimeHandler = new Handler(mCheckTimeThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
//        mCheckTimeHandler.sendMessageDelayed()
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
        if (mSampleHandler != null) {
            mSampleHandler.removeCallbacksAndMessages(null);
        }
        mSampleHandler = new Handler(mSampleThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //collect current system info
                Dispatcher.getInstance().collectSampleData(mContext);
                mSampleHandler.sendMessageDelayed(Message.obtain(), mConfig.getCheckTimeMills());
            }
        };
    }

    @Override
    public void println(String x) {
        if (!mInit) {
            return;
        }
        //忽略第一次执行拿到的是处理完成消息
        if (x.contains("<<<<< Finished to") && !mSwitch.get()) {
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
            return currentMessage.getStartWallTime() - lastMessage.getEndWallTime() > mConfig.getMaxGapMills();
        }
        return false;
    }

    private void startTimeoutCheck() {
        mSampleHandler.removeCallbacksAndMessages(null);
        mSampleHandler.sendMessageDelayed(Message.obtain(), mConfig.getCheckTimeMills());
    }

    private void msgEnd() {
        mSampleHandler.removeCallbacksAndMessages(null);
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

    public BoxMessage getCurrentMessage() {
        return mCurrentMessage;
    }

    public AnrConfig getConfig() {
        return mConfig;
    }

    @Override
    public void onSystemAnr() {
        Log.e(TAG, "onSystemAnr happen," + mInit);
        if (!mInit) {
            return;
        }
        Dispatcher.getInstance().collectANRData(mContext);
        if (mConfig.getCustomListener() != null) {
            mConfig.getCustomListener().onAnr(mCurrentMessage);
        }
    }

    public void stop() {
        LogUtil.i(TAG, "stop()");
        mInit = false;
        if (mSampleThread != null) {
            mSampleThread.quit();
        }
        if (mSampleHandler != null) {
            mSampleHandler.removeCallbacks(null);
        }
        if (mCheckTimeThread != null) {
            mCheckTimeThread.quit();
        }
        if (mCheckTimeHandler != null) {
            mCheckTimeHandler.removeCallbacks(null);
        }
    }

    public boolean isDebuggable() {
        return debuggable;
    }

    public AnrMonitor setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
        return this;
    }

}
