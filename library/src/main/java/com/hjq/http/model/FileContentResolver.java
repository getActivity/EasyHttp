package com.hjq.http.model;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2021/04/18
 *    desc   : 文件内容解析器
 */
public class FileContentResolver extends FileWrapper {

    private final ContentResolver mContentResolver;
    private final Uri mFileUri;

    public FileContentResolver(@NonNull ContentResolver resolver, Uri uri) {
        super(new File(uri.toString()));
        mContentResolver = resolver;
        mFileUri = uri;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return mContentResolver.openInputStream(mFileUri);
    }

    @Override
    public OutputStream getOutputStream() throws FileNotFoundException {
        return mContentResolver.openOutputStream(mFileUri);
    }

    @Override
    public boolean delete() {
        return mContentResolver.delete(mFileUri, null, null) > 0;
    }

    @Override
    public boolean renameTo(@NonNull File dest) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean mkdir() {
        return true;
    }

    @Override
    public boolean mkdirs() {
        return true;
    }

    @Nullable
    @Override
    public File getParentFile() {
        return null;
    }

    @Override
    public boolean setLastModified(long time) {
        return false;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Nullable
    @Override
    public String[] list() {
        return null;
    }

    @Nullable
    @Override
    public String[] list(@Nullable FilenameFilter filter) {
        return null;
    }

    @Nullable
    @Override
    public File[] listFiles() {
        return null;
    }

    @Nullable
    @Override
    public File[] listFiles(@Nullable FileFilter filter) {
        return null;
    }

    @Nullable
    @Override
    public File[] listFiles(@Nullable FilenameFilter filter) {
        return null;
    }
}