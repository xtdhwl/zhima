/* 
 * @Title: NewCityDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zhima.data.model.Region;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.BaseTable;
import com.zhima.db.provider.ZhimaDatabase.NewCityTable;
import com.zhima.db.provider.ZhimaDatabase.RegionBase;
import com.zhima.db.utils.DatabaseUtil;

/**
 * @ClassName: NewCityDbController
 * @Description: 地区基类DbController
 * @author liubingsr
 * @date 2012-6-11 下午2:36:57
 * 
 */
public final class NewCityDbController  {
	private Context mContext;
	private  Uri mUri;
	private NewCityTable mDatabaseTable = null;
	private String[] mProjection = { NewCityTable.ID, NewCityTable.CITY_NAME,
			NewCityTable.PYNAME, NewCityTable.PARENT_ID,
			NewCityTable.IS_OPEN, NewCityTable.POSTCODE};
	
	public NewCityDbController(Context context) {
		mContext = context;
		mDatabaseTable = new ZhimaDatabase.NewCityTable();
		mUri = mDatabaseTable.getContentUri();
	}
	
	public ArrayList<Region> loadAllProvinces() {
		ArrayList<Region> array = new ArrayList<Region>();
//		String sql = "select * from " + ZhimaDatabase.TABLE_NEW_CITY + " where t.id & 0xF = 2";
		String selection = "(" + NewCityTable.ID + " & 15 = 2) ";
		
		Cursor cursor = DatabaseUtil.query(mContext, mUri, mProjection, selection, null, null);
		if (cursor != null) {
			int index;
			Region entry;
			while (cursor.moveToNext()) {
				entry = new Region();
				index = 0;
				entry.setId(cursor.getLong(index++));
				entry.setName(cursor.getString(index++));
				entry.setPinYinName(cursor.getString(index++));
				entry.setParentId(cursor.getLong(index++));
				entry.setIsOpen(cursor.getInt(index++) == 1);
				entry.setPostCode(cursor.getInt(index++));
				array.add(entry);
			}
			cursor.close();
		}
		return array;
	}
	/**
	* @Title: loadProvinces
	* @Description: 省份数据
	* @param isOpen 开通标志
	* @return
	* ArrayList<Region>
	*/
	public ArrayList<Region> loadProvinces(boolean isOpen) {
		ArrayList<Region> array = new ArrayList<Region>();
//		String sql = "select * from " + ZhimaDatabase.TABLE_NEW_CITY + " where t.id & 0xF = 2";
		String selection = "(" + NewCityTable.ID + " & 15 = 2) AND (" + NewCityTable.IS_OPEN + " = " + (isOpen ? 1 : 0) + ")";
		
		Cursor cursor = DatabaseUtil.query(mContext, mDatabaseTable.getContentUri(), mProjection, selection, null, null);
		if (cursor != null) {
			int index;
			Region entry;
			while (cursor.moveToNext()) {
				entry = new Region();
				index = 0;
				entry.setId(cursor.getLong(index++));
				entry.setName(cursor.getString(index++));
				entry.setPinYinName(cursor.getString(index++));
				entry.setParentId(cursor.getLong(index++));
				entry.setIsOpen(cursor.getInt(index++) == 1);
				entry.setPostCode(cursor.getInt(index++));
				array.add(entry);
			}
			cursor.close();
		}
		return array;
	}
	/**
	* @Title: loadCitys
	* @Description: 得到省所有的城市
	* @param provinceId  省份id
	* @return
	* ArrayList<Region>
	*/
	public ArrayList<Region> loadCitys(long provinceId) {
		ArrayList<Region> array = new ArrayList<Region>();
		
		String selection = "(" + NewCityTable.ID + " & 15 = 3) AND " + NewCityTable.PARENT_ID + " = " + provinceId;
		
		Cursor cursor = DatabaseUtil.query(mContext, mDatabaseTable.getContentUri(), mProjection, selection, null, null);
		if (cursor != null) {
			int index;
			Region entry;
			while (cursor.moveToNext()) {
				entry = new Region();
				index = 0;
				entry.setId(cursor.getLong(index++));
				entry.setName(cursor.getString(index++));
				entry.setPinYinName(cursor.getString(index++));
				entry.setParentId(cursor.getLong(index++));
				entry.setIsOpen(cursor.getInt(index++) == 1);
				entry.setPostCode(cursor.getInt(index++));
				array.add(entry);
			}
			cursor.close();
		}
		return array;
	}
	
	/**
	* @Title: loadCountys
	* @Description: 得到地市所有的区县
	* @param cityId  城市id
	* @return
	* ArrayList<Region>
	*/
	public ArrayList<Region> loadCountys(long cityId) {
		ArrayList<Region> array = new ArrayList<Region>();		
		String selection = "(" + NewCityTable.ID + " & 15 = 4) AND " + NewCityTable.PARENT_ID + " = " + cityId;
		
		Cursor cursor = DatabaseUtil.query(mContext, mDatabaseTable.getContentUri(), mProjection, selection, null, null);
		if (cursor != null) {
			int index;
			Region entry;
			while (cursor.moveToNext()) {
				entry = new Region();
				index = 0;
				entry.setId(cursor.getLong(index++));
				entry.setName(cursor.getString(index++));
				entry.setPinYinName(cursor.getString(index++));
				entry.setParentId(cursor.getLong(index++));
				entry.setIsOpen(cursor.getInt(index++) == 1);
				entry.setPostCode(cursor.getInt(index++));
				array.add(entry);
			}
			cursor.close();
		}
		return array;
	}
	
	/**
	* @Title: loadDistricts
	* @Description: 取区下面的商业区
	* @param countyId  城市id
	* @return
	* ArrayList<Region>
	*/
	public ArrayList<Region> loadDistricts(long countyId) {
		ArrayList<Region> array = new ArrayList<Region>();		
		String selection = "(" + NewCityTable.ID + " & 15 = 5) AND " + NewCityTable.PARENT_ID + " = " + countyId;
		
		Cursor cursor = DatabaseUtil.query(mContext, mDatabaseTable.getContentUri(), mProjection, selection, null, null);
		if (cursor != null) {
			int index;
			Region entry;
			while (cursor.moveToNext()) {
				entry = new Region();
				index = 0;
				entry.setId(cursor.getLong(index++));
				entry.setName(cursor.getString(index++));
				entry.setPinYinName(cursor.getString(index++));
				entry.setParentId(cursor.getLong(index++));
				entry.setIsOpen(cursor.getInt(index++) == 1);
				entry.setPostCode(cursor.getInt(index++));
				array.add(entry);
			}
			cursor.close();
		}
		return array;
	}
	/**
	* @Title: update
	* @Description: 更新一条数据
	* @param region
	* @return boolean
	*/
	public boolean update(Region region) {
		String where = NewCityTable.ID + "=";
		ContentValues values = new ContentValues();
		values.put(BaseTable.ID, region.getId());
		values.put(RegionBase.PARENT_ID, region.getParentId());
		values.put(RegionBase.NAME, region.getName());
		values.put(RegionBase.PYNAME, region.getPinYinName());
		values.put(RegionBase.POSTCODE, region.getPostCode());
		values.put(RegionBase.TAG_ID, region.getCityTagId());
		int count = DatabaseUtil.update(mContext, mUri, values, where, null);
		return true;
	}
	/**
	* @Title: update
	* @Description: 更新一组数据
	* @param regions
	* @return
	* boolean
	*/
	public boolean update(ArrayList<Region> regions) {
		if (regions == null || regions.isEmpty()) {
			return true;
		}
		for (Region region: regions) {
			update(region);
		}
		return true;
	}
}
