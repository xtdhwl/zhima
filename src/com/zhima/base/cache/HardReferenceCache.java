package com.zhima.base.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Bitmap;

import com.zhima.base.config.SystemConfig;

/**
 * @ClassName: HardReferenceCache
 * @Description: 硬引用缓存
 * @author liubingsr
 * @date 2012-6-14 下午4:42:03
 * 
 */
public class HardReferenceCache implements IZMMemCache<String, Bitmap> {
	private static final int DEFAULT_CACHE_SIZE = SystemConfig.IMAGE_CACHE_SIZE; // 60
	private ArrayList<HardCacheItem> mCache;
	private Object mLock = new Object();
	private int mCacheSize;
	private Map<String, Bitmap> mCache2 = null;

	private void init() {
		mCache = new ArrayList<HardCacheItem>();
		mCache2 = new HashMap<String, Bitmap>();
	}
	public HardReferenceCache() {
		this.mCacheSize = DEFAULT_CACHE_SIZE;
		init();
	}

	public HardReferenceCache(int cacheSize) {
		this.mCacheSize = cacheSize;
		init();
	}

	@Override
	public void add(String key, Bitmap value) {
		synchronized (mLock) {
//			HardCacheItem newItem = new HardCacheItem(key, value);
//			mCache.remove(newItem);
//			while (mCache.size() >= mCacheSize) {
//				recycle(mCache.get(0));
//				mCache.remove(0);
//			}
//			mCache.add(newItem);
			Iterator<Entry<String, Bitmap>> it = null;
			Bitmap bmp = null;
			String destKey;
			while (mCache2.size() >= mCacheSize) {
				it = mCache2.entrySet().iterator();
				if (it.hasNext()) {
					Entry<String, Bitmap> entry = it.next();
					destKey = entry.getKey();
					recycle(mCache2.get(destKey));
					mCache2.remove(destKey);
				}
			}
			mCache2.put(key, value);
		}
	}

	@Override
	public Bitmap get(String key) {
		synchronized (mLock) {
//			for (HardCacheItem item : mCache) {
//				if (item.mUri.equals(key)) {
//					return item.mBitmap;
//				}
//			}
//			return null;
			return mCache2.get(key);
		}
	}

	@Override
	public void remove(String key) {
		synchronized (mLock) {
//			for (HardCacheItem item : mCache) {
//				if (item.mUri.equals(key)) {
//					recycle(item);
//					mCache.remove(item);
//					return;
//				}
//			}
			Bitmap bmp = mCache2.get(key);
			recycle(bmp);
			mCache2.remove(key);
		}
	}

	@Override
	public int getSize() {
		return mCache2.size();//mCache.size();
	}

	@Override
	public ArrayList<String> getKeys() {
		ArrayList<String> keyList = new ArrayList<String>();
//		for (HardCacheItem item : mCache) {
//			keys.add(item.mUri);
//		}
		Set<String> keySet = mCache2.keySet();
		for (String key : keySet) {
			keyList.add(key);
		}
		return keyList;
	}

	@Override
	public void clear() {
//		for (HardCacheItem item : mCache) {
//			recycle(item);
//		}
		Set<String> keySet = mCache2.keySet();
		for (String key : keySet) {
			recycle(mCache2.get(key));
		}
		mCache2.clear();
		System.gc();
	}

	private void recycle(HardCacheItem item) {
		if (item != null && item.mBitmap != null) {
			if (!item.mBitmap.isRecycled()) {
				item.mBitmap.recycle();
				item.mBitmap = null;
			}
		}
	}
	private void recycle(Bitmap bmp) {
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();			
		}
		bmp = null;
	}

	class HardCacheItem {
		public Bitmap mBitmap;
		public String mUri;

		public HardCacheItem(String uri, Bitmap bmp) {
			this.mBitmap = bmp;
			this.mUri = uri;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof HardCacheItem)) {
				return false;
			}
			HardCacheItem tmpItem = (HardCacheItem) o;
			return tmpItem.mUri.equals(mUri);
		}
	}
}
