/* 
* @Title: AppUpgrade.java
* Created by liubingsr on 2012-9-25 上午11:58:55 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: AppUpgrade
 * @Description: 服务器端返回的版本信息
 * @author liubingsr
 * @date 2012-9-25 上午11:58:55
 *
 */
public class AppUpgrade {
	private final static String TAG = "AppUpgrade";
	/**
	 * 版本信息
	 */
	private AppVersion mLatestVersion = null;
	/**
	 * 是否强制更新。true:强制更新
	 */
	private boolean mMustUpdate = false;
	
	public AppVersion getLatestVersion() {
		return mLatestVersion;
	}
	public void setLatestVersion(AppVersion latestVersion) {
		this.mLatestVersion = latestVersion;
	}	
	
	public boolean isMustUpdate() {
		return mMustUpdate;
	}
	public void setMustUpdate(boolean mustUpdate) {
		this.mMustUpdate = mustUpdate;
	}
	/**
	* @Title: parse
	* @Description: json数据包解析出对象
	* @param jsonObject
	* @return null 解析错误
	*/
	public static AppUpgrade parse(JSONObject jsonObject) {
		AppUpgrade obj = new AppUpgrade();
		try {
			if (!jsonObject.isNull("latestVersion")) {
				obj.setLatestVersion(AppVersion.parse(jsonObject.getJSONObject("latestVersion")));
			}			
			if (!jsonObject.isNull("mustUpdate")) {
				obj.setMustUpdate(jsonObject.getBoolean("mustUpdate"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
