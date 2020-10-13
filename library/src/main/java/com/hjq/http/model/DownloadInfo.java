package com.hjq.http.model;

import com.hjq.http.EasyUtils;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/05/19
 *    desc   : 下载信息
 */
public final class DownloadInfo {

    /** 总字节数 */
    private long mTotalByte;

    /** 已下载字节数 */
    private long mDownloadByte;

    public long getTotalByte() {
        // 如果没有获取到下载内容的大小，就直接返回已下载字节大小
        if (mTotalByte <= 0) {
            return mDownloadByte;
        }
        return mTotalByte;
    }

    public void setTotalByte(long size) {
        mTotalByte = size;
    }

    public long getDownloadByte() {
        return mDownloadByte;
    }

    public void setDownloadByte(long size) {
        mDownloadByte = size;
    }

    /**
     * 获取当前进度
     */
    public int getDownloadProgress() {
        return EasyUtils.getProgressPercent(mTotalByte, mDownloadByte);
    }
}