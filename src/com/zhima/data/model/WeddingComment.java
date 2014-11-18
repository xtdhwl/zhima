/* 
 * @Title: AcqierementComment.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: WeddingComment
 * @Description: 喜印评论
 * @author liubingsr
 * @date 2012-7-25 下午3:00:25
 * 
 */
public class WeddingComment extends BaseData {
	private final static String TAG = "WeddingComment";
	/**
	 * 内容标识
	 */
	private long mId = 0;
	/**
	 * 内容
	 */
	private String mContent = "";
	/**
	 * 图片url
	 */
	private String mImageUrl = "";
	/**
	 * 发表者
	 */
	private User mSender = null;
	/**
	 * 昵称
	 */
	private String mNickname = "";
	/**
	 * 发表时间
	 */
	private long mPostTime = System.currentTimeMillis();

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
	
	public String getNickname() {
		return this.mNickname;
	}
	public void setNickname(String nickname) {
		this.mNickname = nickname;
	}
	
	public String getImageUrl() {
		return this.mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}
	
	public User getSender() {
		return this.mSender;
	}
	public void setSender(User sender) {
		this.mSender = sender;
	}

	public long getPostTime() {
		return mPostTime;
	}
	public void setPostTime(long time) {
		this.mPostTime = time;
	}
	/**
	* @Title: parse
	* @Description: 由json数据解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static WeddingComment parse(JSONObject jsonObject) {
		WeddingComment comment = new WeddingComment();
		try {
			comment.setId(jsonObject.getLong("id"));
			comment.setContent(StringHelper.jsonNullToEmpty(jsonObject.getString("content")));
			if (!jsonObject.isNull("imageUrl")) {
				comment.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}
			if (!jsonObject.isNull("createdByName")) {
				comment.setNickname(StringHelper.jsonNullToEmpty(jsonObject.getString("createdByName")));
			}
			if (!jsonObject.isNull("updatedBy")) {
				User sender = User.parse(jsonObject.getJSONObject("updatedBy"));
				comment.setSender(sender);
			}
			comment.setPostTime(jsonObject.getLong("updatedOn"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return comment;		
	}
}
