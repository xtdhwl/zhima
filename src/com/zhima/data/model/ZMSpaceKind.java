/* 
* @Title: ZMSpaceKind.java
* Created by liubingsr on 2013-1-15 下午3:46:25 
* Copyright (c) 2013 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMSpaceKind
 * @Description: 芝麻空间
 * @author liubingsr
 * @date 2013-1-15 下午3:46:25
 *
 */
public class ZMSpaceKind extends BaseData {
	private final static String TAG = "ZMSpaceKind";
	/**
	 * id
	 */
	private long mId = 0;
	/**
	 * 类型名称
	 */
	private String mName = "";
	/**
	 * 父分类
	 */
	private long mParentId = 0;
	/**
	 * 是否有子分类
	 */
	private boolean mHasChild = false;
	/**
	 * 排序
	 */
	private int mSequenceNumber = 0;
	/**
	 * 路径
	 */
	private String mPath = "";	
	/**
	 * 描述
	 */
	private String mDescription = "";
	/**
	 * 深度
	 */
	private int mDepth = 0;
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		mId = id;
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
	public void setParentId(long id) {
		this.mParentId = id;
	}
	
	public boolean isHasChild() {
		return mHasChild;
	}
	public void setHasChild(boolean hasChild) {
		this.mHasChild = hasChild;
	}
	
	public int getSequenceNumber() {
		return mSequenceNumber;
	}
	public void setSequenceNumber(int number) {
		this.mSequenceNumber = number;
	}		
	
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		this.mDescription = description;
	}
	
	public String getPath() {
		return mPath;
	}
	public void setPath(String path) {
		this.mPath = path;
	}
	
	public int getDepth() {
		return mDepth;
	}
	public void setDepth(int depth) {
		this.mDepth = depth;
	}
	
	public static ZMSpaceKind parse(JSONObject jsonObject) {
		ZMSpaceKind obj = new ZMSpaceKind();
		try {
			if (!jsonObject.isNull("id")) {
				obj.setId(jsonObject.getLong("id"));
			}			
			if (!jsonObject.isNull("name")) {
				obj.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			}
			if (!jsonObject.isNull("parentId")) {
				obj.setParentId(jsonObject.getLong("parentId"));
			}
			if (!jsonObject.isNull("hasChild")) {
				obj.setHasChild(jsonObject.getBoolean("hasChild"));
			}
			if (!jsonObject.isNull("sequenceNumber")) {
				obj.setSequenceNumber(jsonObject.getInt("sequenceNumber"));
			}
			if (!jsonObject.isNull("path")) {
				obj.setPath(StringHelper.jsonNullToEmpty(jsonObject.getString("path")));
			}
			if (!jsonObject.isNull("description")) {
				obj.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}
			if (!jsonObject.isNull("depth")) {
				obj.setDepth(jsonObject.getInt("depth"));
			}

		} catch (JSONException e) {
			Logger.getInstance(TAG ).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}	
}
