/* 
 * @Title: IdolAcqierement.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: IdolAcqierement
 * @Description: 才艺展示内容
 * @author liubingsr
 * @date 2012-7-25 下午3:00:25
 * 
 */
public class IdolAcqierement extends BaseData {
	private final static String TAG = "IdolAcqierement";
	/**
	 * 内容标识
	 */
	private long mId = 0;
	/**
	 * 标题
	 */
	private String mTitle = "";
	/**
	 * 摘要内容
	 */
	private String mSummary = "";
	/**
	 * 详细内容
	 */
	private String mContent = "";
	/**
	 * 图片url
	 */
	private String mImageUrl = "";
	/**
	 * 网页超连接
	 */
	private String mContentUrl = "";
	/**
	 * 作品类型:1作品 ，2 ：频道。 默认为作品
	 */
	private int mOpusType = 0;
	/**
	 * 空间类型
	 */
	private int mTargetType = 0;
	/**
	 * 空间id
	 */
	private long mTargetId = 0;
	/**
	 * 排序
	 */
	private int mSequenceNumber = 0;
	/**
	 * 创建时间
	 */
	private long mCreatedTime = 0;

	@Override
	public long getId() {
		return this.mId;
	}
	public void setId(long id) {
		this.mId = id;
	}

	public String getTitle() {
		return this.mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getSummary() {
		return this.mSummary;
	}
	public void setSummary(String summary) {
		this.mSummary = summary;
	}
	
	public String getContent() {
		return this.mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}
	
	public String getImageUrl() {
		return this.mImageUrl;
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
	
	public int getOpusType() {
		return mOpusType;
	}
	public void setOpusType(int opusType) {
		this.mOpusType = opusType;
	}
	
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

	public int getSequenceNumber() {
		return mSequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.mSequenceNumber = sequenceNumber;
	}

	public long getCreatedTime() {
		return mCreatedTime;
	}
	public void setCreatedTime(long time) {
		this.mCreatedTime = time;
	}
	/**
	* @Title: parse
	* @Description: 由json数据解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static IdolAcqierement parse(JSONObject jsonObject) {
		IdolAcqierement content = new IdolAcqierement();
		try {
			content.setId(jsonObject.getLong("id"));
			content.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("title")));
			content.setSummary(StringHelper.jsonNullToEmpty(jsonObject.getString("summary")));
			if (!jsonObject.isNull("content")) {
				content.setContent(StringHelper.jsonNullToEmpty(jsonObject.getString("content")));
			}
			if (!jsonObject.isNull("contentUrl")) {
				content.setContentUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("contentUrl")));
			}			
			content.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			if (!jsonObject.isNull("opusType")) {
				content.setOpusType(jsonObject.getInt("opusType"));
			}			
			if (!jsonObject.isNull("targetType")) {
				content.setTargetType(jsonObject.getInt("targetType"));
			}
			if (!jsonObject.isNull("targetId")) {
				content.setTargetId(jsonObject.getLong("targetId"));
			} else if (!jsonObject.isNull("spaceId")) {
				content.setTargetId(jsonObject.getLong("spaceId"));
			}
			if (!jsonObject.isNull("sequenceNumber")) {
				content.setSequenceNumber(jsonObject.getInt("sequenceNumber"));
			}			
			if (!jsonObject.isNull("createdOn")) {
				content.setCreatedTime(jsonObject.getLong("createdOn"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return content;		
	}
}
