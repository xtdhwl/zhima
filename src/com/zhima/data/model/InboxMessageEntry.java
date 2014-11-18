/* 
 * @Title: InboxMessageEntry.java
 * Created by liubingsr on 2012-7-27 上午10:11:09 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: InboxMessageEntry
 * @Description: 站内信详情通用
 * @author liubingsr
 * @date 2012-9-3 下午5:19:51
 * 
 */
public class InboxMessageEntry extends BaseData {
	private final static String TAG = "InboxMessageEntry";
	/**
	 * 消息id
	 */
	private long mId;
	/**
	 * 发送者。对于“公告”和“通知”该字段为null，对于“好友申请”该字段为申请人
	 */
	private User mSender = null;
	/**
	 * 消息类型
	 * 
	 * @see com.zhima.base.consts.ZMConsts.MessageType
	 */
	private String mMessageType;
	/**
	 * 内容
	 */
	private String mContent;
	/**
	 * 服务器返回的图片原始url
	 */
	private String mImageUrl;
	/**
	 * 消息状态
	 * @see com.zhima.base.consts.ZMConsts.MessageStatus
	 */
	private int mMessageStatus;
	/**
	 * 未读消息条数
	 */
	private int mUnreadCount;
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

	public User getSender() {
		return mSender;
	}
	public void setSender(User sender) {
		this.mSender = sender;
	}
	
	public String getMessageType() {
		return mMessageType;
	}
	public void setMessageType(String messageType) {
		this.mMessageType = messageType;
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
	
	public int getMessageStatus() {
		return mMessageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.mMessageStatus = messageStatus;
	}
	
	public int getUnreadCount() {
		return mUnreadCount;
	}
	public void setUnreadCount(int unreadCount) {
		this.mUnreadCount = unreadCount;
	}
	public long getCreateTime() {
		return mCreateTime;
	}
	public void setCreateTime(long createTime) {
		this.mCreateTime = createTime;
	}

	public static InboxMessageEntry parse(JSONObject jsonObject) {
		InboxMessageEntry record = new InboxMessageEntry();
		try {
			record.setId(jsonObject.getLong("id"));
			record.setMessageType(jsonObject.getString("messageType"));
			if (!jsonObject.isNull("sender")) {
				User sender = User.parse(jsonObject.getJSONObject("sender"));
				record.setSender(sender);
			}
			record.setContent(jsonObject.getString("content"));
			record.setMessageStatus(jsonObject.getInt("messageStatus"));
			record.setUnreadCount(jsonObject.getInt("unreadCount"));
			record.setCreateTime(jsonObject.getLong("createdOn"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return record;
	}
}
