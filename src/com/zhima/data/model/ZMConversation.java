/* 
 * @Title: ZMConversation.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import com.zhima.base.consts.ZMConsts.SendStatus;

/**
 * @ClassName: ZMConversation
 * @Description: 会话
 * @author liubingsr
 * @date 2012-5-17 下午7:24:52
 * 
 */
public class ZMConversation extends BaseData {
	/**
	 * 内容标识
	 */
	private long mId = 0;
	/**
	 * 所属的thread id
	 */
	private long mThreadId = 0;
	/**
	 * 内容的作者
	 */
	private User mAuthor = null;
	/**
	 * 内容(内容里可以带表情符)
	 * 界面显示时需要把表情符替换成表情图片
	 */
	private String mContent = "";
	/**
	 * 图片url
	 */
	private String mImageUrl = "";
	/**
	 * 发送状态
	 * @see com.zhima.base.consts.ZMConsts.SendStatus
	 */
	private int mStatus = 0;
	/**
	 * 内容发表时间
	 */
	private long mPostTime = 0;	

	@Override
	public long getId() {
		return this.mId;
	}
	public void setId(long id) {
		this.mId = id;
	}
	
	public long getThreadId() {
		return this.mThreadId;
	}
	public void setThreadId(long id) {
		this.mThreadId = id;
	}
	
	public String getContent() {
		return this.mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}
	
	public User getAuthor() {
		return this.mAuthor;
	}
	public void setAuthor(User author) {
		this.mAuthor = author;
	}

	public String getImageUrl() {
		return this.mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}
	
	public boolean isSent() {
		return mStatus == SendStatus.SENT;
	}
	public int getStatus() {
		return this.mStatus;
	}
	public void setStatus(int status) {
		this.mStatus = status;
	}

	public long getPostTime() {
		return this.mPostTime;
	}
	public void setPostTime(long postTime) {
		this.mPostTime = postTime;
	}
}
