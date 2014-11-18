/* 
 * @Title: Spacekind.java
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
 * @ClassName: Spacekind
 * @Description: 空间类型
 * @author liubingsr
 * @date 2012-6-11 上午11:37:03
 */
public final class Spacekind extends BaseData {
	private final static String TAG = "Spacekind";
	/**
	 *  id
	 */
	private long mId;
	/**
	 * 名称
	 */
	private String mName;
	/**
	 * 上一级
	 */
	private long mParentId;
	/**
	 * 子类型列表
	 */
	private ArrayList<Spacekind> mSubSpacekind;

	public Spacekind() {
		mId = 0;
		mName = "";
		mParentId = 0;
		mSubSpacekind = new ArrayList<Spacekind>();
	}
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	
	public long getParentId() {
		return mParentId;
	}
	public void setParentId(long parentId) {
		this.mParentId = parentId;
	}
	
	public ArrayList<Spacekind> getSubSpacekind() {
		return mSubSpacekind;
	}
	public void setSubSpacekind(ArrayList<Spacekind> list) {
		this.mSubSpacekind = list;
	}
	/**
	* @Title: parse
	* @Description: 由json数据包解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static Spacekind parse(JSONObject jsonObject) {
		Spacekind obj = new Spacekind();
		try {
			obj.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("parentId")) {
				obj.setParentId(jsonObject.getLong("parentId"));
			}
			if (!jsonObject.isNull("name")) {
				obj.setName(jsonObject.getString("name"));
			}
			if (!jsonObject.isNull("subSpaceKindList")) {
				JSONArray subArray = jsonObject.getJSONArray("subSpaceKindList");
				for (int index = 0, count = subArray.length(); index < count; ++index) {
					JSONObject item = subArray.getJSONObject(index);
					Spacekind subSpacekind = Spacekind.parse(item);
					if (subSpacekind != null) {
						obj.getSubSpacekind().add(subSpacekind);
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
