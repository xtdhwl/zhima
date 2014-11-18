/* 
 * @Title: PublicToiletObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONObject;

/**
 * @ClassName: PublicToiletObject
 * @Description: 公共卫生间
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class PublicToiletObject extends ZMObject {
	/*
	 * 卫生称赞次数
	 */
	private int mHygieneCount;
	/*
	 * 服务称赞次数
	 */
	private int mServiceCount;
	/*
	 * 安全称赞次数
	 */
	private int mSecurityCount;
	/*
	 * 物业电话
	 */
	private int mPhone;
	/*
	 * 地址
	 */
	private int mAddress;

	/**
	 * @return mHygieneCount
	 */
	public int getHygieneCount() {
		return mHygieneCount;
	}
	/**
	 * @param count 要设置的 mHygieneCount
	 */
	public void setHygieneCount(int count) {
		this.mHygieneCount = count;
	}
	/**
	 * @return mServiceCount
	 */
	public int getServiceCount() {
		return mServiceCount;
	}
	/**
	 * @param serviceCount 要设置的 mServiceCount
	 */
	public void setServiceCount(int serviceCount) {
		this.mServiceCount = serviceCount;
	}
	/**
	 * @return mSecurityCount
	 */
	public int getSecurityCount() {
		return mSecurityCount;
	}
	/**
	 * @param count 要设置的 mSecurityCount
	 */
	public void setSecurityCount(int count) {
		this.mSecurityCount = count;
	}
	/**
	 * @return mPhone
	 */
	public int getPhone() {
		return mPhone;
	}
	/**
	 * @param mPhone 要设置的 mPhone
	 */
	public void setPhone(int phone) {
		this.mPhone = phone;
	}
	/**
	 * @return mAddress
	 */
	public int getAddress() {
		return mAddress;
	}
	/**
	 * @param address 要设置的 mAddress
	 */
	public void setAddress(int address) {
		this.mAddress = address;
	}
	@Override
	public void parse(JSONObject jsonObject) {		
	}
}
