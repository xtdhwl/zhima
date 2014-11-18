/* 
 * @Title: ZMContent.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMContent
 * @Description: 内容模型
 * @author liubingsr
 * @date 2012-5-17 下午7:24:52
 * 
 */
public class ZMContent extends ZMContentBase {
	private final static String TAG = "ZMContent";

	/**
	 * 回复次数
	 */
	protected int mReplyCount = 0;
	/**
	 * 浏览次数
	 */
	protected int mViewCount = 0;
	/**
	 * 转发次数
	 */
	protected int mForwardCount = 0;

	public int getReplyCount() {
		return this.mReplyCount;
	}
	public void setReplyCount(int replyCount) {
		this.mReplyCount = replyCount;
	}

	public int getViewCount() {
		return this.mViewCount;
	}
	public void setViewCount(int viewCount) {
		this.mViewCount = viewCount;
	}

	public int getForwardCount() {
		return this.mForwardCount;
	}
	public void setForwardCount(int forwardCount) {
		this.mForwardCount = forwardCount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:" + mId);
		sb.append(",content:" + mContent);
		sb.append(",imageUrl:" + mImageUrl);
		sb.append(",status:" + mStatus);
		sb.append(",replyCount:" + mReplyCount);
		sb.append(",viewCount:" + mViewCount);
		sb.append(",forwardCount:" + mForwardCount);
		sb.append(",postTime:" + mPostTime);
		return sb.toString();
	}
	
	public void create(JSONObject jsonObject) throws JSONException {
		mId = jsonObject.getLong("id");
		if (!jsonObject.isNull("author")) {
			mAuthor = User.parse(jsonObject.getJSONObject("author"));
		}
		if (!jsonObject.isNull("summary")) {
			mContent = StringHelper.jsonNullToEmpty(jsonObject.getString("summary"));
		}
		if (!jsonObject.isNull("imageUrl")) {
			mImageUrl = StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl"));
		}
		if (!jsonObject.isNull("forwardCount")) {
			mForwardCount = jsonObject.getInt("forwardCount");
		}
		if (!jsonObject.isNull("replyCount")) {
			mReplyCount = jsonObject.getInt("replyCount");
		}		
		if (!jsonObject.isNull("viewCount")) {
			mViewCount = jsonObject.getInt("viewCount");
		}
		if (!jsonObject.isNull("createdOn")) {
			mPostTime = jsonObject.getLong("createdOn");
		}		
	}
	/**
	 * @Title: parse
	 * @Description: 解析出对象
	 * @param jsonObject
	 * @return null 解析失败
	 */
	public static ZMContent parse(JSONObject jsonObject) {
		ZMContent content = new ZMContent();
		try {
			content.create(jsonObject);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			content = null;
		}
		return content;
	}
}
