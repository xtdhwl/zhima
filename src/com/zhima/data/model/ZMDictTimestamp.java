/* 
* @Title: ZMDictTimestamp.java
* Created by liubingsr on 2012-9-26 下午4:25:49 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

/**
 * @ClassName: ZMDictTimestamp
 * @Description: 字典时间戳定义
 * @author liubingsr
 * @date 2012-9-26 下午4:25:49
 *
 */
public class ZMDictTimestamp {
	private int mDictType = 0;
	private long mTimestamp = 0;
	
	public ZMDictTimestamp() {
	}
	
	public ZMDictTimestamp(int dictType, long timestamp) {
		mDictType = dictType;
		mTimestamp = timestamp;
	}	

	public int getDictType() {
		return mDictType;
	}
	public void setDictType(int type) {
		this.mDictType = type;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}
}
