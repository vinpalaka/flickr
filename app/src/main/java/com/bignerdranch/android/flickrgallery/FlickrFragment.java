package com.bignerdranch.android.flickrgallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FlickrFragment extends Fragment {

    private static final String ARG_FLICKR_ID = "flickr_id";
    private FlickrImage mFlickrImage;
    private ImageView mImageView;
    private TextView mEditText;
    private PictureDownloader<FlickrImage> mPictureDownloader;

    public static FlickrFragment newInstance(String crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FLICKR_ID, crimeId);
        FlickrFragment fragment = new FlickrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FlickrFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String crimeId = (String) getArguments().getSerializable(ARG_FLICKR_ID);
        mFlickrImage = FlickrUtil.get(getActivity()).getImage(crimeId);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        Handler responseHandler = new Handler();
        mPictureDownloader = new PictureDownloader<>(responseHandler);
        mPictureDownloader.setTPictureDownloaderListener(
                new PictureDownloader.PictureDownloaderListener<FlickrImage>() {
                    @Override
                    public void onThumbnailDownloaded(FlickrImage photoHolder,
                                                      Bitmap bitmap) {
                        Drawable mDr = new BitmapDrawable(getResources(), bitmap);
                        mImageView.setImageDrawable(mDr);
                    }
                }
        );
        mPictureDownloader.start();
        mPictureDownloader.getLooper();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.flickr_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        if(mFlickrImage != null) {
            if(mFlickrImage.getImageView() == null) {
                Drawable placeholder = getResources().getDrawable(R.drawable.waiting);
                mImageView.setImageDrawable(placeholder);
            } else {
                Bitmap bg = mFlickrImage.getImageView();
                Drawable drawable = new BitmapDrawable(getResources(), bg);
                mImageView.setImageDrawable(drawable);
            }
            mEditText = (TextView) v.findViewById(R.id.image_title);
            mEditText.setText(mFlickrImage.getTitle());
            if(mFlickrImage.getUrl() != null) {
                mPictureDownloader.queuePicture(mFlickrImage, mFlickrImage.getUrl());
            }
        } else {
            Drawable placeholder = getResources().getDrawable(R.drawable.waiting);
            mImageView.setImageDrawable(placeholder);
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.flickr_photo, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                QueryPreferences.setStoredQuery(getActivity(), s);
                updateItems();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<FlickrImage>> {
        private String mQuery;
        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<FlickrImage> doInBackground(Void... params) {
            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<FlickrImage> items) {
            FlickrUtil.get(getActivity()).setList(items);
            Intent intent = FlickrPagerActivity.newIntent(getActivity());
            startActivity(intent);

        }
    }
    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPictureDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPictureDownloader.quit();
        Log.i("Picture", "Background thread destroyed");
    }

}
