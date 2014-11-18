/* 
 * @Title: User.java
 * Created by liubingsr on 2012-5-18 下午5:02:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.AccessTokenType;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: LogonUser
 * @Description: 登录用户信息
 * @author liubingsr
 * @date 2012-5-18 下午5:02:53
 * 
 */
public class LogonUser extends User {
	private final static String TAG = "LogonUser";
	/**
	 * 服务器分配给登录用户的token
	 */
	private String mAccessToken;
	/**
	 * Token类型 1 注册用户 2 自动注册用户 3 外部三方用户
	 */
	private int mAccessTokenType = AccessTokenType.AUTO;
	/**
	 * 登录帐号名
	 */
	private String mAccountName;
	/**
	 * 是否绑定过本地帐号
	 */
	protected boolean mIsBoundUser = false;
	/**
	 * 上次登录时间
	 */
	private long mLoginTime = System.currentTimeMillis();
	/**
	 * 注册日期
	 */
	private long mCreateTime = System.currentTimeMillis();

	public LogonUser() {
	}

	public LogonUser(long userId) {
		mUserId = userId;
	}

	public LogonUser(LogonUser profile) {
		super(profile);
		if (profile != null) {
			setZMUserId(profile.getZMUserId());
			setAccessToken(profile.getAccessToken());			
//			setUserId(profile.getUserId());			
//			setNickname(profile.getNickname());
//			setPassword(profile.getPassword());
//			setGender(profile.getGender());
//			setImageUrl(profile.getImageUrl());
//			setCityId(profile.getCityId());
//			setBoardId(profile.getBoardId());
//			setGeo(profile.getGeo());
		}
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(String accessToken) {
		this.mAccessToken = accessToken;
	}

	public int getAccessTokenType() {
		return mAccessTokenType;
	}

	public void setAccessTokenType(int accessTokenType) {
		this.mAccessTokenType = accessTokenType;
	}
	/**
	* @Title: isGuest
	* @Description: 是否是游客身份登录
	* @return
	* boolean
	*/
	public boolean isGuest() {
		return mAccessTokenType == AccessTokenType.AUTO;
	}
	
	public String getAccountName() {
		return mAccountName;
	}

	public void setAccountName(String accountName) {
		this.mAccountName = accountName;
	}

	public boolean isBoundUser() {
		return mIsBoundUser;
	}

	public void setIsBoundUser(boolean isBoundUser) {
		this.mIsBoundUser = isBoundUser;
	}

	public long getLoginTime() {
		return this.mLoginTime;
	}

	public void setLoginTime(long loginTime) {
		this.mLoginTime = loginTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:" + mUserId);
		sb.append(",zmIdentity:" + mZMUserId);
		sb.append(",nickname:" + mNickname);
		sb.append(",gender:" + mGender);
		sb.append(",birthday:" + mBirthday);
		sb.append(",bloodType:" + mBloodType);
		sb.append(",astro:" + mAstro);
		sb.append(",age:" + mAge);
		sb.append(",signature:" + mSignature);
		sb.append(",imageUrl:" + mImageUrl);
		sb.append(",cityId:" + mCityId);
		sb.append(",createTime:" + mCreateTime);
		sb.append(",loginTime:" + mLoginTime);
		return sb.toString();
	}

	@Override
	public void create(JSONObject jsonObject) throws JSONException {
		if (!jsonObject.isNull("userInfo")) {
			super.create(jsonObject.getJSONObject("userInfo"));
		}
		if (!jsonObject.isNull("accessToken")) {
			mAccessToken = jsonObject.getString("accessToken");
		}
		if (!jsonObject.isNull("accessTokenType")) {
			mAccessTokenType = jsonObject.getInt("accessTokenType");
		}
//		if (!jsonObject.isNull("username")) {
//			mZMUserId = jsonObject.getString("username");
//		} else if (!jsonObject.isNull("zmUserid")) {
//			mZMUserId = jsonObject.getString("zmUserid");
//		}
		mAccountName = mZMUserId;
		if (!jsonObject.isNull("isBoundUser")) {
			mIsBoundUser = jsonObject.getBoolean("isBoundUser");
		}				
	}

	/**
	 * @Title: parse
	 * @Description: 由json解析出对象
	 * @param jsonObject
	 * @return null 解析失败
	 */
	public static LogonUser parse(JSONObject jsonObject) {
		LogonUser user = new LogonUser();
		try {
			user.create(jsonObject);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			user = null;
		}
		return user;
	}
}
