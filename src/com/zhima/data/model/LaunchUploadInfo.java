/* 
* @Title: LaunchUploadInfo.java
* Created by liubingsr on 2012-9-21 上午10:46:22 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import com.zhima.base.config.SystemConfig;

/**
 * @ClassName: LaunchUploadInfo
 * @Description: app打开时上传信息
 * @author liubingsr
 * @date 2012-9-21 上午10:46:22
 *
 */
public class LaunchUploadInfo {	
	/**
	 *  App安装版本
	 */
	private  AppVersion mInstalledVersion;
	/**
	 * 平台
	 */
	private String mPlatform = SystemConfig.PLATFORM;
	/**
	 * 平台版本
	 */
	private String mPlatformVersion = "";
	/**
	 *  手机型号
	 */
	private String mPhoneModel = "";
	/**
	 * 运营商名称
	 */
	private String mCarrierName = "";
	/**
	 * IMEI设备号
	 */
	private String mIMEI = "";
	/**
	 * IMSI国际移动用户识别码
	 */
	private String mIMSI = "";
	/**
	 * 手机号
	 */
	private String mMobileNumber = "";
	/**
	 * 分发渠道
	 */
	private String mPublishChannel = "";
	
	public AppVersion getInstalledVersion() {
		return mInstalledVersion;
	}
	public void setInstalledVersion(AppVersion installedVersion) {
		this.mInstalledVersion = installedVersion;
	}
	public String getPlatform() {
		return mPlatform;
	}
	public void setPlatform(String platform) {
		this.mPlatform = platform;
	}
	public String getPlatformVersion() {
		return mPlatformVersion;
	}
	public void setPlatformVersion(String platformVersion) {
		this.mPlatformVersion = platformVersion;
	}
	public String getPhoneModel() {
		return mPhoneModel;
	}
	public void setPhoneModel(String phoneModel) {
		this.mPhoneModel = phoneModel;
	}
	public String getCarrierName() {
		return mCarrierName;
	}
	public void setCarrierName(String carrierName) {
		this.mCarrierName = carrierName;
	}
	public String getIMEI() {
		return mIMEI;
	}
	public void setIMEI(String imei) {
		this.mIMEI = imei;
	}
	public String getIMSI() {
		return mIMSI;
	}
	public void setIMSI(String imsi) {
		this.mIMSI = imsi;
	}
	public String getMobileNumber() {
		return mMobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mMobileNumber = mobileNumber;
	}
	public String getPublishChannel() {
		return mPublishChannel;
	}
	public void setPublishChannel(String channel) {
		this.mPublishChannel = channel;
	}
}
