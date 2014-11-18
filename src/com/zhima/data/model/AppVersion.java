/* 
 * @Title: AppVersion.java
 * Created by liubingsr on 2012-9-21 下午12:23:03 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.config.SystemConfig;
import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: AppVersion
 * @Description: app版本信息
 * @author liubingsr
 * @date 2012-9-21 上午10:49:21
 * 
 */
public final class AppVersion {
	private final static String TAG = "AppVersion";
	/**
	 * 平台
	 */
	private String mPlatform = SystemConfig.PLATFORM;
	/**
	 * 大版本号
	 */
	private int mMajorVersion = 0;
	/**
	 * 小版本号
	 */
	private int mMinorVersion = 0;
	/**
	 * 修正号，如：12 
	 */
	private int mRevision = 0;
	/**
	 * build序号
	 */
	private String mBuildNumber = "";
	/**
	 * apk下载URL
	 */
	private String mDownloadUrl = "";
	/**
	 * 版本更新描述文字
	 */
	private String mChangeLog = "";

	public String getPlatform() {
		return mPlatform;
	}
	public void setPlatform(String platform) {
		this.mPlatform = platform;
	}

	public int getMajorVersion() {
		return mMajorVersion;
	}
	public void setMajorVersion(int majorVersion) {
		this.mMajorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return mMinorVersion;
	}
	public void setMinorVersion(int minorVersion) {
		this.mMinorVersion = minorVersion;
	}
	
	public int getRevision() {
		return mRevision;
	}
	public void setRevision(int revision) {
		this.mRevision = revision;
	}
	
	public String getBuildNumber() {
		return mBuildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.mBuildNumber = buildNumber;
	}
	
	public String getDownloadUrl() {
		return mDownloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.mDownloadUrl = downloadUrl;
	}
	
	public String getDescription() {
		return mChangeLog;
	}
	public void setDescription(String description) {
		this.mChangeLog = description;
	}
	/**
	* @Title: getVersion
	* @Description: 获取版本号信息
	* @return
	* String
	*/
	public String getVersion() {
		return mMajorVersion + "." + mMinorVersion + "." + mRevision;
	}
	/**
	* @Title: parse
	* @Description: json数据包解析出对象
	* @param jsonObject
	* @return null 解析错误
	*/
	public static AppVersion parse(JSONObject jsonObject) {
		AppVersion obj = new AppVersion();
		try {
			obj.setPlatform(jsonObject.getString("platform"));
			obj.setMajorVersion(jsonObject.getInt("majorVersion"));
			obj.setMinorVersion(jsonObject.getInt("minorVersion"));
			if (!jsonObject.isNull("revision")) {
				obj.setRevision(jsonObject.getInt("revision"));
			}
			if (!jsonObject.isNull("build")) {
				obj.setBuildNumber(jsonObject.getString("build"));
			}
			if (!jsonObject.isNull("dowloadUrl")) {
				obj.setDownloadUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("dowloadUrl")));
			}
			if (!jsonObject.isNull("changeLog")) {
				obj.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("changeLog")));
			}			
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
