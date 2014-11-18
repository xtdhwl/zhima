package com.zhima.base.cache;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

/**
* @ClassName: SoftReferenceCache
* @Description: 软引用缓存
* @author liubingsr
* @date 2012-6-14 下午5:01:59
*
*/
public class SoftReferenceCache implements IZMMemCache<String, Bitmap> {
    private ConcurrentHashMap<String, SoftReference<Bitmap>> mCache;   

    public SoftReferenceCache() {
        mCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
    }
    
    @Override
	public void add(String key, Bitmap value) {
		mCache.put(key, new SoftReference<Bitmap>(value));
	}
    
    @Override
    public void remove(String key) {
        mCache.remove(key);
    }    
    
    @Override
    public Bitmap get(String key) {
        SoftReference<Bitmap> bmp = mCache.get(key);
        if (bmp != null && bmp.get() != null && !bmp.get().isRecycled()) {
            return bmp.get();
        }
        if (bmp != null && bmp.get() != null && bmp.get().isRecycled()) {
        	mCache.remove(key);
        }
        return null;
    }

    @Override
    public int getSize() {
        return mCache.size();
    }

	@Override
	public ArrayList<String> getKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		for (Entry<String, SoftReference<Bitmap>> entry : mCache.entrySet()) {
			keys.add(entry.getKey());
		}
		return keys;
	}
	
	@Override
    public void clear() {
        mCache.clear();
        System.gc();
    }
}