/* 
 * @Title: RegionService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.RegionProtocolHandler.SyncRegionDictProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.Region;
import com.zhima.db.dbcontroller.CityDbController;
import com.zhima.db.dbcontroller.NewCityDbController;
import com.zhima.db.dbcontroller.ProvinceDbController;
import com.zhima.db.dbcontroller.RegionDbController;

/**
 * @ClassName: RegionService
 * @Description: 地区
 * @author liubingsr
 * @date 2012-8-8 下午5:15:07
 * 
 */
public class RegionService extends BaseService {
	private final static String TAG = "RegionService";
	private static RegionService mInstance = null;
	
	private ProvinceDbController mProvinceDbController;
	private RefreshListData<Region> mProvinceList;
	private CityDbController mCityDbController;
	private RegionDbController mRegionDbController;
	private RefreshListData<Region> mDict;
	private NewCityDbController mCityDb;

	private TreeMap<Long, ArrayList<Region>> mCityMap = new TreeMap<Long, ArrayList<Region>>();
	private TreeMap<Long, ArrayList<Region>> mRegionMap = new TreeMap<Long, ArrayList<Region>>();

	private RegionService(Context context) {
		super(context);
		mProvinceDbController = new ProvinceDbController(mContext);
		mCityDbController = new CityDbController(mContext);
		mRegionDbController = new RegionDbController(mContext);
		mDict = new RefreshListData<Region>();
		// onCreate();
	}

