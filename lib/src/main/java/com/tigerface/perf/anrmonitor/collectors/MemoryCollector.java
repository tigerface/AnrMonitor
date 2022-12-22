package com.tigerface.perf.anrmonitor.collectors;

import android.app.ActivityManager;
import android.content.Context;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

public class MemoryCollector implements Collector {
    @Override
    public String collect(Context context, BoxMessage message) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        StringBuilder sb = new StringBuilder();

        Runtime runtime = Runtime.getRuntime();
        //返回【Java虚拟机】的最大内存：从系统可挖最大内存
        sb.append("本应用maxMemory======>")
                .append(runtime.maxMemory() / 1024 / 1024).append("Mb").append("\n");
        //返回【Java虚拟机】的内存总量：从系统已挖的总内存
        sb.append("本应用totalMemory======>")
                .append(runtime.totalMemory() / 1024 / 1024).append("Mb").append("\n");
        //返回【Java虚拟机】中可用内存的数量：从系统已挖但还没用的内存
        sb.append("本应用freeMemory======>")
                .append(runtime.freeMemory() / 1024 / 1024).append("Mb").append("\n");
        sb.append("本应用 当前使用======>")
                .append((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024)
                .append("Mb").append("\n");


        //adb shell cat /proc/meminfo 命令行可直接获取
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        //【系统内核】上可用的内存
        sb.append("系统availMem======>")
                .append(memoryInfo.availMem / 1024 / 1024).append("Mb").append("\n");
        //【系统内核】可访问的总内存
        sb.append("系统totalMem======>")
                .append(memoryInfo.totalMem / 1024 / 1024).append("Mb").append("\n");
        //如果系统认为自己当前处于低内存，则设置为true
        sb.append("系统lowMemory======>")
                .append(memoryInfo.lowMemory).append("\n");
        return sb.toString();
    }
}
