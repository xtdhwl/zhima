/* 
 * @Title: VoUserInfo.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

/**
* @ClassName: VoUserInfo
* @Description: 用户信息
* @author liubingsr
* @date 2012-8-18 下午1:54:30
*
*/
public final class VoUserInfo {
	/**
	 * 昵称
	 */
	private String displayName;
	/**
	 * 性别
	 */
	private String gender;
	/**
	 * 出生日期
	 */
	private long dateOfBirth;
	/**
	 * 血型
	 */
	private String bloodType;
	/**
	 * 签名
	 */
	private String signature;
	/**
	 * 城市
	 */
	private long cityId;
	/**
	 * 未知庶板块id
	 */
	private long boardId;
	/**
	 * 学校id
	 */
	private long schoolId;

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public long getDateOfBirth() {
		return dateOfBirth;
	}	
	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getBloodType() {
		return bloodType;
	}
	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}

	public long getCityId() {
		return cityId;
	}
	public void setCityId(long cityId) {
		this.cityId = cityId;
	}
	
	public long getBoardId() {
		return boardId;
	}
	public void setBoardId(long boardId) {
		this.boardId = boardId;
	}
	public long getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(long schoolId) {
		this.schoolId = schoolId;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("displayName:" + displayName + ",");
		sb.append("gender:" + gender + ",");
		sb.append("dateOfBirth:" + dateOfBirth + ",");
		sb.append("bloodType:" + bloodType + ",");
		sb.append("signature:" + signature + ",");
		sb.append("cityId:" + cityId);
		return sb.toString();
	}
}
