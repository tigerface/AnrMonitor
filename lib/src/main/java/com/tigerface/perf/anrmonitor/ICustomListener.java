package com.tigerface.perf.anrmonitor;

import com.tigerface.perf.anrmonitor.entity.BoxMessage;

/**
 * 用户自定义回调
 */
public interface ICustomListener {

    /**
     * 发生ANR
     *
     * @param message
     */
    void onAnr(BoxMessage message);

    /**
     * UI 卡顿
     *
     * @param message
     */
    void onJank(BoxMessage message);

    /**
     * 处理消息超时
     *
     * @param message
     */
    void onTimeOutWarn(BoxMessage message);

}
