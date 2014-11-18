/* 
 * @Title: GeoCoordinate.java
 * Created by liubingsr on 2012-9-12 下午3:56:57 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: GeoCoordinate
 * @Description: gps坐标
 * @author liubingsr
 * @date 2012-9-12 下午3:56:57
 * 
 */
public class GeoCoordinate implements Serializable {
	/**
	 * @Fields serialVersionUID : TODO(描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 4359217628547539681L;
	private final static String TAG = "GeoCoordinate";
	private final long TIME_OUT = SystemConfig.LOCATION_TIMEOUT;
	/**
	 * 原始GPS经度
	 */
	private double mLongitude = ZMConsts.INVALID_LONGITUDE;
	/**
	 * 原始GPS纬度
	 */
	private double mLatitude = ZMConsts.INVALID_LATITUDE;
	/**
	 * 高德经度
	 */
	private double mGdLongitude = ZMConsts.INVALID_LONGITUDE;
	/**
	 * 高德纬度
	 */
	private double mGdLatitude = ZMConsts.INVALID_LATITUDE;

	/**
	 * 系统最后定位到的时间
	 */
	private long mTime = 0;

	/**
	 * 高德最后定位到的时间
	 */
	private long mGdTime = 0;

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getGdLongitude() {
		return mGdLongitude;
	}

	public void setGdLongitude(double gdLongitude) {
		this.mGdLongitude = gdLongitude;
	}

	public double getGdLatitude() {
		return mGdLatitude;
	}

	public void setGdLatitude(double gdLatitude) {
		this.mGdLatitude = gdLatitude;
	}

//	/**
//	 * @Title: isInvalidGeo
//	 * @Description: 是否是无效的geo值
//	 * @return boolean
//	 */
//	public boolean isInvalidGeo() {
//		if (Math.abs(mLatitude - ZMConsts.INVALID_LATITUDE) <= 0
//				|| Math.abs(mLongitude - ZMConsts.INVALID_LONGITUDE) <= 0
//				|| Math.abs(mLatitudeGd - ZMConsts.INVALID_LATITUDE) <= 0
//				|| Math.abs(mLatitudeGd - ZMConsts.INVALID_LONGITUDE) <= 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	/**
	 * @Title: isMapabcValid
	 * @Description: 高德坐标值是否有效
	 * @return true：有效
	 */
	public boolean isMapabcValid() {
		return Math.abs(mGdLatitude - ZMConsts.INVALID_LATITUDE) > 0
				&& Math.abs(mGdLatitude - ZMConsts.INVALID_LONGITUDE) > 0;
	}

	public boolean isMapabcInvalidTime() {
		long gdTime = getGdTime();
		long currenTime = System.currentTimeMillis();
		return currenTime - gdTime > TIME_OUT;
	}

	/**
	 * @Title: isGpsValid
	 * @Description: 本机gps坐标值是否有效
	 * @return true：有效
	 */
	public boolean isGpsValid() {
		return Math.abs(mLatitude - ZMConsts.INVALID_LATITUDE) > 0
				&& Math.abs(mLongitude - ZMConsts.INVALID_LONGITUDE) > 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("longitude:" + mLongitude + ",latitude:" + mLatitude);
		sb.append("\r\nlongitudeGd:" + mGdLongitude + ",latitudeGd:" + mGdLatitude);
		return sb.toString();
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		this.mTime = time;
	}

	public long getGdTime() {
		return mGdTime;
	}

	public void setGdTime(long gdTime) {
		mGdTime = gdTime;
	}

	/**
	 * @Title: parse
	 * @Description: json解析出对象
	 * @param jsonObject
	 * @return null 解析失败
	 */
	public static GeoCoordinate parse(JSONObject jsonObject) {
		GeoCoordinate obj = new GeoCoordinate();
		try {
			obj.setLatitude(jsonObject.getDouble("latitude"));
			obj.setLongitude(jsonObject.getDouble("longitude"));
			obj.setGdLatitude(jsonObject.getDouble("latitudeGd"));
			obj.setGdLongitude(jsonObject.getDouble("longitudeGd"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
