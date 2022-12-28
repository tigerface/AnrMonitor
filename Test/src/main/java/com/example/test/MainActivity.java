package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends Activity {
    private final String TAG = MainActivity.class.getSimpleName();
    private JankView jankView;
    AnrTestBroadcast anrTestBroadcast;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            TextView textView = findViewById(R.id.tv_text);
            textView.setText("我是：  " + num++);
//            textView.postDelayed(this,2500);

            int i = 1;
            while (i < 500_000_000) {
                i++;
            }
            textView.postDelayed(this, 16);
        }
    };

    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            Toast.makeText(MainActivity.this, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予文件读写权限权限", Toast.LENGTH_SHORT).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页
                        } else {
                            Toast.makeText(MainActivity.this, "获取文件读写权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        findViewById(R.id.tv_text).postDelayed(runnable,2500);
//        findViewById(R.id.tv_text).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                consumeCpu();
//            }
//        },3*5000);

        anrTestBroadcast = AnrTestBroadcast.register(this);
        jankView = findViewById(R.id.jankView);
        findViewById(R.id.tvTestJank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jankView.setJank(true);
            }
        });
        findViewById(R.id.singleLongTimeTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送消息的目的是提前让消息队列处理严重耗时任务，导致广播不能及时被接收处理
                mainHandler.post(() -> {
                    SystemClock.sleep(12000);
                });
                AnrTestBroadcast.sentBroadcast(MainActivity.this);
            }
        });
        findViewById(R.id.multiLongTimeTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送多个不是非常严重耗时消息，模拟消息队列繁忙
                for (int i = 25; i > 0; i--) {
                    mainHandler.post(() -> {
                        SystemClock.sleep(500);
                    });
                }
                AnrTestBroadcast.sentBroadcast(MainActivity.this);
            }
        });
        findViewById(R.id.otherProcessAnr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnrTestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(anrTestBroadcast);
    }

    private void consumeCpu() {
        int i = 1;
        while (i < 50) {
            int j = 1;
            while (j < 500_000_000) {
                j++;
            }
            i++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        consumeCpu();
    }

    Object object = new Object();

}