/* 
 * @Title: ZMObjectImage.java
 * Created by liubingsr on 2012-5-25 下午12:01:58 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMObjectImage
 * @Description: 知码对象相册信息
 * @author liubingsr
 * @date 2012-6-7 下午2:21:06
 * 
 */
public class ZMObjectImage extends BaseData {
	private final static String TAG = "ZMObjectImage";
	/**
	 * 标识
	 */
	private long mId;
	/**
	 * 所属的zmobjectId
	 */
	private long mZMObjectId;
	/**
	 * 名称
	 */
	private String mName;
	/**
	 * 简要描述
	 */
	private String mDescription;
	/**
	 * 图片url
	 */
	private String mImageUrl;

	public ZMObjectImage() {
		mId = 0;
		mName = "";
		mDescription = "";
		mImageUrl = "";
	}

	@Override
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public long getZMObjectId() {
		return mZMObjectId;
	}
	public void setZMObjectId(long objectId) {
		this.mZMObjectId = objectId;
	}

	/**
	 * @return mName
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name
	 *            要设置的 mName
	 */
	public void setName(String name) {
		this.mName = name;
	}

	/**
	 * @return mDescription
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @param description
	 *            要设置的 mDescription
	 */
	public void setDescription(String description) {
		this.mDescription = description;
	}

	/**
	 * @return mImageUrl
	 */
	public String getImageUrl() {
		return mImageUrl;
	}

	/**
	 * @param imageUrl
	 *            要设置的 mImageUrl
	 */
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id:" + mId);
		sb.append(",name:" + mName);
		sb.append(",description:" + mDescription);
		sb.append(",imageUrl:" + mImageUrl);
		return sb.toString();
	}

	/**
	 * @Title: parse
	 * @Description: 图片信息
	 * @param jsonObject
	 * @return ZMObjectImage
	 */
	public static ZMObjectImage parse(JSONObject jsonObject) {
		ZMObjectImage image = new ZMObjectImage();
		try {
			image.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("spaceId")) {
				image.setZMObjectId(jsonObject.getLong("spaceId"));
			}
			image.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			image.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			image.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return image;
	}
}
