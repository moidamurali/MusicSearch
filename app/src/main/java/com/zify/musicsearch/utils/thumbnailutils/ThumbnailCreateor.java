package com.zify.musicsearch.utils.thumbnailutils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.zify.musicsearch.MusicSearchApplication;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Murali on 17/12/2019.
 */

public class ThumbnailCreateor {




    public static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>
    {
        private long data;
        private String videoData;
        private WeakReference<ImageView> imageViewReference;
        private String url;
        public BitmapWorkerTask(ImageView view, String url)
        {
            imageViewReference = new WeakReference<ImageView>(view);
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String...videoId) {
            /*this.data = Long.parseLong(videoId[0]);*/
            this.videoData = videoId[0];
            Bitmap found = BitmapCache.GetInstance().getBitmapFromDiskCache(videoData);
            if (found != null)
                return found;

            Bitmap bitmap =null;
            try{
                URL ulrn = new URL(videoId[0]);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                bitmap = BitmapUtil.scaleDownBitmap(MusicSearchApplication.getAppContext(),bitmap,105);
                if (null != bitmap)
                    return bitmap;

            }catch(Exception e){}

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            BitmapCache.GetInstance().AddBitmapToCache(url, bm);
            if (imageViewReference != null)
            {
                ImageView imageView = imageViewReference.get();
                if (imageView != null)
                {
                    BitmapWorkerTask bitmapDownloaderTask = getBitmapWorkerTask(imageView);
                    if (this == bitmapDownloaderTask) {
                        imageView.setImageBitmap(bm);
                    }
                }
            }
        }

    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }


    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }


    public static boolean cancelPotentialWork(long data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final long bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

}
