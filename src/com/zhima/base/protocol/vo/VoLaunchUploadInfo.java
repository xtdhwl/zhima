/* 
* @Title: VoLaunchUploadInfo.java
* Created by liubingsr on 2012-9-21 上午10:46:22 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.protocol.vo;

import com.zhima.data.model.AppVersion;
import com.zhima.data.model.LaunchUploadInfo;

/**
 * @ClassName: VoLaunchUploadInfo
 * @Description: app打开时上传信息
 * @author liubingsr
 * @date 2012-9-21 上午10:46:22
 *
 */
public class VoLaunchUploadInfo {	
	/**
	 *  App安装版本
	 */
	private  VoAppVersion installedVersion;
	/**
	 * 平台
	 */
	private String platform = "android";
	/**
	 * 平台版本
	 */
	private String platformVersion = "";
	/**
	 *  手机型号
	 */
	private String phoneModel = "";
	/**
	 * 运营商名称
	 */
	private String carrierName = "";
	/**
	 * IMEI设备号
	 */
	private String imei = "";
	/**
	 * IMSI国际移动用户识别码
	 */
	private String imsi = "";
	/**
	 * 手机号
	 */
	private String mobileNumber = "";
	/**
	 * 分发渠道
	 */
	private String publishChannel = "";
	
	public static VoLaunchUploadInfo createInstance(LaunchUploadInfo info) {
		VoLaunchUploadInfo vo = new VoLaunchUploadInfo();
		VoAppVersion voVersion = new VoAppVersion();
		AppVersion version = info.getInstalledVersion();
		voVersion.setMajorVersion(version.getMajorVersion());
		voVersion.setMinorVersion(version.getMinorVersion());
		voVersion.setRevision(version.getRevision());
		voVersion.setBuild(version.getBuildNumber());
		voVersion.setPlatform(version.getPlatform());
		
		vo.setInstalledVersion(voVersion);
		vo.setPlatform(info.getPlatform());
		vo.setPlatformVersion(info.getPlatformVersion());
		vo.setPhoneModel(info.getPhoneModel());
		vo.setCarrierName(info.getCarrierName());
		vo.setImei(info.getIMEI());
		vo.setImsi(info.getIMSI());
		vo.setMobileNumber(info.getMobileNumber());
		vo.setPublishChannel(info.getPublishChannel());
		return vo;
	}
	public VoAppVersion getInstalledVersion() {
		return installedVersion;
	}
	public void setInstalledVersion(VoAppVersion installedVersion) {
		this.installedVersion = installedVersion;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getPlatformVersion() {
		return platformVersion;
	}
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}
	public String getPhoneModel() {
		return phoneModel;
	}
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	public String getCarrierName() {
		return carrierName;
	}
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getPublishChannel() {
		return publishChannel;
	}
	public void setPublishChannel(String channel) {
		this.publishChannel = channel;
	}
}
