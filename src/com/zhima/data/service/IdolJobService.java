/* 
 * @Title: IdolJobService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.IdolJobProtocolHandler.IdolJobDictProtocol;
import com.zhima.base.utils.SettingHelper;
import com.zhima.data.model.IdolJob;
import com.zhima.data.model.RefreshListData;
import com.zhima.db.dbcontroller.IdolJobDbController;

/**
 * @ClassName: IdolJobService
 * @Description: 知天使职业字典
 * @author liubingsr
 * @date 2012-8-8 下午5:15:07
 * 
 */
public class IdolJobService extends BaseService {
	private final static String TAG = "IdolJobDictService";
	private static IdolJobService mInstance = null;
	
	private IdolJobDbController mDbController;	
	private RefreshListData<IdolJob> mDict;

	private IdolJobService(Context context) {
		super(context);
		mDbController = new IdolJobDbController(mContext);
		mDict = new RefreshListData<IdolJob>();
	}

	public static IdolJobService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new IdolJobService(context);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mDict = new RefreshListData<IdolJob>(mDbController.reloadAllDataWithOrderAsc(), mDbController);
	}
	
	@Override
	public void onDestroy() {
		clear();
		mDict = null;
		System.gc();
	}

	/**
	 * @Title: getIdolJobList
	 * @Description: 职业列表
	 * @return
	 */
	public ArrayList<IdolJob> getIdolJobList() {
		if (mDict == null) {
			onCreate();
		}
		return mDict.getDataList();
	}
	/**
	* @Title: getIdolJob
	* @Description:
	* @param id 类型Id
	* @return IdolJob
	*/
	public IdolJob getIdolJob(long id) {
		return mDict.getData(id);
	}	
	/**
	* @Title: saveTimestamp
	* @Description: 保存最新的时间戳
	* @param lastTimestamp
	* @return void
	*/
	public void saveTimestamp(long lastTimestamp) {
		SettingHelper.setLong(mContext, SettingHelper.Field.DICT_IDOL_JOB_TIMESTAMP, lastTimestamp);
	}
	
	public void addIdolJob(ArrayList<IdolJob> list) {		
		if (list.isEmpty() || list.get(0) == null) {
			return;
		}
		mDict.clear();
		mDbController.deleteAll();
		mDict.add(list);
	}
	
	public void sync(long lastTimestamp, IHttpRequestCallback callBack) {
		mDict.clear();
		IdolJobDictProtocol protocol = new IdolJobDictProtocol(mContext, true, mDict, callBack);
		protocol.sync(lastTimestamp);
	}
	@Override
	public void clear() {
		if (mDict != null) {
			mDict.clear();
		}
	}
}
