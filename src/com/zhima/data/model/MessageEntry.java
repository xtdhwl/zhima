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
public class MessageEntry extends BaseData {
	private final static String TAG = "MessageEntry";
	/**
	 * 消息id
	 */
	private long mId;
	/**
	 * 消息类型
	 * @see com.zhima.base.consts.ZMConsts.MessageType
	 */
	private String mMessageType;
	/**
	 * 发送者
	 * 对于“公告”和“系统通知”该字段为null，对于“好友申请”该字段为申请人，对于“好友对话”，该字段为对话双方除了当前用户的另外一人，哪怕最新一条是当前用户发送的
	 */
	private User mSender = null;
//	/**
//	 * 接收者
//	 */
//	private User mReceiver;
	/**
	 * 消息内容
	 */
	private String mContent = "";	
	/**
	 * 消息状态：1 (未读) | 2 (已读)| 3(消息已处理)
	 * @see com.zhima.base.consts.ZMConsts.MessageStatus
	 */
	private int mMessageStatus;
	/**
	 * 目标类型
	 */
	private int mTargetType;
	/**
	 * 目标Id
	 */
	private long mTargetId;
	/**
	 * 时间
	 */
	private long mTimestamp = System.currentTimeMillis();	
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
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
//
//	public User getReceiver() {
//		return mReceiver;
//	}
//	public void setReceiver(User receiver) {
//		this.mReceiver = receiver;
//	}

	public int getTargetType() {
		return mTargetType;
	}
	public void setTargetType(int targetType) {
		this.mTargetType = targetType;
	}

	public long getTargetId() {
		return mTargetId;
	}
	public void setTargetId(long targetId) {
		this.mTargetId = targetId;
	}
	
	public int getMessageStatus() {
		return mMessageStatus;
	}
	public void setMessageStatus(int messageStatus) {
		this.mMessageStatus = messageStatus;
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
	* @return
	* MessageEntry
	*/
	public static MessageEntry parse(JSONObject jsonObject) {
		MessageEntry entry = new MessageEntry();
		try {
			entry.setId(jsonObject.getLong("id"));
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
			if (!jsonObject.isNull("targetType")) {
				entry.setTargetType(jsonObject.getInt("targetType"));
			}
			if (!jsonObject.isNull("targetId")) {
				entry.setTargetId(jsonObject.getLong("targetId"));
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
