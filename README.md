# AnrMonitor  
本工具是一款线下的ANR检测线下工具。

## 实现方案 
技术理论源自于：

[头条ANR系列文章](https://mp.weixin.qq.com/mp/appmsgalbum?action=getalbum&album_id=1780091311874686979)

[今日头条 ANR 优化实践系列](https://juejin.cn/post/6942665216781975582#heading-16)

<img src="https://github.com/tigerface/AnrMonitor/blob/main/picture/ANR_T.jpg"  style="width:600px" />

参考仓库，并对代码做了重构
[月光宝盒](https://github.com/xiaolutang/MoonlightTreasureBox)

### 目前实现的功能有：
* 收集所有的主线程Message，并保存到内存，最多保存100条
* 消息可以进行聚合处理，每条聚合消息最长时间300ms，不足300ms的消息，聚合为一个消息，特殊消息单独保存为一条消息。
* 检测ANR信号，发生ANR后，收集当前的内存信息、CPU信息、堆栈、和pending 消息，并序列保存消息队列到硬盘。

#### 保存的消息类型有：

* 普通消息，消息类型为INFO。
* 耗时超过3000ms的消息，此类消息单独保存，此类消息类型为 WARN。
* 卡顿消息（HandlerName为android.view.Choreographer$FrameHandler）的耗时消息，此类消息单独保存，此类消息类型为 JANK。
* 两个消息的间隔超过50ms，此类消息单独保存，此类消息类型为GAP。
* 生ANR时的消息，单独保存，此类消息类型为ANR。
* 最后，收集未处理的Pending消息，此类消息类型为PENDING.

## 初始化AnrMonitor
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
	
Step 2. Add the dependency
```
dependencies {
  implementation 'com.github.tigerface:AnrMonitor:v1.0.1'
}
```
Step 3. init AnrMonitor

```
 AnrMonitor.install(this, new DefaultAnrConfig())
 	   .setDebuggable(true)
	   .start();
 ```

## 查看ANR日志

	
查看logcat中是否产生ANR日志，保存路径如下：

```
12-28 13:12:11.822 D 8468     8824     ANR_FileCache:      cache path : /storage/emulated/0/Android/data/com.example.test/cache/block_anr 
12-28 13:12:11.825 E 8468     8825     ANR_FileCache:      cacheData file : /storage/emulated/0/Android/data/com.example.test/cache/block_anr/2022-12-28-13:12:11:823_anr.txt 
12-28 13:12:11.825 I 8468     8468     ANR_DISPATCHER:     collect anr system information end. 
12-28 13:12:11.826 E 8468     8825     ANR_FileCache:      cacheData success 

```

在Test工程下提供了一个Html可以简单查看消息队列。可以在电脑端打开并加载
\AnrMonitor\Test\src\main\assets\index.html


![](https://github.com/tigerface/AnrMonitor/blob/main/picture/ANR_0.jpg)

![](https://github.com/tigerface/AnrMonitor/blob/main/picture/ANR_2.jpg)


## TODO List
* 头条的实现方案中，设置了CheckTime线程机制，用于检测系统的整体卡顿情况。该功能暂时未加。
* 消息队列数据保存到SD卡中，目前使用简单的html显示时间线，有需要的可自行开发。
* 通过 adb 发送广播的方式实时收集堆栈信息
* 未区分Release和Debug版本。
