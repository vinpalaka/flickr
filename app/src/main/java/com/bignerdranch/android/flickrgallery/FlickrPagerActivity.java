package com.bignerdranch.android.flickrgallery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlickrPagerActivity extends AppCompatActivity {
    private ViewPager mFlickrPager;
    private List<FlickrImage> mFlickrImages;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, FlickrPagerActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flickr_pager);
        mFlickrPager = (ViewPager) findViewById(R.id.flickr_view_pager);
        mFlickrImages = FlickrUtil.get(this).getImages();
        FragmentManager fragmentManager = getSupportFragmentManager();
        String crimeId = QueryPreferences.getStoredQuery(this);
        mFlickrPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                FlickrImage crime = mFlickrImages.get(position);
                return FlickrFragment.newInstance(crime.getId());
            }
            @Override
            public int getCount() {
                return mFlickrImages.size();
            }
        });
        Fragment fragment = fragmentManager.findFragmentById(R.id.flickr_view_pager);
        if (fragment == null) {
            fragment = FlickrFragment.newInstance("");
            fragmentManager.beginTransaction()
                    .add(R.id.flickr_view_pager, fragment)
                    .commit();
        }
    }
}
