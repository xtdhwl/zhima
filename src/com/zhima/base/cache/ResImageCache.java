package com.zhima.base.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zhima.R;

public class ResImageCache extends ImageCache {
	private final static String TAG = "ResImageCache";
    private static ResImageCache mImageCache;       

    public final static int DEFAULT_THUMBNAIL = 0;
    public final static int CHECKED = 1;
    public final static int UNCHECKED = 2;
    public final static int LOCKCHECK  = 3;

    private int[] indexs = {
            DEFAULT_THUMBNAIL,
            CHECKED,
            UNCHECKED,
            LOCKCHECK};

    private Integer[] res = {
            R.drawable.default_avatar,
            R.drawable.checkbox2,
            R.drawable.checkbox1,
            R.drawable.checkbox1
    		};
    
    public ResImageCache() {
        super();
    }
    
    public static ResImageCache getInstance() {
        if (mImageCache == null) {
            mImageCache = new ResImageCache();
        }
        return mImageCache;
    } 

    public void setupSolidCache(Context c) {
        super.setupSolidCache(c, res);
    }

    public Bitmap getDefaultThumbnail() {
        return getBitmap(DEFAULT_THUMBNAIL, R.drawable.default_avatar);
    }

    public Bitmap getBitmap(int index, int res) {
        Bitmap bmp = getSolidBitmap(index);
        if (bmp == null && mContext != null && mContext.getResources() != null) {
            bmp = BitmapFactory.decodeResource(mContext.getResources(), res);
            insertSolid(index, bmp);
        }
        return bmp;
    }

    public Bitmap getCheckboxBackground(int checked) {
        if (checked == 0) {
            return getBitmap(CHECKED, R.drawable.checkbox2);
        } else if(checked == 1) {
            return getBitmap(UNCHECKED, R.drawable.checkbox1);
        } else {
        	return getBitmap(LOCKCHECK, R.drawable.checkbox1);
        }
    }
}
