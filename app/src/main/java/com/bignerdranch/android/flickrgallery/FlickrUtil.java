package com.bignerdranch.android.flickrgallery;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;


public class FlickrUtil {
    private static FlickrUtil sFlickr;
    private Context mContext;
    List<FlickrImage> flickrImages;

    private FlickrUtil(Context context) {
        mContext = context.getApplicationContext();
    }

    public static FlickrUtil get(Context conext) {
        if(sFlickr == null) {
            sFlickr = new FlickrUtil(conext);
        }
        return sFlickr;
    }

    public void setList(List<FlickrImage> list) {
        flickrImages = list;
    }

    public List<FlickrImage> getImages() {
        if( flickrImages == null ) {
            flickrImages = new LinkedList<>();
            FlickrImage flickrImage = new FlickrImage();
            flickrImage.setId("newInstance");
            flickrImage.setTitle("Please search for desired item.");
            flickrImages.add(flickrImage);
        }

        return flickrImages;
    }

    public FlickrImage getImage(String crimeId) {
        FlickrImage image = null;
        for(int i = 0; i < flickrImages.size(); i++) {
            if(crimeId.equals(flickrImages.get(i).getId())) {
                image = flickrImages.get(i);
            }
        }
        return image;
    }
}