	public static RegionService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new RegionService(context);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mCityDb = new NewCityDbController(mContext);
		ArrayList<Region> array = mCityDb.loadAllProvinces();
		mProvinceList = new RefreshListData<Region>(array);
		ArrayList<Region> provinceList = mProvinceList.getDataList();
		long parentId = 0L;
		ArrayList<Region> list;
		for (Region province : provinceList) {
			parentId = province.getId();
			Region all = new Region();
			all.setId(ZMConsts.ALL_ID);
			all.setName("全部");
			all.setParentId(parentId);
			list = mCityDb.loadCitys(parentId);
			list.add(0, all);
			mCityMap.put(parentId, list);
		}
		Set<Long> provinceIdList = mCityMap.keySet();
		for (Long provinceId : provinceIdList) {
			ArrayList<Region> cityList = mCityMap.get(provinceId);
			for (Region city : cityList) {
				parentId = city.getId();
				Region all = new Region();
				all.setId(ZMConsts.ALL_ID);
				all.setName("全部");
				all.setParentId(parentId);
				if (parentId == ZMConsts.ALL_ID) {				
					list = new ArrayList<Region>();
					list.add(all);				
				} else {
					list = mCityDb.loadCountys(parentId);
					list.add(0, all);
				}
				mRegionMap.put(parentId, list);
			}
		}
	}
	/**
	 * @Title: getProvinceList
	 * @Description: 省(直辖市)
	 * @return ArrayList<Region>
	 */
	public ArrayList<Region> getProvinceList() {
		return mProvinceList.getDataList();		
	}
	/**
	* @Title: getProvinceList
	* @Description: 得到具有指定状态的省份列表
	* @param isOpen  开通标注。true:已开通
	* @return ArrayList<Region>
	*/
	public ArrayList<Region> getProvinceList(boolean isOpen) {
		if (mProvinceList == null) {
			onCreate();
		}
		ArrayList<Region> array = new ArrayList<Region>();
		for (Region item : mProvinceList.getDataList()) {
			if (item.isOpen()) {
				array.add(item);
			}
		}
		return array;
	}	
	/**
	 * @Title: getCityList
	 * @Description: 返回省(直辖市)所有的城市(区)
	 * @param provinceId 省(直辖市)Id
	 * @return ArrayList<Region>
	 */
	public ArrayList<Region> getCityList(long provinceId, boolean needAll) {
		if (needAll) {
			return mCityMap.get(provinceId);
		}
		ArrayList<Region> array = mCityMap.get(provinceId);
//		if (array.size() == 1) {
//			return array;
//		}
		int start = 1, end = array.size() - 1;
		if (start > end) {
			return new ArrayList<Region>();
		}
		return new ArrayList<Region>(array.subList(1, array.size() - 1));
		
	}
	/**
	 * @Title: getRegionList
	 * @Description: 返回城市(区)所有的区县
	 * @param cityId 城市(区)Id
	 * @return ArrayList<Region>
	 */
	public ArrayList<Region> getRegionList(long cityId, boolean needAll) {
		if (needAll) {
			return mRegionMap.get(cityId);
		}
		ArrayList<Region> array = mRegionMap.get(cityId);
//		if (array.size() == 1) {
//			return array;
//		}
		int start = 1, end = array.size() - 1;
		if (start > end) {
			return new ArrayList<Region>();
		}		
		return new ArrayList<Region>(array.subList(1, array.size() - 1));		
	}	
	/**
	 * @Title: getRegionById
	 * @Description:通过地区的id获取到地区（省市区）
	 * @param
	 * @return String
	 */
	public String getRegionById(long id){
		if (mProvinceList != null && mProvinceList.getData(id) != null) {
			return mProvinceList.getData(id).getName();
		}
		if (mRegionMap.containsKey(id)) {
			Collection<ArrayList<Region>> cityValueList = mCityMap.values();
			for (ArrayList<Region> entryList : cityValueList) {
				for (Region entry : entryList) {
					if (entry.getId() == id) {
						return entry.getName();
					}
				}
			}
		}
		Collection<ArrayList<Region>> regionValueList = mRegionMap.values();
		for (ArrayList<Region> entryList : regionValueList) {
			for (Region entry : entryList) {
				if (entry.getId() == id) {
					return entry.getName();
				}
			}
		}
		return null;
	}
	/**
	* @Title: getRegionStr
	* @Description: 由区域id得到省、市、区名称
	* @param id
	* @return
	* String
	*/
	public String getRegionStr(long id) {
		String region = "";
		long parentId = 0;
		Collection<ArrayList<Region>> regionValueList = mRegionMap.values();
		for (ArrayList<Region> entryList : regionValueList) {
			for (Region entry : entryList) {
				if (entry.getId() == id) {
					region = entry.getName();
					parentId = entry.getParentId();
					break;
				}
			}
		}
		String city = "";
		long cityId = 0L;
		if (parentId > 0) {
			// 是区一级，再找出它的上级城市
			cityId = parentId;
			parentId = 0;
		} else {
			// 是城市级
			cityId = id;
			parentId = 0;
		}
		// 取出城市名称
		Collection<ArrayList<Region>> cityValueList = mCityMap.values();
		for (ArrayList<Region> entryList : cityValueList) {
			for (Region entry : entryList) {
				if (entry.getId() == cityId) {
					city = entry.getName();
					parentId = entry.getParentId();
					break;
				}
			}
		}
		if (parentId > 0) {
			// 省级
			id = parentId;
		}
		String province = "";
		if (mProvinceList != null && mProvinceList.getData(id) != null) {
			province = mProvinceList.getData(id).getName();
		}		
		return province + "-" + city + "-" + region;
	}
	/**
	* @Title: getCityIdByName
	* @Description: 由GPS提供者得到的城市名称查找出对应的cityId值
	* @param cityName
	* @return 0:没有找到
	*/
	public long getCityIdByName(String cityName) {
		// TODO
		if (!TextUtils.isEmpty(cityName)) {
			cityName = cityName.trim();
			if (cityName.equals("")) {
				return 0;
			}
			ArrayList<Region> regionList = mProvinceList.getDataList();
			for (Region region : regionList) {
				if (cityName.equals(region.getName())) {
					return region.getId();
				}
			}
			Collection<ArrayList<Region>> cityValueList = mCityMap.values();
			for (ArrayList<Region> entryList : cityValueList) {
				for (Region region : entryList) {
					if (cityName.equals(region.getName())) {
						return region.getId();
					}
				}
			}
			Collection<ArrayList<Region>> regionValueList = mRegionMap.values();
			for (ArrayList<Region> entryList : regionValueList) {
				for (Region region : entryList) {
					if (cityName.equals(region.getName())) {
						return region.getId();
					}
				}
			}
		}
		return 0;
	}
	public void addRegion(ArrayList<Region> list) {
		System.currentTimeMillis();
//		if (list.isEmpty() || list.get(0) == null) {
//			return;
//		}
//		ArrayList<Region> provinceList = list.get(0).getSubRegion();
//		if (provinceList == null || provinceList.isEmpty()) {
//			return;
//		}
//		mProvinceList.clear();
//		mProvinceDbController.deleteAll();
//		mCityMap.clear();
//		mCityDbController.deleteAll();
//		mRegionMap.clear();
//		mRegionDbController.deleteAll();
//		
//		mProvinceList.add(provinceList);		
//		mProvinceDbController.updateDataList(provinceList);
//		long parentId = 0;
//		for (Region province : provinceList) {
//			ArrayList<Region> cityList = province.getSubRegion();
//			parentId = province.getId();
//			mCityMap.put(parentId, cityList);
//			mCityDbController.updateDataList(cityList);
//			
//			Region all = new Region();
//			all.setId(ZMConsts.ALL_ID);
//			all.setName("全部");
//			all.setParentId(parentId);
//			cityList.add(0, all);
//			
//			for (Region city : cityList) {
//				ArrayList<Region> regionList = city.getSubRegion();
//				parentId = city.getId();
//				mRegionMap.put(parentId, regionList);
//				mRegionDbController.updateDataList(regionList);		
//				
//				Region subAll = new Region();
//				subAll.setId(ZMConsts.ALL_ID);
//				subAll.setName("全部");
//				subAll.setParentId(parentId);
//				if (parentId == ZMConsts.ALL_ID) {		
//					regionList.add(subAll);	
//				} else {
//					regionList.add(0, subAll);
//				}				
//			}
//		}
	}
	
	public void sync(long lastTimestamp, IHttpRequestCallback callBack) {
		mDict.clear();
		SyncRegionDictProtocol protocol = new SyncRegionDictProtocol(mContext, true, mDict, callBack);
		protocol.sync(lastTimestamp);
	}

	@Override
	public void clear() {
		if (mProvinceList != null) {
			mProvinceList.clear();
		}
		if (mCityMap != null) {
			mCityMap.clear();
		}
		if (mRegionMap != null) {
			mRegionMap.clear();
		}
		if (mDict != null) {
			mDict.clear();
		}
	}

	@Override
	public void onDestroy() {
		clear();
		mProvinceList = null;
		mCityMap = null;
		mRegionMap = null;
		mDict = null;
		System.gc();
	}
}
