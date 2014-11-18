/* 
 * @Title: AccountService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import android.content.Context;
import android.text.TextUtils;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.AccessTokenType;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.model.LogonUser;
import com.zhima.data.model.User;
/**
 * @ClassName: AccountService
 * @Description: 登录帐号
 * @author liubingsr
 * @date 2012-6-6 下午7:55:36
 * 
 */
public class AccountService extends BaseService {
	private final static String TAG = "AccountService";
	private static AccountService mInstance = null;

	private String mAccount;
	private int mAccountId = ZMConsts.INVALID_ID;

	private AccountService(Context context) {
		super(context);
	}

	public static AccountService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new AccountService(context);
		}
		return mInstance;
	}

	/**
	 * @Title: isMySelf
	 * @Description: 是否是本机登录用户自己
	 * @param c
	 * @param uid
	 * @return boolean
	 */
	public static boolean isMySelf(Context c, long uid) {
		return uid == getInstance(c).getUserId();
	}

	@Override
	public void onCreate() {
//		loadAccountObject();
	}
//	
//	private void loadAccountObject() {
//		mAccountObject = new Account();
//		mAccountObject.setAccessToken(getZMAccessToken());
//		mAccountObject.setAccount(getAccount());
//		mAccountObject.setUserId(getUserId());
//		mAccountObject.setPassword(getPassword());
//		mAccountObject.setCityId(getCityId());
//		mAccountObject.setNickname(getDisplayName());
//		mAccountObject.setLogin(isLogin());
//		mAccountObject.setFirstStartup(isFirstStartup());
//	}
	/**
	* @Title: getAccountObject
	* @Description: 获取登录帐号信息
	* @return Account
	*/
