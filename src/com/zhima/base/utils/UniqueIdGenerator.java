package com.zhima.base.utils;

import android.text.format.Time;

/**
 * @ClassName: UniqueIdGenerator
 * @Description: 生成唯一的长整数
 * @author liubingsr
 * @date 2012-6-21 上午10:16:24
 * 
 */
public class UniqueIdGenerator {
	private long mLastId = 0;
	private Time mTime;
	public static UniqueIdGenerator mInstance = null;
	
	private UniqueIdGenerator() {
		mTime = new Time();
	}
	public static UniqueIdGenerator getInstance() {
		if (mInstance == null) {
			mInstance = new UniqueIdGenerator();
		}
		return mInstance;
	}

	synchronized public long genericId() {
		mTime.setToNow();
		long id = mTime.toMillis(true);
		if (id <= mLastId) {
			id = mLastId + 1;
		}
		mLastId = id;
		return id;
	}
}
