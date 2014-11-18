/* 
 * @Title: ZMObjectVisitedEntry.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: ZMObjectVisitedEntry
 * @Description: 看看谁来过访问记录
 * @author liubingsr
 * @date 2012-6-8 下午5:20:40
 * 
 */
public final class ZMObjectVisitedEntry extends BaseData {
	private final static String TAG = "ZMObjectVisitedEntry";	
	/**
	 * 记录id
	 */
	private long mId = 0;
	/**
	 * ZMObjectId
	 */
	private long mZMObjectId = 0;
	/**
	 * 访客
	 */
	private User mVisitor = null;
	/**
	 * 留言条数
	 */
	private int mMessageCount = 0;
	/**
	 * 访问时间
	 */
	private long mVisitedTime = 0;

	public ZMObjectVisitedEntry() {
		mId = 0;
	}

	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}

	public long getZMObjectId() {
		return this.mZMObjectId;
	}
	public void setZMObjectId(long objectId) {
		this.mZMObjectId = objectId;
	}
	
	public User getVisitor() {
		return this.mVisitor;
	}
	public void setVisitor(User visitor) {
		this.mVisitor = visitor;
	}
	
	public int getMessageCount() {
		return mMessageCount;
	}
	public void setMessageCount(int count) {
		this.mMessageCount = count;
	}
	
	public long getVisitedTime() {
		return this.mVisitedTime;
	}
	public void setVisitedTime(long time) {
		this.mVisitedTime = time;
	}

	/**
	* @Title: parse
	* @Description: 由json数据包解析出对象
	* @param jsonObject
	* @return
	* ZMObjectVisitedLog
	*/
	public static ZMObjectVisitedEntry parse(JSONObject jsonObject) {
		ZMObjectVisitedEntry visitedLog = new ZMObjectVisitedEntry();
		try {
			visitedLog.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("user")) {
				User visitor = User.parse(jsonObject.getJSONObject("user"));
				visitedLog.setVisitor(visitor);
			}
			visitedLog.setMessageCount(jsonObject.getInt("messageCount"));
			if(!jsonObject.isNull("createdOn")) {
				visitedLog.setVisitedTime(jsonObject.getLong("createdOn"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return visitedLog;
	}
}
