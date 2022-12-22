package com.tigerface.perf.anrmonitor.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


import com.tigerface.perf.anrmonitor.entity.BoxMessage;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class FileUtil {

    private static final String TAG = "ANR_" + FileUtil.class.getSimpleName();
    private static final FileCache fileCache = new FileCache();
    private static final FileUtil instance = new FileUtil();
    private static final String SUFFIX = "_sample.txt";

    private FileUtil() {

    }

    public static FileUtil getInstance(Context context) {
        fileCache.init(context, "block_anr", 20, "1.0.0");
        return instance;
    }

    public void saveSampleData(String info) {
        String path = FileCache.sFormat.format(new Date()) + SUFFIX;
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                fileCache.cacheData(path, info);
            }
        });
    }

    public void saveAnrData(String info) {
        String path = FileCache.sFormat.format(new Date()) + "_anr.txt";
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                fileCache.cacheData(path, info);
            }
        });
    }

    public boolean existSampleFile() {
        //20秒内无Sample日志
        return fileCache.existSampleFile(20 * 1000 * 1000);
    }

    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File value : files) {
                        return deleteFile(value);
                    }
                }
            }
            return file.delete();
        }
        return false;
    }

    public static void closeStream(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class FileCache {
        private static final String TAG = "ANR_" + FileCache.class.getSimpleName();
        private File diskCacheDir;

        /**
         * 指定目录下最多能够存储多少个文件
         */
        private int maxSize = 4;
        private int currentSize;
        private static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");

        private FileCache() {
        }

        private File getDiskCacheDir(Context context, String uniqueName) {
            boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            final String cachePath;
            if (externalStorageAvailable) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + uniqueName);
        }

        public void init(Context context, String rootDir, int maxSize, String appVersion) {
            if (maxSize > 2) {
                this.maxSize = maxSize;
            }
            diskCacheDir = getDiskCacheDir(context, rootDir);
            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }
            Log.d(TAG, "cache path : " + diskCacheDir.getPath());
            File[] files = diskCacheDir.listFiles();
            currentSize = files == null ? 0 : files.length;
            long m = getUsableSpace(diskCacheDir);
        }

        public synchronized void cacheData(String path, String content) {
            //如果文件不存在就创建文件
            File file = new File(diskCacheDir.getPath() + File.separator + path);
            Log.e(TAG, "cacheData file : " + file.getAbsolutePath());
            //获取输出流
            //这里如果文件不存在会创建文件，  如果文件存在，新写会覆盖以前的内容吗？
            BufferedOutputStream bs = null;
            try {
                bs = new BufferedOutputStream(new FileOutputStream(file, true));
                bs.write(content.getBytes());
                bs.flush();
                Log.e(TAG, "cacheData success");
            } catch (IOException e) {
                Log.e(TAG, "cacheData cause error:" + e.getMessage());
                e.printStackTrace();
            } finally {
                FileUtil.closeStream(bs);
            }
            currentSize = diskCacheDir.listFiles() == null ? 0 : diskCacheDir.listFiles().length;
            Log.d(TAG, "removeLastFile currentSize:" + currentSize + ",maxSize: " + maxSize);
            if (currentSize > maxSize) {
                removeLastFile();
            }
        }

        private synchronized void removeLastFile() {
            File[] files = diskCacheDir.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return (int) (o1.lastModified() - (o2.lastModified()));
                }
            });
            //把最小的移除掉
            Log.d(TAG, "removeLastFile file name: " + files[0].getName());
            FileUtil.deleteFile(files[0]);
        }

//        public synchronized List<T> restoreData() {
//            List<T> result = new ArrayList<>();
//            File[] files = diskCacheDir.listFiles();
//            if (files == null) {
//                return result;
//            }
//            for (File file : files) {
//                ObjectInputStream ois = null;
//                try {
//                    //获取输入流
//                    ois = new ObjectInputStream(new FileInputStream(file));
//                    //获取文件中的数据
//                    T data = (T) ois.readObject();
//                    result.add(data);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (ois != null) {
//                            ois.close();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            return result;
//        }


        private long getUsableSpace(File path) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                return path.getUsableSpace();
            }
            final StatFs statFs = new StatFs(path.getPath());
            return statFs.getBlockSizeLong() + statFs.getAvailableBlocksLong();
        }

        public boolean existSampleFile(long maxDuration) {
            File[] files = diskCacheDir.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return (int) (o2.lastModified() - o1.lastModified());
                }
            });
            if (files.length > 0) {
                File lastFile = files[0];
                Log.d(TAG, "currentTimeMillis: " + System.currentTimeMillis() + " ,lastMode " + lastFile.lastModified() + " " + lastFile.getName());
                return lastFile.getName().contains(SUFFIX) && (System.currentTimeMillis() - lastFile.lastModified() < maxDuration);
            }
            return false;
        }
    }
}
