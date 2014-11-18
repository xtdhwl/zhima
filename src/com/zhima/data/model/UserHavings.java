/* 
* @Title: UserHavings.java
* Created by liubingsr on 2013-1-18 下午12:12:31 
* Copyright (c) 2013 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: UserHavings
 * @Description: 用户统计数量
 * @author liubingsr
 * @date 2013-1-18 下午12:12:31
 *
 */
public class UserHavings extends BaseData {
	private final static String TAG = "UserHavings";
	/**
	 * id
	 */
	private long mId = 0;
	/**
	 * 开通的空间数量
	 */
	private int mSpaceCount = 0;
	/**
	 * 相册中图片数量
	 */
	private int mAlbumCount = 0;
	/**
	 * 格子铺物品数量
	 */
	private int mLatticeProductCount = 0;
	/**
	 * 好友数量
	 */
	private int mFriendCount = 0;
	/**
	 * 日志数量
	 */
	private int mDiaryCount = 0;
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		mId = id;
	}
	public int getSpaceCount() {
		return mSpaceCount;
	}
	public void setSpaceCount(int count) {
		this.mSpaceCount = count;
	}
	public int getAlbumCount() {
		return mAlbumCount;
	}
	public void setAlbumCount(int count) {
		this.mAlbumCount = count;
	}
	public int getLatticeProductCount() {
		return mLatticeProductCount;
	}
	public void setLatticeProductCount(int count) {
		this.mLatticeProductCount = count;
	}
	public int getFriendCount() {
		return mFriendCount;
	}
	public void setFriendCount(int count) {
		this.mFriendCount = count;
	}
	public int getDiaryCount() {
		return mDiaryCount;
	}
	public void setDiaryCount(int count) {
		this.mDiaryCount = count;
	}
	
	public static UserHavings parse(JSONObject jsonObject) {
		UserHavings product = new UserHavings();
		try {
			if (!jsonObject.isNull("zhimaQ")) {
				product.setSpaceCount(jsonObject.getInt("zhimaQ"));
			}
			if (!jsonObject.isNull("albumQ")) {
				product.setAlbumCount(jsonObject.getInt("albumQ"));
			}
			if (!jsonObject.isNull("productQ")) {
				product.setLatticeProductCount(jsonObject.getInt("productQ"));
			}
			if (!jsonObject.isNull("friendQ")) {
				product.setFriendCount(jsonObject.getInt("friendQ"));
			}
			if (!jsonObject.isNull("blogQ")) {
				product.setDiaryCount(jsonObject.getInt("blogQ"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return product;
	}
}
