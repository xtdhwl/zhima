/* 
 * @Title: ServerConfigInfo.java
 * Created by liubingsr on 2012-9-21 上午10:46:22 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: ServerConfigInfo
 * @Description: appInit服务器返回信息
 * @author liubingsr
 * @date 2012-9-21 上午10:46:22
 * 
 */
public class AppInitInfo {
	private final static String TAG = "AppInitInfo";
	/**
	 * 自动注册并登录的用户信息
	 */
	private LogonUser mUserProfile = null;
	/**
	 * 登录密码
	 */
	private String mPassword = "";
	
	public LogonUser getUserProfile() {
		return mUserProfile;
	}
	public void setUserProfile(LogonUser user) {
		this.mUserProfile = user;
	}	
	
	/**
	* @Title: parse
	* @Description: json数据包解析出对象
	* @param jsonObject
	* @return null 解析错误
	*/
	public static AppInitInfo parse(JSONObject jsonObject) {
		AppInitInfo obj = new AppInitInfo();
		try {
			LogonUser profile = null;
			if (!jsonObject.isNull("autoRegisteredUser")) {
				profile = LogonUser.parse(jsonObject.getJSONObject("autoRegisteredUser"));
				obj.setUserProfile(profile);
			} else if (!jsonObject.isNull("logonUser")) {
				profile = LogonUser.parse(jsonObject.getJSONObject("logonUser"));
				obj.setUserProfile(profile);
			}
			if (!jsonObject.isNull("autoRegisteredUserPassword")) {
				if (profile != null) {
					profile.setPassword(jsonObject.getString("autoRegisteredUserPassword"));
				}
			} else if(!jsonObject.isNull("password")) {
				if (profile != null) {
					profile.setPassword(jsonObject.getString("password"));
				}
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
