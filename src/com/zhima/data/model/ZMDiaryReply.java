/* 
 * @Title: ZMDiaryReply.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: ZMDiaryReply
 * @Description: 日志回复
 * @author liubingsr
 * @date 2012-5-17 下午7:24:52
 * 
 */
public class ZMDiaryReply extends BaseData {
	private final static String TAG = "ZMDiaryReply";
	/**
	 * 内容标识
	 */
	private long mId = 0;
	/**
	 * 是否评论日志。true：评论日志，false：回复某个人
	 */
	private boolean mIsComment = true;
	/**
	 * 被回复人userid
	 */
	private long mRepliedUserId = 0;
	/**
	 * 被回复人昵称
	 */
	private String mRepliedUserNickname = "";
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
	
	public boolean isComment() {
		return mIsComment;
	}
	public void setIsComment(boolean isComment) {
		this.mIsComment = isComment;
	}
	public long getRepliedUserId() {
		return this.mRepliedUserId;
	}
	public void setRepliedUserId(long userId) {
		this.mRepliedUserId = userId;
	}
	
	public String getRepliedUserNickname() {
		return this.mRepliedUserNickname;
	}
	public void setRepliedUserNickname(String nickname) {
		this.mRepliedUserNickname = nickname;
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

	public long getPostTime() {
		return this.mPostTime;
	}
	public void setPostTime(long postTime) {
		this.mPostTime = postTime;
	}
	
	public static ZMDiaryReply parse(JSONObject jsonObject) {
		ZMDiaryReply diary = new ZMDiaryReply();
		try {
			diary.setId(jsonObject.getLong("id"));
			diary.setIsComment(jsonObject.getBoolean("replyOrginal"));
			if (!jsonObject.isNull("repliedId")) {
				diary.setRepliedUserId(jsonObject.getLong("repliedId"));
			}
			if (!jsonObject.isNull("repliedName")) {
				diary.setRepliedUserNickname(jsonObject.getString("repliedName"));
			}			
			if (!jsonObject.isNull("content")) {
				diary.setContent(jsonObject.getString("content"));
			}
			if (!jsonObject.isNull("createdUserInfo")) {
				User author = User.parse(jsonObject.getJSONObject("createdUserInfo"));
				diary.setAuthor(author);
			}
			if (!jsonObject.isNull("createdOn")) {
				diary.setPostTime(jsonObject.getLong("createdOn"));
			}
			
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return diary;
	}
}
