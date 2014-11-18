/* 
* @Title: ZMSpace.java
* Created by liubingsr on 2013-1-15 下午3:46:25 
* Copyright (c) 2013 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMSpace
 * @Description: 芝麻空间
 * @author liubingsr
 * @date 2013-1-15 下午3:46:25
 *
 */
public class ZMSpace extends BaseData implements Serializable {
	/**
	* @Fields serialVersionUID : TODO(描述这个变量表示什么)
	*/
	private static final long serialVersionUID = 3909583941916132625L;
	private final static String TAG = "ZMSpace";
	/**
	 * id
	 */
	private long mSpaceId = 0;
	/**
	 * zmid
	 */
	private String mZMId = "";
	/**
	 * 空间名称
	 */
	private String mName = "";
	/**
	 * 空间描述
	 */
	private String mDescription = "";
	/**
	 * 空间头像
	 */
	private String mImageUrl = "";
	/**
	 * 空间分类
	 * @see com.zhima.base.consts.ZMConsts.ZMObjectKind
	 */
	private long mSpaceKind = 0;
	/**
	 * 空间介绍web url
	 */
	private String mIntroUrl = "";
	/**
	 * 空间是否已经绑定了
	 */
	private boolean mEnabled = false;
	
	@Override
	public long getId() {
		return mSpaceId;
	}
	public void setId(long spaceId) {
		mSpaceId = spaceId;
	}

	public String getZMId() {
		return mZMId;
	}
	public void setZMId(String zmId) {
		this.mZMId = zmId;
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		this.mDescription = description;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}
	
	public long getSpaceKind() {
		return mSpaceKind;
	}
	public void setSpaceKind(long spaceKind) {
		this.mSpaceKind = spaceKind;
	}
	
	public String getIntroUrl() {
		return mIntroUrl;
	}
	public void setIntroUrl(String introUrl) {
		this.mIntroUrl = introUrl;
	}
	
	public boolean isEnabled() {
		return mEnabled;
	}
	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}
	
	public static ZMSpace parse(JSONObject jsonObject) {
		ZMSpace obj = new ZMSpace();
		try {
			obj.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("zmid")) {
				obj.setZMId(StringHelper.jsonNullToEmpty(jsonObject.getString("zmid")));
			}
			if (!jsonObject.isNull("name")) {
				obj.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			}
			if (!jsonObject.isNull("spaceType")) {
				obj.setSpaceKind(jsonObject.getInt("spaceType"));
			}
			if (!jsonObject.isNull("description")) {
				obj.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}
			obj.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));			
			if (!jsonObject.isNull("enabled")) {
				obj.setEnabled(jsonObject.getBoolean("enabled"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG ).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
