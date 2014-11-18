/* 
 * @Title: VisitorMessage.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: VisitorMessage
 * @Description: 看看谁来过 访客留言
 * @author liubingsr
 * @date 2012-6-8 下午5:20:40
 * 
 */
public final class VisitorMessage extends BaseData {
	private final static String TAG = "VisitorMessage";
	/**
	 * 记录id
	 */
	private long mId = 0;
	/**
	 * 访问记录id
	 */
	private long mVisiteLogId = 0;
	/**
	 * 留言内容
	 */
	private String mContent = "";
	/**
	 * 图片url
	 */
	private String mImageUrl = "";
	/**
	 * 访客
	 */
	private User mVisitor = null;
	/**
	 * 留言时间
	 */
	private long mPostTime = 0;

	public VisitorMessage() {
		mId = 0;
	}

	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}

	public long getVisiteLogId() {
		return this.mVisiteLogId;
	}
	public void setVisiteLogId(long visiteLogId) {
		this.mVisiteLogId = visiteLogId;
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
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	public User getVisitor() {
		return this.mVisitor;
	}
	public void setVisitor(User visitor) {
		this.mVisitor = visitor;
	}

	public long getPostTime() {
		return this.mPostTime;
	}
	public void setPostTime(long postTime) {
		this.mPostTime = postTime;
	}

	/**
	 * @Title: parse
	 * @Description: 由json数据包解析出对象
	 * @param jsonObject
	 * @return VisitorMessage
	 */
	public static VisitorMessage parse(JSONObject jsonObject) {
		VisitorMessage visitedLog = new VisitorMessage();
		try {
			visitedLog.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("visitedUserId")) {
				visitedLog.setVisiteLogId(jsonObject.getLong("visitedUserId"));
			}
			visitedLog.setContent(jsonObject.getString("content"));
			if (!jsonObject.isNull("imageUrl")) {
				visitedLog.setImageUrl(jsonObject.getString("imageUrl"));
			}
			if (!jsonObject.isNull("createdUser")) {
				visitedLog.setVisitor(User.parse(jsonObject.getJSONObject("createdUser")));
			}
			visitedLog.setPostTime(jsonObject.getLong("createdOn"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return visitedLog;
	}
}
