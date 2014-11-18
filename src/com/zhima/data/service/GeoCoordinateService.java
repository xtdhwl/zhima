/* 
 * @Title: GeoCoordinateService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import android.content.Context;

import com.zhima.data.model.GeoCoordinate;

/**
* @ClassName: GeoCoordinateService
* @Description: gps位置坐标服务
* @author liubingsr
* @date 2012-9-22 下午3:05:45
*
*/
public class GeoCoordinateService extends BaseService {
	private final static String TAG = "GeoCoordinateService";
	private static GeoCoordinateService mInstance = null;
	private GeoCoordinate mGeoCoordinate = new GeoCoordinate();

	private GeoCoordinateService(Context context) {
		super(context);
	}
	
	public static GeoCoordinateService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new GeoCoordinateService(context);
		}
//		mInstance.mContext = context;
		return mInstance;
	}	
	public GeoCoordinate getGeo() {		
		return mGeoCoordinate;
	}
	public void setGeo(GeoCoordinate geo) {
		this.mGeoCoordinate = geo;
	}
	
	@Override
	public void onCreate() {
		
	}
	@Override
	public void onDestroy() {
		clear();
		System.gc();
	}
	@Override
	public void clear() {
		
	}
}
