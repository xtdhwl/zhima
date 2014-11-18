package com.zhima.base.cache;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImageCache {
    protected IZMMemCache<String, Bitmap> mGenericCache;
    protected HashMap<Integer, Bitmap> mSolidCache;    
    protected Context mContext;
    private int mIndex;
    
    private static ImageCache mImageCache = null;

    protected ImageCache() {
    	mIndex = 0;
        mGenericCache = new SoftReferenceCache();//HardReferenceCache();//;
        mSolidCache = new HashMap<Integer, Bitmap>();
    }

    public static ImageCache getInstance() {
        if (mImageCache == null) {
            mImageCache = new ImageCache();
        }
        return mImageCache;
    }

    public void setupSolidCache(Context c, Integer[] resIds) {
        mContext = c;
        new DecodeTask().execute(resIds);
    }

    public void insertSolid(int index, Bitmap bmp) {
    	if (bmp != null) {
    		mSolidCache.put(index, bmp);
    	}
    }

    public Bitmap getSolidBitmap(int solidIndex) {
        return mSolidCache.get(solidIndex);
    }    

    public void add(String uri, Bitmap bmp) {
        if (bmp != null) {
        	if (mGenericCache.get(uri) == null) {
        		mGenericCache.add(uri, bmp);
        	}
        }
    }    
    
    public ArrayList<String> getAllUri() {
    	return mGenericCache.getKeys();
    }

    public Bitmap getBitmapFromSoft(String uri) {
        return mGenericCache.get(uri);
    }

    public void removeSoftCache(String uri) {
        mGenericCache.remove(uri);
    }
    
    public void clearCache(String uri) {
        mGenericCache.remove(uri);
    }
    
    public int getSoftCacheSize() {
        return mGenericCache.getSize();
    }
    
    public void recycle(String uri) {
//    	Bitmap bmp = mGenericCache.get(uri);
//    	if (bmp != null) {
//    		bmp.recycle();
//    	}
//    	bmp = null;
    	mGenericCache.remove(uri);
    }
    
    class DecodeTask extends AsyncTask<Integer, Bitmap, Void> {
        @Override
        protected Void doInBackground(Integer... resIds) {
            for (int resId : resIds) {
                Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resId);
                this.publishProgress(bmp);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Bitmap... bmp) {
            super.onProgressUpdate(bmp);
            insertSolid(mIndex++, bmp[0]);
        }
    }
}
