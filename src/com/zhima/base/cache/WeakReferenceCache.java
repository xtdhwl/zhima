package com.zhima.base.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class WeakReferenceCache implements IZMMemCache<String, Bitmap> {

	private HashMap<String, Entry<String, Bitmap>> mWeakMap;
	private ReferenceQueue<Bitmap> mQueue = new ReferenceQueue<Bitmap>();

	public WeakReferenceCache() {
		mWeakMap = new HashMap<String, Entry<String, Bitmap>>();
	}

	private static class Entry<K, V> extends WeakReference<V> {
		K mKey;

		public Entry(K key, V value, ReferenceQueue<V> queue) {
			super(value, queue);
			mKey = key;
		}
	}

	@SuppressWarnings("unchecked")
	private void cleanUpWeakMap() {
		Entry<String, Bitmap> entry = (Entry<String, Bitmap>) mQueue.poll();
		while (entry != null) {
			mWeakMap.remove(entry.mKey);
			entry = (Entry<String, Bitmap>) mQueue.poll();
		}
	}

	@Override
	public synchronized void add(String key, Bitmap value) {
		cleanUpWeakMap();
		mWeakMap.put(key, new Entry<String, Bitmap>(key, value, mQueue));
	}

	@Override
	public synchronized Bitmap get(String key) {
		cleanUpWeakMap();
		Entry<String, Bitmap> entry = mWeakMap.get(key);
		return entry == null ? null : entry.get();
	}

	@Override
	public synchronized void remove(String key) {
		mWeakMap.remove(key);
	}

	@Override
	public int getSize() {
		return mWeakMap.size();
	}

	@Override
	public ArrayList<String> getKeys() {
		cleanUpWeakMap();
		ArrayList<String> keys = new ArrayList<String>();
		for (Map.Entry<String, Entry<String, Bitmap>> entry : mWeakMap.entrySet()) {
			keys.add(entry.getKey());
		}
		return keys;
	}

	public synchronized void clear() {
		mWeakMap.clear();
	}
}
