/* 
 * @Title: ScanningHistoryService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;

import com.zhima.base.utils.DateUtils;
import com.zhima.base.utils.UniqueIdGenerator;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ReverseOrderComparator;
import com.zhima.data.model.ScanningHistoryEntry;
import com.zhima.db.dbcontroller.ScanningHistoryDbController;

/**
* @ClassName: ScanningHistoryService
* @Description: 扫码历史处理服务
* @author liubingsr
* @date 2012-8-10 下午5:33:46
*
*/
public class ScanningHistoryService extends BaseService {
	private final static String TAG = "ScanningHistoryService";
	private static ScanningHistoryService mInstance = null;
	private final static long ONE_DAY_MILLISECONDS = 24 * 60 * 60 * 1000;
	private Calendar mCal1;
	private Calendar mCal2;
	private Comparator<ScanningHistoryEntry> mComp;
	
	private ScanningHistoryDbController mDbController;
	private RefreshListData<ScanningHistoryEntry> mRefreshList;

	private ScanningHistoryService(Context context) {
		super(context);
		mDbController = new ScanningHistoryDbController(mContext);
		mCal1 = Calendar.getInstance();
		mCal2 = Calendar.getInstance();
		mComp = new ReverseOrderComparator<ScanningHistoryEntry>();
		onCreate();
	}

	public static ScanningHistoryService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ScanningHistoryService(context);
			mInstance.onCreate();
		}
//		mInstance.mContext = context;
		return mInstance;
	}

	@Override
	public void onCreate() {
		mRefreshList = new RefreshListData<ScanningHistoryEntry>(mDbController.reloadAllDataWithOrder(),mDbController);
	}

	@Override
	public void onDestroy() {
		clear();
		mRefreshList = null;
		System.gc();
	}
	/**
	* @Title: getScanningHistoryOfDay
	* @Description: 获取指定日期的拍码历史记录
	* @param day 日期值
	* @return
	* ArrayList<ScanningHistoryEntry>
	*/
	public ArrayList<ScanningHistoryEntry> getScanningHistoryOfDay(Date day) {
		long destDay = day.getTime();
		return getScanningHistoryOfDay(destDay);
	}
	/**
	* @Title: getScanningHistoryOfDay
	* @Description: 获取指定日期的拍码历史记录
	* @param dayInMillis 日期值(UTC milliseconds)
	* @return
	* ArrayList<ScanningHistoryEntry>
	*/
	public ArrayList<ScanningHistoryEntry> getScanningHistoryOfDay(long dayInMillis) {
		ArrayList<ScanningHistoryEntry> ret = new ArrayList<ScanningHistoryEntry>();
		ArrayList<ScanningHistoryEntry> list = getScanningHistoryList();
		mCal1.setTimeInMillis(dayInMillis);
 		for (ScanningHistoryEntry entry : list) {			
			mCal2.setTimeInMillis(entry.getScanningTime());
			if (DateUtils.isSameDay(mCal1, mCal2)) {
				ret.add(entry);
			}
		}
		return ret;
	}
	/**
	* @Title: getScanningHistoryList
	* @Description: 得到拍码历史记录(按照拍码时间倒序排列)
	* @return
	* ArrayList<ScanningHistoryEntry>
	*/
	public ArrayList<ScanningHistoryEntry> getScanningHistoryList() {
		ArrayList<ScanningHistoryEntry> list = mRefreshList.getDataList();		
        Collections.sort(list, mComp);
		return list;
	}
	/**
	* @Title: getRefreshListData
	* @Description: 得到拍码历史记录
	* @return
	* RefreshListData<ScanningHistoryEntry>
	*/
	public RefreshListData<ScanningHistoryEntry> getRefreshListData() {
		return mRefreshList;
	}
	/**
	* @Title: clearRefreshListData
	* @Description: 清空数据
	* @return void
	*/
	public void clearRefreshListData() {
		mRefreshList = null;
		mRefreshList = new RefreshListData<ScanningHistoryEntry>();
	}
	/**
	* @Title: addScanningLog
	* @Description: 添加一条拍码历史记录(未登录用户不保存)
	* @param title 标题
	* @param zmCode 码
	* @return void
	*/
	public void addScanningHistoryEntry(String title, String zmCode) {
		if (!AccountService.getInstance(mContext).isLogin()) {
			return;
		}
		long userId = AccountService.getInstance(mContext).getUserId();
		if (userId <= 0) {
			return;
		}
		ScanningHistoryEntry entry = new ScanningHistoryEntry();
		entry.setId(UniqueIdGenerator.getInstance().genericId());
		entry.setTitle(title);
		entry.setZMCode(zmCode);
		entry.setUserId(userId);
//		mDbController.updateData(entry);
		mRefreshList.add(entry);
	}
	/**
	* @Title: deleteScanningHistoryEntry
	* @Description: 删除一条拍码历史记录
	* @param entryId 要删除的记录id
	* @return boolean
	*/
	public void deleteScanningHistoryEntry(long entryId) {
		mRefreshList.delete(entryId);
	}
	/**
	* @Title: deleteAll
	* @Description: 删除所有的拍码历史记录
	* @return void
	*/
	public void deleteAll() {
		mRefreshList.clear();
		mDbController.deleteAll();
	}

	@Override
	public void clear() {
		if (mRefreshList != null) {
			mRefreshList.clear();
		}		
	}
}
