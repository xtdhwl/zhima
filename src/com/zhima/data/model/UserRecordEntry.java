/* 
 * @Title: UserRecordEntry.java
 * Created by liubingsr on 2012-7-27 上午10:11:09 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: UserRecordEntry
 * @Description: 个人管理条目
 * @author liubingsr
 * @date 2012-9-3 下午5:19:51
 * 
 */
public class UserRecordEntry extends BaseData {
	private final static String TAG = "UserRecordEntry";
	/**
	 * 条目id
	 */
	private long mId = 0;
	/**
	 * 对象标识
	 */
	private long mTargetId = 0;
	/**
	 * 对象类型
	 */
	private int mTargetType = -1;
	/**
	 * 条目类型
	 * 
	 * @see com.zhima.base.consts.ZMConsts.PersonRecordType
	 */
	private int mRecordType = -1;
	/**
	 * 标题
	 */
	private String mTitle = "";
	/**
	 * 内容
	 */
	private String mContent = "";
	/**
	 * 服务器返回的图片原始url
	 */
	private String mImageUrl = "";
	/**
	 * 产生时间
	 */
	private long mCreateTime = System.currentTimeMillis();

	@Override
	public long getId() {
		return mId;
	}

	public long setId(long id) {
		return mId = id;
	}

	public long getTargetId() {
		return mTargetId;
	}
	public void setTargetId(long targetId) {
		this.mTargetId = targetId;
	}

	public int getTargetType() {
		return mTargetType;
	}
	public void setTargetType(int targetType) {
		this.mTargetType = targetType;
	}

	public int getRecordType() {
		return mRecordType;
	}
	public void setRecordType(int recordType) {
		this.mRecordType = recordType;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getContent() {
		return mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String uploadFlag) {
		this.mImageUrl = uploadFlag;
	}

	public long getCreateTime() {
		return mCreateTime;
	}
	public void setCreateTime(long createTime) {
		this.mCreateTime = createTime;
	}

	public static UserRecordEntry parse(JSONObject jsonObject) {
		UserRecordEntry record = new UserRecordEntry();
		try {
			record.setId(jsonObject.getLong("id"));
			record.setRecordType(jsonObject.getInt("recordType"));
			record.setTargetType(jsonObject.getInt("targetType"));
			record.setTargetId(jsonObject.getLong("targetId"));
			record.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("title")));
			if (!jsonObject.isNull("content")) {
				record.setContent(StringHelper.jsonNullToEmpty(jsonObject.getString("content")));
			}
			if (!jsonObject.isNull("imageUrl")) {
				record.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}
			record.setCreateTime(jsonObject.getLong("createdOn"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return record;
	}
}
