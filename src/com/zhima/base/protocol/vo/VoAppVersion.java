/* 
* @Title: VoAppVersion.java
* Created by liubingsr on 2012-9-21 下午4:12:27 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.protocol.vo;

/**
* @ClassName: VoAppVersion
* @Description: app版本信息
* @author liubingsr
* @date 2012-9-21 上午10:49:21
*
*/
public class VoAppVersion {
	/**
	 * 平台
	 */
	private String platform = "android";
	/**
	 * 大版本号
	 */
	private int majorVersion;
	/**
	 *  小版本号
	 */
	private int minorVersion;
	/**
	 * 修正号，如：12 
	 */
	private int revision;
	/**
	 * build序号
	 */
	private String build;
	/**
	 * apk下载URL
	 */
	private String  dowloadUrl;
	
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public int getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	public int getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public String getDowloadUrl() {
		return dowloadUrl;
	}
	public void setDowloadUrl(String downloadUrl) {
		this.dowloadUrl = downloadUrl;
	}		
}
