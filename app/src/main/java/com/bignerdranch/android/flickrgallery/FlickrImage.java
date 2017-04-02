package com.bignerdranch.android.flickrgallery;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.UUID;


public class FlickrImage {

    private String mId;
    private String mTitle;
    private String mUrl;

    public Bitmap getImageView() {
        return mImageView;
    }

    public void setImageView(Bitmap imageView) {
        mImageView = imageView;
    }

    private Bitmap mImageView;


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
