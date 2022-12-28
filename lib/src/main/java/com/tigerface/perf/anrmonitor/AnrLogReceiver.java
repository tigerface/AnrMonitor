package com.tigerface.perf.anrmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AnrLogReceiver extends BroadcastReceiver {
    private static final String TAG = "AnrLogReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        // 仪表发来的广播,直接调用更新方法
        if ("collectSamplerLog".equals(action)) {
            Log.i(TAG, "onReceive, collectSamplerLog");
            Dispatcher.getInstance().collectANRData(context);
        }
    }
}
