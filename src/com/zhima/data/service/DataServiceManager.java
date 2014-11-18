/* 
* @Title: DataServiceManager.java
* Created by liubingsr on 2012-8-17 下午3:53:53 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.service;

import java.util.ArrayList;

import com.zhima.base.logger.Logger;
import com.zhima.data.model.Region;
import com.zhima.db.dbcontroller.NewCityDbController;

import android.content.Context;

/**
 * @ClassName: DataServiceManager
 * @Description: Service管理器
 * @author liubingsr
 * @date 2012-8-17 下午3:53:53
 *
 */
public class DataServiceManager {
	private final static String TAG = "DataServiceManager";
	private static DataServiceManager mInstance = null;
	
	private Context mContext;
	private ArrayList<Region> mProvinces = null;
	
	private DataServiceManager(Context context) {
		startService();
//		NewCityDbController cityDb = new NewCityDbController(context);
//		mProvinces = cityDb.loadProvinces(true);
		int  i = 1;
		int j = i;
	}
	
	private void startService() {
		Logger.getInstance(TAG).debug("dataservice---startService");
		
		OrderkindService.getInstance(mContext).onCreate();
		RegionService.getInstance(mContext).onCreate();
		SpaceKindService.getInstance(mContext).onCreate();
		IdolJobService.getInstance(mContext).onCreate();
		AccountService.getInstance(mContext).onCreate();
//		DiaryService.getInstance(mContext).onCreate();
//		ZMSpaceService.getInstance(mContext).onCreate();
	}
	
	public static DataServiceManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DataServiceManager(context);
		}
		return mInstance;
	}
	
	public void onDestroy() {
		
		Logger.getInstance(TAG).debug("dataservice---onDestroy");
		
		doDestroy();
		System.gc();
	}
	/**
	* @Title: clearMemCache
	* @Description: 清空内存暂存的数据，但是不Destroy对象
	* @return void
	*/
	public void clearMemCache() {
		
		Logger.getInstance(TAG).debug("dataservice---clearMemCache");
		
		RequestService.getInstance(mContext).clear();
		ZMCouplesService.getInstance(mContext).clear();
		ZMIdolService.getInstance(mContext).clear();
		CommerceService.getInstance(mContext).clear();
		ScanningcodeService.getInstance(mContext).clear();
		//日志Service
		DiaryService.getInstance(mContext).clear();
		SearchService.getInstance(mContext).clear();
		OrderkindService.getInstance(mContext).clear();
		ContactService.getInstance(mContext).clear();
		RegionService.getInstance(mContext).clear();
		SpaceKindService.getInstance(mContext).clear();
		ScanningHistoryService.getInstance(mContext).clear();
		
		UserService.getInstance(mContext).clear();
		AccountService.getInstance(mContext).clear();
		ZMSpaceService.getInstance(mContext).clear();
		// TODO 清空其它service里的暂存数据
		// ...
		System.gc();
	}
	private void doDestroy() {
		Logger.getInstance(TAG).debug("dataservice---doDestroy");
		
		RequestService.getInstance(mContext).onDestroy();
		ZMCouplesService.getInstance(mContext).onDestroy();
		ZMIdolService.getInstance(mContext).onDestroy();
		CommerceService.getInstance(mContext).onDestroy();
		ScanningcodeService.getInstance(mContext).onDestroy();
		//日志Service
		DiaryService.getInstance(mContext).onDestroy();
		SearchService.getInstance(mContext).onDestroy();
		OrderkindService.getInstance(mContext).onDestroy();
		ContactService.getInstance(mContext).onDestroy();
		RegionService.getInstance(mContext).onDestroy();
		SpaceKindService.getInstance(mContext).onDestroy();
		ScanningHistoryService.getInstance(mContext).onDestroy();
		
		UserService.getInstance(mContext).onDestroy();
		AccountService.getInstance(mContext).onDestroy();
		ZMSpaceService.getInstance(mContext).onDestroy();
	}
}
