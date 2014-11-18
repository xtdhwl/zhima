/* 
 * @Title: UserAlbumImage.java
 * Created by liubingsr on 2012-5-25 下午12:01:58 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
* @ClassName: UserAlbumImage
* @Description: 用户相册图片
* @author liubingsr
* @date 2012-8-18 下午2:45:42
*
*/
public class UserAlbumImage extends BaseData {
	private final static String TAG = "UserAlbumImage";
	/**
	 * 标识
	 */
	private long mId = 0;
	/**
	 * 用户id
	 */
	private long mUserId = 0;
	/**
	 * 图片标题
	 */
	private String mTitle = "";
	/**
	 * 图片url
	 */
	private String mImageUrl = "";

	public UserAlbumImage() {
		mId = 0;
		mUserId = 0;
		mTitle = "";
		mImageUrl = "";
	}

	@Override
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public long getUserId() {
		return mUserId;
	}
	public void setUserId(long userId) {
		this.mUserId = userId;
	}
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id:" + mId);
		sb.append(",title:" + mTitle);
		sb.append(",imageUrl:" + mImageUrl);
		return sb.toString();
	}

	/**
	 * @Title: parse
	 * @Description: 图片信息
	 * @param jsonObject
	 * @return null 解析错误
	 */
	public static UserAlbumImage parse(JSONObject jsonObject) {
		UserAlbumImage image = new UserAlbumImage();
		try {
			image.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("description")) {
				image.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}			
			image.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return image;
	}
}
