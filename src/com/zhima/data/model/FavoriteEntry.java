/* 
* @Title: FavoriteEntry.java
* Created by liubingsr on 2012-7-27 上午10:11:09 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

/**
 * @ClassName: FavoriteEntry
 * @Description: 收藏夹条目(收藏目标是：3000件商品、非3000件商品)
 * @author liubingsr
 * @date 2012-7-27 上午10:11:09
 *
 */
public class FavoriteEntry extends BaseData {
	/**
	 * 条目id
	 */
	private long mId;
	/**
	 * 用户id
	 */
	private long mUserId = 0;	
	/**
	 * 收藏对象标识
	 */
	private long mObjectId = 0;
	/**
	 * 收藏对象类型。非自有码(3000件商品、非3000件商品)
	 * @see com.zhima.base.consts.ZMConsts.TargetType.TARGET_TYPE_UNKNOWN_SPACE
	 */
	private int mObjectType;
	/**
	 * 标题(解析出来的码内容)
	 */
	private String mTitle = "";
	/**
	 * 码值
	 */
	private String mContent = "";
//	/**
//	 * 上传标志
//	 */
//	private boolean mUploadFlag;
	/**
	 * 服务器返回的图片原始url
	 */
	private String mImageUrl = "";
	/**
	 * 收藏时间
	 */
	private long mFavoriteTime = System.currentTimeMillis();
	
	@Override
	public long getId() {
		return mId;
	}
	public long setId(long id) {
		return mId = id;
	}
	
	public long getUserId() {
		return this.mUserId;
	}
	public void setUserId(long userId) {
		this.mUserId = userId;
	}
	
	public int getObjectType() {
		return mObjectType;
	}
	public void setObjectType(int objectType) {
		this.mObjectType = objectType;
	}
	
	public long getObjectId() {
		return mObjectId;
	}
	public void setObjectId(long objectId) {
		this.mObjectId = objectId;
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
	public void setContent(String zmCode) {
		this.mContent = zmCode;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String uploadFlag) {
		this.mImageUrl = uploadFlag;
	}
	
	public long getFavoriteTime() {
		return mFavoriteTime;
	}
	public void setFavoriteTime(long favoriteTime) {
		this.mFavoriteTime = favoriteTime;
	}
}
