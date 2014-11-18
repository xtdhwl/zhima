/* 
 * @Title: ZMContentBase.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.io.Serializable;

/**
 * @ClassName: ZMContentBase
 * @Description: 内容模型基类
 * @author liubingsr
 * @date 2012-5-17 下午7:24:52
 * 
 */
public abstract class ZMContentBase extends BaseData implements Serializable{
	/**
	* @Fields serialVersionUID : TODO(描述这个变量表示什么)
	*/
	private static final long serialVersionUID = 5646618437801703249L;
	/**
	 * 内容标识
	 */
	protected long mId = 0;
	/**
	 * 内容的作者
	 */
	protected User mAuthor = null;
	/**
	 * 内容(内容里可以带表情符)
	 * 界面显示时需要把表情符替换成表情图片
	 */
	protected String mContent = "";
	/**
	 * 图片url
	 */
	protected String mImageUrl = "";
	/**
	 * 内容状态
	 * @see com.zhima.base.consts.ZMConsts.SendStatus
	 */
	protected int mStatus = 0;
	/**
	 * 内容发表时间
	 */
	protected long mPostTime = 0;	

	@Override
	public long getId() {
		return this.mId;
	}
	public void setId(long id) {
		this.mId = id;
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
