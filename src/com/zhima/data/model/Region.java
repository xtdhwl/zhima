/* 
 * @Title: Region.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: Region
 * @Description: 区域
 * @author liubingsr
 * @date 2012-6-7 下午6:16:12
 * 
 */
public final class Region extends BaseData {
	private final static String TAG = "Region";
	/**
	 * id
	 */
	private long mId;
	/**
	 * 父id
	 */
	private long mParentId;
	/**
	 * 名称
	 */
	private String mName;
	/**
	 * 城市拼音
	 */
	private String mPinYinName;
	/**
	 * 邮编
	 */
	private int mPostCode;
	/**
	 * 商圈id
	 */
    private long mCityTagId;
    /**
     * 是否开通
     */
    private boolean mIsOpen = false;
	/**
     * 下级列表
     */
    private ArrayList<Region> mSubRegion;

	public Region() {
		mId = 0;
		mName = "";
		mParentId = 0;
		mPinYinName = "";
		mPostCode = 0;
		mCityTagId = 0;
		mSubRegion = new ArrayList<Region>();
	}
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}
	
	public long getParentId() {
		return mParentId;
	}
	public void setParentId(long parentId) {
		this.mParentId = parentId;
	}

	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getPinYinName() {
		return mPinYinName;
	}
	public void setPinYinName(String pinYinName) {
		this.mPinYinName = pinYinName;
	}
	
	public int getPostCode() {
		return mPostCode;
	}
	public void setPostCode(int postCode) {
		this.mPostCode = postCode;
	}
	
	public long getCityTagId() {
		return mCityTagId;
	}
	public void setCityTagId(long tagId) {
		this.mCityTagId = tagId;
	}

	public ArrayList<Region> getSubRegion() {
		return mSubRegion;
	}
	public void setSubRegion(ArrayList<Region> subRegion) {
		this.mSubRegion = subRegion;
	}
	
	public boolean isOpen() {
		return mIsOpen;
	}
	public void setIsOpen(boolean open) {
		this.mIsOpen = open;
	}
	/**
	* @Title: parse
	* @Description: json数据解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static Region parse(JSONObject jsonObject) {
		Region obj = new Region();
		try {
			obj.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("parentId")) {
				obj.setParentId(jsonObject.getLong("parentId"));
			}
			if (!jsonObject.isNull("cityName")) {
				obj.setName(jsonObject.getString("cityName"));
			}
			if (!jsonObject.isNull("ename")) {
				obj.setPinYinName(jsonObject.getString("ename"));
			}
			if (!jsonObject.isNull("zipCode")) {
				obj.setPostCode(jsonObject.getInt("zipCode"));
			}
			if (!jsonObject.isNull("cityTagId")) {
				obj.setCityTagId(jsonObject.getLong("cityTagId"));
			}
			if (!jsonObject.isNull("subCity")) {
				JSONArray subArray = jsonObject.getJSONArray("subCity");
				for (int index = 0, count = subArray.length(); index < count; ++index) {
					JSONObject item = subArray.getJSONObject(index);
					Region subRegion = Region.parse(item);
					if (subRegion != null) {
						obj.getSubRegion().add(subRegion);
					}
				}
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
