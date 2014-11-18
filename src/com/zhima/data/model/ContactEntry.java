/* 
* @Title: ContactEntry.java
* Created by liubingsr on 2012-7-27 上午10:11:09 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ContactEntry
 * @Description: 通讯录条目(通讯录目标类型是：除去3000件商品、非3000件商品之外其它类型的空间)
 * @author liubingsr
 * @date 2012-7-27 上午10:11:09
 *
 */
public class ContactEntry extends BaseData {
	private final static String TAG = "ContactEntry";
	/**
	 * 条目Id
	 */
	private long mId = 0;
	/**
	 * 用户id
	 */
	private long mUserId = 0;
	/**
	 * 对象标识
	 */
	private long mObjectId = 0;
	/**
	 * 对象类型。自有码及知天使
	 * @see com.zhima.base.consts.ZMConsts.TargetType
	 */
	private int mObjectType = 0;
	/**
	 * 名称
	 */
	private String mTitle = "";
	/**
	 * 电话号码
	 */
	private String mTelephone = "";
	/**
	 * 服务器返回的图片原始url
	 */
	private String mImageUrl = "";
	/**
	 * 加入时间
	 */
	private long mTimestamp = System.currentTimeMillis();
	/**
	 * 是否已经上传
	 */
	private boolean mUpload = false;
	
	@Override
	public long getId() {
		return mId;
	}
	public long setId(long id) {
		return mId = id;
	}
	
	public long getUserId() {
		return this.mUserId;
	}
	public void setUserId(long userId) {
		this.mUserId = userId;
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}
	
	public String getTelephone() {
		return mTelephone;
	}
	public void setTelephone(String telephone) {
		this.mTelephone = telephone;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String uploadFlag) {
		this.mImageUrl = uploadFlag;
	}
	
	public long getObjectId() {
		return mObjectId;
	}
	public void setObjectId(long objectId) {
		this.mObjectId = objectId;
	}
	
	public int getObjectType() {
		return mObjectType;
	}
	public void setObjectType(int objectType) {
		this.mObjectType = objectType;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}
	
	public boolean isUpload() {
		return mUpload;
	}
	public void setUpload(boolean upload) {
		this.mUpload = upload;
	}
	
	/**
	 * @Title: parse
	 * @Description: 解析出对象
	 * @param jsonObject
	 * @return null 解析错误
	 */
	public static ContactEntry parse(JSONObject jsonObject) {
		ContactEntry entry = new ContactEntry();
		try {
			entry.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("title")) {
				entry.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("title")));
			}
			if (!jsonObject.isNull("telephone")) {
				entry.setTelephone(StringHelper.jsonNullToEmpty(jsonObject.getString("telephone")));
			}
			if (!jsonObject.isNull("targetType")) {
				entry.setObjectType(jsonObject.getInt("targetType"));
			}
			if (!jsonObject.isNull("targetId")) {
				entry.setObjectId(jsonObject.getLong("targetId"));
			}
			if (!jsonObject.isNull("imageUrl")) {
				entry.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}			
		} catch (JSONException e) {
			Logger.getInstance(TAG ).debug(e.getMessage(), e);
			return null;
		}
		return entry;
	}
}
