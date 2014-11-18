/* 
* @Title: ViewHistoryEntry.java
* Created by liubingsr on 2012-7-27 上午10:11:09 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;


/**
* @ClassName: ViewHistoryEntry
* @Description:  浏览记录条目
* @author liubingsr
* @date 2012-8-10 上午11:35:23
*
*/
public class ViewHistoryEntry extends BaseData {
	/**
	 * 条目id
	 */
	private long mId = 0;
	/**
	 * 用户id
	 */
	private long mUserId = 0;
	/**
	 * 目标对象标识
	 */
	private long mObjectId;
	/**
	 * 目标对象类型。日记、未知庶帖子、空间、某个人的Home页
	 */
	private int mObjectType;
	/**
	 * 标题
	 */
	private String mTitle;	
	/**
	 * 上传标志
	 */
	private boolean mUploadFlag;
	/**
	 * 浏览时间
	 */
	private long mViewTime = System.currentTimeMillis();
	
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
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}
	
	public long getViewTime() {
		return this.mViewTime;
	}
	public void setViewTime(long viewTime) {
		this.mViewTime = viewTime;
	}
	
	public boolean getUploadFlag() {
		return mUploadFlag;
	}
	public void setUploadFlag(boolean uploadFlag) {
		this.mUploadFlag = uploadFlag;
	}
}
