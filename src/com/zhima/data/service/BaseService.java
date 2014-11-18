/* 
 * @Title: BaseService.java
 * Created by liubingsr on 2012-6-3 上午11:02:45 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import android.content.Context;

import com.zhima.app.ZhimaApplication;

/**
 * @ClassName: BaseService
 * @Description: 基类
 * @author liubingsr
 * @date 2012-6-3 上午11:02:45
 * 
 */
public abstract class BaseService {
	public abstract void onCreate();
	/**
	* @Title: clear
	* @Description: 清空缓存数据
	* @return void
	*/
	public abstract void clear();
	public abstract void onDestroy();

	protected RequestService mRequestService;
	protected Context mContext;

	public BaseService(Context context) {
		mContext = ZhimaApplication.getContext();
		mRequestService = RequestService.getInstance(mContext);
	}
}