//	public Account getAccountObject() {
//		if (mAccountObject == null) {
//			loadAccountObject();
//		}
//		return mAccountObject;	
//	}
	/**
	* @Title: saveAccountData
	* @Description: 保存登录帐号信息
	* @param profile
	* @param isFirstStartup 是否首次启动
	* @return void
	*/
	public void saveAccountData(LogonUser profile, boolean isFirstStartup) {
		setZMAccessToken(profile.getAccessToken());
		setZMAccessTokenType(profile.getAccessTokenType());
		setAccount(profile.getAccountName());
		
		saveMyAccountData(profile);
		
		setLogin(true);
		// 首次启动标志置位
		setFirstStartup(isFirstStartup);
	}
	// save myself userinfo
	public void saveMyAccountData(User profile) {
		setUserId(profile.getUserId());
		setImageUrl(profile.getImageUrl());
		setPassword(profile.getPassword());
		setCityId(profile.getCityId());
		setDisplayName(profile.getNickname());
		setGender(profile.getGender());
		setZMUserId(profile.getZMUserId());
		setSignature(profile.getSignature());
	}
	private void setSignature(String signature) {
		//TODO
		SettingHelper.setString(mContext, Field.ACCOUNT_SIGNATURE, signature);
	}

	private void setZMUserId(String zmUserId) {
		//TODO
		SettingHelper.setString(mContext, Field.ACCOUNT_ZMUSER_Id, zmUserId);
	}

	private void setGender(String gender) {
		//TODO
		SettingHelper.setString(mContext, Field.ACCOUNT_GENDER, gender);
	}

	// get myself
	public User getMyself() {
		User my = UserService.getInstance(mContext).getMyself();
		if (my == null) {
			my = new User(getUserId());
			my.setNickname(getDisplayName());
			my.setCityId(getCityId());
			my.setImageUrl(getImageUrl());
			my.setZMUserId(getAccount());
			my.setGender(getGender());
			my.setZMUserId(getZmUserId());
			my.setSignature(getSignature());
		}
		return my;
	}
	private String getGender() {
		//TODO
		return SettingHelper.getString(mContext, Field.ACCOUNT_GENDER, "");
		
	}

	private String getZmUserId() {
		//TODO
		return SettingHelper.getString(mContext, Field.ACCOUNT_ZMUSER_Id, "");
		
	}

	private String getSignature() {
		//TODO
		return SettingHelper.getString(mContext, Field.ACCOUNT_SIGNATURE, "");
		
	}

	/**
	* @Title: isGuest
	* @Description: 是否是游客(自动注册用户)
	* @return boolean
	*/
	public boolean isGuest() {
		return getZMAccessTokenType() == AccessTokenType.AUTO;
	}
	/**
	 * @Title: isLogin
	 * @Description: 用户是否已经登录。自动注册用户为“未登录状态”
	 * @return true:已经登录
	 */
	public boolean isLogin() {
		if (isGuest()) {
			return false;
		}
		boolean ret = SettingHelper.getBoolean(mContext, SettingHelper.Field.LOGIN_SUCCESS, false);
		return ret && !TextUtils.isEmpty(getZMAccessToken());
	}
	public void setLogin(boolean successed) {
		SettingHelper.setBoolean(mContext, Field.LOGIN_SUCCESS, successed);
	}
	
	public boolean isFirstStartup() {
		return SettingHelper.getBoolean(mContext, Field.APP_FIRSTSTARTUP, true);
	}
	public void setFirstStartup(boolean isFirst) {
		SettingHelper.setBoolean(mContext, Field.APP_FIRSTSTARTUP, isFirst);
	}

	public String getAccount() {
		return SettingHelper.getString(mContext, Field.ACCOUNT_NAME, "");
	}
	public void setAccount(String account) {
		SettingHelper.setString(mContext, Field.ACCOUNT_NAME, account);
	}
	
	public String getDisplayName() {
		return SettingHelper.getString(mContext, Field.ACCOUNT_DISPLAYNAME, "");
	}
	public void setDisplayName(String nickname) {
		SettingHelper.setString(mContext, Field.ACCOUNT_DISPLAYNAME, nickname);
	}
	
	public String getPassword() {
		return SettingHelper.getString(mContext, Field.ACCOUNT_PASSWORD, "");
	}
	public void setPassword(String password) {
		SettingHelper.setString(mContext, Field.ACCOUNT_PASSWORD, password);
	}
	
	public long getCityId() {
		return SettingHelper.getLong(mContext, Field.ACCOUNT_CITYID, 0);
	}
	public void setCityId(long cityId) {
		SettingHelper.setLong(mContext, Field.ACCOUNT_CITYID, cityId);
	}

	public long getUserId() {
		return SettingHelper.getLong(mContext, Field.ACCOUNT_USERID, 0);
	}
	public void setUserId(long userId) {
		SettingHelper.setLong(mContext, Field.ACCOUNT_USERID, userId);
	}
	
	public String getImageUrl() {
		return SettingHelper.getString(mContext, Field.ACCOUNT_IMAGE_URL, "");
	}
	public void setImageUrl(String imageUrl) {
		SettingHelper.setString(mContext, Field.ACCOUNT_IMAGE_URL, imageUrl);
	}
	
	public String getZMAccessToken() {
		return SettingHelper.getString(mContext, Field.ACCESS_TOKEN, "");
	}
	/**
	* @Title: setZMAccessToken
	* @Description: 保存登录后获取到的accessToken
	* @param accessToken 服务器分配的token
	* @return void
	*/
	public void setZMAccessToken(String accessToken) {
		SettingHelper.setString(mContext, Field.ACCESS_TOKEN, accessToken);
	}
	
	public int getZMAccessTokenType() {
		return SettingHelper.getInt(mContext, Field.ACCESS_TOKEN_TYPE, AccessTokenType.AUTO);
	}
	public void setZMAccessTokenType(int tokenType) {
		SettingHelper.setInt(mContext, Field.ACCESS_TOKEN_TYPE, tokenType);
	}
	
	public void saveSetting(String account) {
		setAccount(account);
	}
	
	public void clearAccessToken() {
		SettingHelper.setInt(mContext, Field.ACCESS_TOKEN_TYPE, AccessTokenType.AUTO);
		SettingHelper.setString(mContext, Field.ACCESS_TOKEN, "");
	}
	
	public void clearSetting() {
		setUserId(0);
		setAccount("");
		setZMAccessToken("");
		setZMAccessTokenType(AccessTokenType.AUTO);
		SettingHelper.setBoolean(mContext, SettingHelper.Field.LOGIN_SUCCESS, false);
	}	

	@Override
	public void onDestroy() {
		clear();
		System.gc();
	}

	@Override
	public void clear() {
		
	}
}
