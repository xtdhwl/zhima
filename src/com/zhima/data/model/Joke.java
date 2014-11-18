/* 
 * @Title: Joke.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: Joke
 * @Description: 知趣信息
 * @author liubingsr
 * @date 2012-6-7 下午6:16:12
 * 
 */
public final class Joke extends BaseData {
	private final static String TAG = "Joke";
	/**
	 * id
	 */
	private long mId = 0;
	/**
	 * 标题
	 */
	private String mTitle = "";
	/**
	 *  内容
	 */
	private String mContent = "";
	/**
	 * 图片URL
	 */
	private String mImageUrl = "";
	/**
	 * 创建时间
	 */
	private long mCreatedTime = System.currentTimeMillis();

	public Joke() {
		mId = 0;
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

	public long getCreatedTime() {
		return mCreatedTime;
	}
	public void setCreatedTime(long time) {
		this.mCreatedTime = time;
	}
	/**
	* @Title: parse
	* @Description: json数据包解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static Joke parse(JSONObject jsonObject) {
		Joke joke = new Joke();
		try {
			joke.setId(jsonObject.getLong("id"));
			joke.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("title")));
			joke.setContent(StringHelper.jsonNullToEmpty(jsonObject.getString("content")));
			joke.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			if(!jsonObject.isNull("createdOn")){
				joke.setCreatedTime(jsonObject.getLong("createdOn"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return joke;
	}
}
