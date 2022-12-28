package com.tigerface.perf.anrmonitor.collectors;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

import java.util.ArrayList;

public class MemoryCollector implements Collector {
    private static final String TAG = "ANR_" + MemoryCollector.class.getSimpleName();

    @Override
    public String collect(Context context, BoxMessage message) {
        Log.i(TAG, "collect memory info,msgId:" + message.getId());
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("msgId: ").append(message.getId()).append("</br>");
        Runtime runtime = Runtime.getRuntime();
        //返回【Java虚拟机】的最大内存：从系统可挖最大内存
        sb.append("本应用 maxMemory:")
                .append(runtime.maxMemory() / 1024 / 1024).append("Mb").append("</br>");
        //返回【Java虚拟机】的内存总量：从系统已挖的总内存
        sb.append("本应用 totalMemory:")
                .append(runtime.totalMemory() / 1024 / 1024).append("Mb").append("</br>");
        //返回【Java虚拟机】中可用内存的数量：从系统已挖但还没用的内存
        sb.append("本应用 freeMemory:")
                .append(runtime.freeMemory() / 1024 / 1024).append("Mb").append("</br>");
        sb.append("本应用 当前使用:")
                .append((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024)
                .append("Mb").append("</br>");


        //adb shell cat /proc/meminfo 命令行可直接获取
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        //【系统内核】上可用的内存
        sb.append("系统 availMem:").append(memoryInfo.availMem / 1024 / 1024).append("Mb").append("</br>");
        //【系统内核】可访问的总内存
        sb.append("系统 totalMem:").append(memoryInfo.totalMem / 1024 / 1024).append("Mb").append("</br>");
        //如果系统认为自己当前处于低内存，则设置为true
        sb.append("系统 lowMemory:").append(memoryInfo.lowMemory).append("</br>");
        if(message.getMemoryInfoList() == null){
            message.setMemoryInfoList(new ArrayList<>());
        }
        message.getMemoryInfoList().add(sb.toString());
        return sb.toString();
    }
}
