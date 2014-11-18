/* 
 * @Title: Notice.java
 * Created by liubingsr on 2012-7-9 上午10:53:36 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: Notice
 * @Description: 公告
 * @author liubingsr
 * @date 2012-7-9 上午10:53:36
 * 
 */
public class Notice extends BaseData {
	private final static String TAG = "Notice";
	/**
	 * 公告id
	 */
	private long mNoticeId;
//	/**
//	 * 所属的zmCode
//	 */
//	private String mZMCode;
	/**
	 * 城市id
	 */
	private long mCityId;
	/**
	 * 公告标题
	 */
	private String mTitle = "";
	/**
	 * 公告内容
	 */
	private String mContent = "";
	/**
	 * 公告图片
	 */
	private String mImageUrl = "";
	/**
	 * 公告类别
	 */
	private int mNoticeKind;
	/**
	 * 公告发布时间
	 */
	private long mPostTime = System.currentTimeMillis();

	@Override
	public long getId() {
		return mNoticeId;
	}

	public long getNoticeId() {
		return mNoticeId;
	}
	public void setNoticeId(long noticeId) {
		this.mNoticeId = noticeId;
	}
//
//	public String getZMCode() {
//		return mZMCode;
//	}
//	public void setZMCode(String zmCode) {
//		this.mZMCode = zmCode;
//	}
	
	public long getCityId() {
		return mCityId;
	}
	public void setCityId(long cityId) {
		this.mCityId = cityId;
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
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
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	public int getNoticeKind() {
		return mNoticeKind;
	}
	public void setNoticeKind(int noticeKind) {
		this.mNoticeKind = noticeKind;
	}

	public long getPostTime() {
		return mPostTime;
	}
	public void setPostTime(long postTime) {
		this.mPostTime = postTime;
	}

	/**
	 * @Title: parse
	 * @Description: 由json数据包解析出公告信息
	 * @param jsonObject
	 * @return Notice
	 */
	public static Notice parse(JSONObject jsonObject) {
		Notice notice = new Notice();
		try {
			if (!jsonObject.isNull("id")) {
				notice.setNoticeId(jsonObject.getLong("id"));
			}
			if (!jsonObject.isNull("cityId")) {
				notice.setCityId(jsonObject.getLong("cityId"));
			}
			notice.setTitle(jsonObject.getString("title"));
			if (!jsonObject.isNull("content")) {
				notice.setContent(StringHelper.jsonNullToEmpty(jsonObject.getString("content")));
			}
			if (!jsonObject.isNull("imageUrl")) {
				notice.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}
			if (!jsonObject.isNull("createdOn")) {
				notice.setPostTime(jsonObject.getLong("createdOn"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return notice;
	}
}
