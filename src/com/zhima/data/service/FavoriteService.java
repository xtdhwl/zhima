/* 
 * @Title: FavoriteService.java
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

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.AddFavoriteProtocol;
import com.zhima.base.utils.DateUtils;
import com.zhima.base.utils.UniqueIdGenerator;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ReverseOrderComparator;
import com.zhima.db.dbcontroller.FavoriteDbController;

/**
* @ClassName: FavoriteService
* @Description: 收藏夹管理服务
* @author liubingsr
* @date 2012-8-10 下午5:33:46
*
*/
public class FavoriteService extends BaseService {
	private final static String TAG = "FavoriteService";
	private static FavoriteService mInstance = null;

	private Calendar mCal1;
	private Calendar mCal2;
	private Comparator<FavoriteEntry> mComp;
	
	private FavoriteDbController mDbController;
	private RefreshListData<FavoriteEntry> mRefreshList;

	private FavoriteService(Context context) {
		super(context);
		mDbController = new FavoriteDbController(mContext);
		mCal1 = Calendar.getInstance();
		mCal2 = Calendar.getInstance();
		mComp = new ReverseOrderComparator<FavoriteEntry>();
//		onCreate();
	}

	public static FavoriteService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new FavoriteService(context);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mRefreshList = new RefreshListData<FavoriteEntry>(mDbController.reloadAllDataWithOrder(),mDbController);
	}

	@Override
	public void onDestroy() {
		clear();
		mRefreshList = null;
		System.gc();
	}
	/**
	* @Title: getFavoritesOfDay
	* @Description: 获取指定日期的收藏项目
	* @param day 日期值
	* @return
	* ArrayList<FavoriteEntry>
	*/
	public ArrayList<FavoriteEntry> getFavoritesOfDay(Date day) {
		long destDay = day.getTime();
		return getFavoritesOfDay(destDay);
	}
	/**
	* @Title: getFavoritesOfDay
	* @Description: 获取指定日期的收藏项目
	* @param dayInMillis 日期值(UTC milliseconds)
	* @return
	* ArrayList<FavoriteEntry>
	*/
	public ArrayList<FavoriteEntry> getFavoritesOfDay(long dayInMillis) {
		ArrayList<FavoriteEntry> ret = new ArrayList<FavoriteEntry>();
		ArrayList<FavoriteEntry> list = getFavoriteList();
		mCal1.setTimeInMillis(dayInMillis);
 		for (FavoriteEntry entry : list) {			
			mCal2.setTimeInMillis(entry.getFavoriteTime());
			if (DateUtils.isSameDay(mCal1, mCal2)) {
				ret.add(entry);
			}
		}
		return ret;
	}
	/**
	* @Title: getFavoriteList
	* @Description: 得到收藏项目(按照加入收藏夹时间倒序排列)
	* @return
	* ArrayList<FavoriteEntry>
	*/
	public ArrayList<FavoriteEntry> getFavoriteList() {
		ArrayList<FavoriteEntry> list = mRefreshList.getDataList();		
        Collections.sort(list, mComp);
		return list;
	}
	/**
	* @Title: getRefreshListData
	* @Description: 得到收藏项目
	* @return
	* RefreshListData<FavoriteEntry>
	*/
	public RefreshListData<FavoriteEntry> getRefreshListData() {
		return mRefreshList;
	}
	/**
	* @Title: clearRefreshListData
	* @Description: 清空数据
	* @return void
	*/
	public void clearRefreshListData() {
		mRefreshList = null;
		mRefreshList = new RefreshListData<FavoriteEntry>();
	}
	/**
	* @Title: addFavoriteEntry
	* @Description: 添加一条收藏项目(未登录用户不保存)
	* @param title 标题
	* @param zmCode 码
	* @return void
	*/
	public void addFavoriteEntry(FavoriteEntry entry) {
		if (entry == null) {
			return;
		}
		long userId = AccountService.getInstance(mContext).getUserId();
		if (userId <= 0) {
			return;
		}
		entry.setId(UniqueIdGenerator.getInstance().genericId());
		entry.setUserId(userId);
//		mDbController.updateData(entry);
		mRefreshList.add(entry);
	}
	/**
	 * @Title: addFavorite
	 * @Description: 添加收藏(收藏目标是非自有码：3000件商品、非3000件商品与知天使)
	 * @param userId 用户id
	 * @param entry 收藏
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void addFavorite(FavoriteEntry entry, IHttpRequestCallback callBack) {
		AddFavoriteProtocol protocol = new AddFavoriteProtocol(mContext, callBack);
		protocol.addFavorite(entry);
	}
	/**
	* @Title: deleteFavoriteEntry
	* @Description: 删除一条收藏项目
	* @param entryId 要删除的记录id
	* @return boolean
	*/
	public void deleteFavoriteEntry(long entryId) {
		mRefreshList.delete(entryId);
	}
	/**
	* @Title: deleteAll
	* @Description: 删除所有的收藏项目
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
