package com.bignerdranch.android.flickrgallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PictureDownloader<T> extends HandlerThread {
    private static final String TAG = "PictureDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private PictureDownloaderListener<T> mTPictureDownloaderListener;

    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    public interface PictureDownloaderListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }
    public void setTPictureDownloaderListener(PictureDownloaderListener<T> listener) {
        mTPictureDownloaderListener = listener;
    }

    public PictureDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
    public void queuePicture(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            if(mRequestHandler != null) {
                mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                        .sendToTarget();
            }

        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
            mResponseHandler.postAtFrontOfQueue(new Runnable() {
                public void run() {
                    if (mRequestMap.get(target) != url ||
                            mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mTPictureDownloaderListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}