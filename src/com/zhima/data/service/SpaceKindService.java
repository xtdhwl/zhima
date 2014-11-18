/* 
 * @Title: SpaceKindService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.SpaceKindProtocolHandler.SyncSpacekindDictProtocol;
import com.zhima.base.utils.SettingHelper;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.Spacekind;
import com.zhima.db.dbcontroller.SpaceMainDbController;
import com.zhima.db.dbcontroller.SpaceRootDbController;
import com.zhima.db.dbcontroller.SpaceSubTypeDbController;

/**
 * @ClassName: SpaceKindService
 * @Description: 空间类型
 * @author liubingsr
 * @date 2012-8-8 下午5:15:07
 * 
 */
public class SpaceKindService extends BaseService {
	private final static String TAG = "SpaceKindService";
	private static SpaceKindService mInstance = null;
	
	private SpaceRootDbController mRootTypeDbController;	
	private SpaceMainDbController mMainTypeDbController;
	private SpaceSubTypeDbController mSubTypeDbController;
	
	private RefreshListData<Spacekind> mRootTypeList;	
	private RefreshListData<Spacekind> mDict;

	private TreeMap<Long, ArrayList<Spacekind>> mMainTypeMap = new TreeMap<Long, ArrayList<Spacekind>>();
	private TreeMap<Long, ArrayList<Spacekind>> mSubTypeMap = new TreeMap<Long, ArrayList<Spacekind>>();	

	private SpaceKindService(Context context) {
		super(context);
		mRootTypeDbController = new SpaceRootDbController(mContext);
		mMainTypeDbController = new SpaceMainDbController(mContext);
		mSubTypeDbController = new SpaceSubTypeDbController(mContext);
		mDict = new RefreshListData<Spacekind>();		
		// onCreate();
	}

	public static SpaceKindService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SpaceKindService(context);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mRootTypeList = new RefreshListData<Spacekind>(mRootTypeDbController.reloadAllDataWithOrderAsc(), mRootTypeDbController);
		ArrayList<Spacekind> rootList = mRootTypeList.getDataList();
		long parentId = 0;
		ArrayList<Spacekind> list;
		for (Spacekind rootKind : rootList) {
			parentId = rootKind.getId();
			Spacekind all = new Spacekind();
			all.setId(ZMConsts.ALL_ID);
			all.setName("全部");
			all.setParentId(parentId);
			list = mMainTypeDbController.reloadAllDataWithOrderAsc(parentId);
			list.add(0, all);
			mMainTypeMap.put(parentId, list);
		}
		Set<Long> rootIdList = mMainTypeMap.keySet();
		for (Long rootId : rootIdList) {
			ArrayList<Spacekind> mainList = mMainTypeMap.get(rootId);
			for (Spacekind mainType : mainList) {
				parentId = mainType.getId();
				Spacekind all = new Spacekind();
				all.setId(ZMConsts.ALL_ID);
				all.setName("全部");
				all.setParentId(parentId);
				if (parentId == ZMConsts.ALL_ID) {					
					list = new ArrayList<Spacekind>();
					list.add(all);				
				} else {
					list = mSubTypeDbController.reloadAllDataWithOrderAsc(parentId);
					list.add(0, all);
				}
				mSubTypeMap.put(parentId, list);
			}
		}
	}
	
	@Override
	public void onDestroy() {
		clear();
		mRootTypeList = null;
		mMainTypeMap = null;
		mSubTypeMap = null;
		mDict = null;
		System.gc();
	}

	/**
	 * @Title: getRootTypeList
	 * @Description: 空间根类型
	 * @return
	 */
	public ArrayList<Spacekind> getRootTypeList() {
		if (mRootTypeList == null) {
			onCreate();
		}
		return mRootTypeList.getDataList();
	}
	/**
	* @Title: getMainTypeList
	* @Description: 根类型获取主类新列表
	* @param rootId 根类型Id
	* @return
	* ArrayList<Spacekind>
	*/
	public ArrayList<Spacekind> getMainTypeList(long rootId) {
		return mMainTypeMap.get(rootId);
	}	
	/**
	 * @Title: getSubTypeList
	 * @Description: 返回主类型下面的子类型
	 * @param mainTypeId 主类型Id
	 * @return
	 */
	public ArrayList<Spacekind> getSubTypeList(long mainTypeId) {
		return mSubTypeMap.get(mainTypeId);
	}
	
	/**
	* @Title: saveTimestamp
	* @Description: 保存最新的时间戳
	* @param lastTimestamp
	* @return void
	*/
	public void saveTimestamp(long lastTimestamp) {
		SettingHelper.setLong(mContext, SettingHelper.Field.DICT_SPACE_TIMESTAMP, lastTimestamp);
	}
	
	public void addSpacekind(ArrayList<Spacekind> list) {		
		if (list.isEmpty() || list.get(0) == null) {
			return;
		}
		mRootTypeList.clear();
		mRootTypeDbController.deleteAll();
		mMainTypeMap.clear();
		mMainTypeDbController.deleteAll();
		mSubTypeMap.clear();
		mSubTypeDbController.deleteAll();
		for (Spacekind kind : list) {
			mRootTypeList.add(kind);
		}
		mRootTypeDbController.updateDataList(mRootTypeList.getDataList());
		long parentId = 0;
		for (Spacekind rootType : mRootTypeList.getDataList()) {
			ArrayList<Spacekind> mainTypeList = rootType.getSubSpacekind();
			parentId = rootType.getId();
			mMainTypeMap.put(parentId, mainTypeList);
			mMainTypeDbController.updateDataList(mainTypeList);
			
			Spacekind all = new Spacekind();
			all.setId(ZMConsts.ALL_ID);
			all.setName("全部");
			all.setParentId(parentId);
			mainTypeList.add(0, all);
			
			for (Spacekind subType : mainTypeList) {
				ArrayList<Spacekind> subTypeList = subType.getSubSpacekind();
				parentId = subType.getId();
				mSubTypeMap.put(parentId, subTypeList);
				mSubTypeDbController.updateDataList(subTypeList);
				
				Spacekind subAll = new Spacekind();
				subAll.setId(ZMConsts.ALL_ID);
				subAll.setName("全部");
				subAll.setParentId(parentId);
				if (parentId == ZMConsts.ALL_ID) {				
					subTypeList.add(subAll);			
				} else {
					subTypeList.add(0, subAll);
				}
			}
		}
	}
	
	public void sync(long lastTimestamp, IHttpRequestCallback callBack) {
		mDict.clear();
		SyncSpacekindDictProtocol protocol = new SyncSpacekindDictProtocol(mContext, true, mDict, callBack);
		protocol.sync(lastTimestamp);
	}
	@Override
	public void clear() {
		if (mRootTypeList != null) {
			mRootTypeList.clear();
		}
		if (mMainTypeMap != null) {
			mMainTypeMap.clear();
		}
		if (mSubTypeMap != null) {
			mSubTypeMap.clear();
		}
		if (mDict != null) {
			mDict.clear();
		}
	}
}
