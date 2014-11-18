/* 
 * @Title: PingService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import android.content.Context;

import com.zhima.base.config.SystemConfig;
import com.zhima.base.utils.SettingHelper;

/**
* @ClassName: PingService
* @Description: Ping服务
* @author liubingsr
* @date 2012-9-22 下午3:05:45
*
*/
public class PingService extends BaseService {
	private final static String TAG = "PingService";
	private static PingService mInstance = null;
	private int mInterval;

	private PingService(Context context) {
		super(context);
		mInterval = SettingHelper.getInt(mContext, SettingHelper.Field.PING_INTERVAL, SystemConfig.PING_INTERVAL);
	}

	public static PingService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new PingService(context);
		}
//		mInstance.mContext = context;
		return mInstance;
	}
	
	public void updatePingInterval(int seconds) {
		if (seconds > 0) {
			SettingHelper.setInt(mContext, SettingHelper.Field.PING_INTERVAL, seconds);
			mInterval = seconds;
		}
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
