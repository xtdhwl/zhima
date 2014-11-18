/* 
 * @Title: InboxModel.java
 * Created by liubingsr on 2012-5-31 上午10:15:56 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

/**
 * @ClassName: InboxModel
 * @Description: 收件箱
 * @author liubingsr
 * @date 2012-6-7 下午6:29:43
 * 
 */
public class InboxModel extends BaseData {
	// 消息id
	private long mMessageId;
	//
	private long mObjectId;
	// 发件人名称
	private String mSenderName;
	// appkey
	private String mAppKey;
	// appsecret
	private String mAppSecret;

	/**
	 * @return mSenderName
	 */
	public String getSenderName() {
		return mSenderName;
	}

	/**
	 * @param senderName
	 *            要设置的 mSenderName
	 */
	public void setSenderName(String senderName) {
		this.mSenderName = senderName;
	}

	/**
	 * @return mAppKey
	 */
	public String getAppKey() {
		return mAppKey;
	}

	/**
	 * @param appKey
	 *            要设置的 mAppKey
	 */
	public void setAppKey(String appKey) {
		this.mAppKey = appKey;
	}

	/**
	 * @return mAppSecret
	 */
	public String getAppSecret() {
		return mAppSecret;
	}

	/**
	 * @param appSecret
	 *            要设置的 mAppSecret
	 */
	public void setAppSecret(String appSecret) {
		this.mAppSecret = appSecret;
	}

	/*
	 * (非 Javadoc) <p>Title: getId</p> <p>Description: </p>
	 * 
	 * @return
	 * 
	 * @see com.zhima.data.model.BaseModel#getId()
	 */
	@Override
	public long getId() {
		return mMessageId;
	}

	public void setId(long id) {
		this.mMessageId = id;
	}
}
