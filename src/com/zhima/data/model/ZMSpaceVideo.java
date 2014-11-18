/* 
 * @Title: ZMSpaceVideo.java
 * Created by liubingsr on 2012-5-25 下午12:01:58 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMSpaceVideo
 * @Description: 芝麻空间影像视频
 * @author liubingsr
 * @date 2012-6-7 下午2:21:06
 * 
 */
public class ZMSpaceVideo extends BaseData {
	private final static String TAG = "ZMSpaceVideo";
	/**
	 * 标识
	 */
	private long mId;
	/**
	 * 标题
	 */
	private String mTitle;
	/**
	 * 描述
	 */
	private String mSummary;
	/**
	 * 图片url
	 */
	private String mImageUrl;
	/**
	 * 网页超连接
	 */
	private String mContentUrl;
	/**
	 * 视频视频路径
	 */
	private String mVideoUrl;

	public ZMSpaceVideo() {
		mId = 0;
		mTitle = "";
		mSummary = "";
		mImageUrl = "";
		mContentUrl = "";
		mVideoUrl = "";
	}

	@Override
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getDescription() {
		return mSummary;
	}
	public void setDescription(String description) {
		this.mSummary = description;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}
	
	public String getContentUrl() {
		return mContentUrl;
	}
	public void setContentUrl(String contentUrl) {
		this.mContentUrl = contentUrl;
	}
	
	public String getVideoUrl() {
		return mVideoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.mVideoUrl = videoUrl;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id:" + mId);
		sb.append(",name:" + mTitle);
		sb.append(",description:" + mSummary);
		sb.append(",imageUrl:" + mImageUrl);
		sb.append(",contentUrl:" + mContentUrl);
		sb.append(",videoUrl:" + mVideoUrl);
		return sb.toString();
	}

	/**
	 * @Title: parse
	 * @Description: 图片信息
	 * @param jsonObject
	 * @return ZMObjectImage
	 */
	public static ZMSpaceVideo parse(JSONObject jsonObject) {
		ZMSpaceVideo image = new ZMSpaceVideo();
		try {
			image.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("title")) {
				image.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("title")));
			}
			if (!jsonObject.isNull("summary")) {
				image.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("summary")));
			}
			if (!jsonObject.isNull("imageUrl")) {
				image.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}
			if (!jsonObject.isNull("contentUrl")) {
				image.setContentUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("contentUrl")));
			}
			
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return image;
	}
}
