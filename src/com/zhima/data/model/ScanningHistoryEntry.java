/* 
 * @Title: ScanningHistoryEntry.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

/**
 * @ClassName: ScanningHistoryEntry
 * @Description: 扫码历史记录
 * @author liubingsr
 * @date 2012-6-11 上午11:11:18
 * 
 */
public final class ScanningHistoryEntry extends BaseData {
	/**
	 * id
	 */
	private long mId = 0;
	/**
	 * 用户id
	 */
	private long mUserId = 0;
	/**
	 * 标题
	 */
	private String mTitle = "";
	/**
	 * 码内容
	 */
	private String mZMCode = "";
	/**
	 * 纬度
	 */
	protected double mLatitude = 0.0;
	/**
	 * 经度
	 */
	protected double mLongitude = 0.0;
	/**
	 * 上传标志。false：未上传
	 */
	private boolean mUploadFlag = false;
	/**
	 * 拍码时间
	 */
	private long mScanningTime = System.currentTimeMillis();

	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}

	public long getUserId() {
		return this.mUserId;
	}
	public void setUserId(long userId) {
		this.mUserId = userId;
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getZMCode() {
		return mZMCode;
	}
	public void setZMCode(String code) {
		this.mZMCode = code;
	}

	public double getLatitude() {
		return mLatitude;
	}
	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}
	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	public boolean getUploadFlag() {
		return this.mUploadFlag;
	}
	public void setUploadFlag(boolean uploadFlag) {
		this.mUploadFlag = uploadFlag;
	}

	public long getScanningTime() {
		return this.mScanningTime;
	}
	public void setScanningTime(long scanningTime) {
		this.mScanningTime = scanningTime;
	}
}
