package com.hjq.http.model;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 下载任务
 */
public final class DownloadTask {

    /** 文件对象 */
    private final File mFile;
    /** 总字节数 */
    private long mTotalBytes;
    /** 已下载字节数 */
    private long mCurrentBytes;

    public DownloadTask(File file) {
        mFile = file;
    }

    public File getFile() {
        return mFile;
    }

    public long getTotalBytes() {
        return mTotalBytes;
    }

    public void setTotalBytes(long bytes) {
        mTotalBytes = bytes;
    }

    public long getCurrentBytes() {
        return mCurrentBytes;
    }

    public void setCurrentBytes(long bytes) {
        mCurrentBytes = bytes;
    }

    /**
     * 获取当前进度
     */
    public int getProgress() {
        // 计算百分比，这里踩了两个坑
        // 当文件很大的时候：字节数 * 100 会超过 int 最大值，计算结果会变成负数
        // 还有需要注意的是，long 除以 long 等于 long，这里的字节数除以总字节数应该要 double 类型的
        return (int) (((double) mCurrentBytes / mTotalBytes) * 100);
    }
}