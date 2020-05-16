package com.hjq.http.model;

import java.io.File;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 下载信息
 */
public final class DownloadInfo {

    /**
     * 文件对象
     */
    private final File mFile;

    /**
     * 总字节数
     */
    private long mTotalLength;

    /**
     * 已下载字节数
     */
    private long mDownloadLength;

    public DownloadInfo(File file) {
        mFile = file;
    }

    public File getFile() {
        return mFile;
    }

    public long getTotalLength() {
        // 如果没有获取到下载内容的大小，就直接返回已下载字节大小
        if (mTotalLength <= 0) {
            return mDownloadLength;
        }
        return mTotalLength;
    }

    public void setTotalLength(long length) {
        mTotalLength = length;
    }

    public long getDownloadLength() {
        return mDownloadLength;
    }

    public void setDownloadLength(long length) {
        mDownloadLength = length;
    }

    /**
     * 获取当前进度
     */
    public int getDownloadProgress() {
        // 计算百分比，这里踩了两个坑
        // 当文件很大的时候：字节数 * 100 会超过 int 最大值，计算结果会变成负数
        // 还有需要注意的是，long 除以 long 等于 long，这里的字节数除以总字节数应该要 double 类型的
        return (int) (((double) getDownloadLength() / getTotalLength()) * 100);
    }
}