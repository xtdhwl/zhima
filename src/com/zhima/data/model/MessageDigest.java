/* 
* @Title: MessageEntry.java
* Created by liubingsr on 2012-7-27 下午6:00:00 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: MessageEntry
 * @Description: 消息
 * @author liubingsr
 * @date 2012-7-27 下午6:00:00
 *
 */
public class MessageDigest extends BaseData {
	private final static String TAG = "MessageEntry";
	/**
	 * 消息主键id
	 */
	protected long mId = 0;
	/**
	 * 消息主键id
	 */
	protected long mMessageId = 0;
	/**
	 * 消息类型
	 * @see com.zhima.base.consts.ZMConsts.MessageType
	 */
	protected String mMessageType;
	/**
	 * 发送者
	 * 对于“公告”和“系统通知”该字段为null，对于“好友申请”该字段为申请人，对于“好友对话”，该字段为对话双方除了当前用户的另外一人，哪怕最新一条是当前用户发送的
	 */
	protected User mSender = null;
	/**
	 * 消息内容
	 */
	protected String mContent = "";
	/**
	 * 消息状态：1 (未读) | 2 (已读)| 3(消息已处理)
	 * @see com.zhima.base.consts.ZMConsts.MessageStatus
	 */
	protected int mMessageStatus;
	/**
	 * 时间
	 */
	protected long mTimestamp = System.currentTimeMillis();
	/**
	 * 未读消息数量
	 */
	protected int mUnReadCount = 0;
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}
	
	public long getMessageId() {
		return mMessageId;
	}
	public void setMessageId(long id) {
		this.mMessageId = id;
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
	public void setMessageType(String type) {
		this.mMessageType = type;
	}
	
	public String getContent() {
		return mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}
	
	public int getMessageStatus() {
		return mMessageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.mMessageStatus = messageStatus;
	}
	
	public int getUnReadCount() {
		return mUnReadCount;
	}
	public void setUnReadCount(int count) {
		this.mUnReadCount = count;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}
	/**
	* @Title: parse
	* @Description: 解析出数据
	* @param jsonObject
	* @return MessageDigest
	*/
	public static MessageDigest parse(JSONObject jsonObject) {
		MessageDigest entry = new MessageDigest();
		try {
			entry.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("messageId")) {
				entry.setMessageId(jsonObject.getLong("messageId"));
			}
			if (!jsonObject.isNull("messageType")) {
				entry.setMessageType(jsonObject.getString("messageType"));
			}
			if (!jsonObject.isNull("sender")) {
				User sender = User.parse(jsonObject.getJSONObject("sender"));
				entry.setSender(sender);
			}
			if (!jsonObject.isNull("messageStatus")) {
				entry.setMessageStatus(jsonObject.getInt("messageStatus"));
			}
			entry.setContent(jsonObject.getString("content"));
			if (!jsonObject.isNull("unreadCount")) {
				entry.setUnReadCount(jsonObject.getInt("unreadCount"));
			}
			if (!jsonObject.isNull("createdOn")) {
				entry.setTimestamp(jsonObject.getLong("createdOn"));
			}
			
		} catch (JSONException e) {
			Logger.getInstance(TAG ).debug(e.getMessage(), e);
			return null;
		}
		return entry;
	}
}
