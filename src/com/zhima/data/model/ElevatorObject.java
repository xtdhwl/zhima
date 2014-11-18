/* 
 * @Title: ElevatorObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

/**
 * @ClassName: ElevatorObject
 * @Description: 电梯
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class ElevatorObject extends ZMObject {
	/*
	 * 类型
	 */
	private String mType;
	/*
	 * 环境称赞次数
	 */
	private int mEnvCount;
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
	 * @return mType
	 */
	public String getType() {
		return mType;
	}
	/**
	 * @param type 要设置的 mType
	 */
	public void setType(String type) {
		this.mType = type;
	}
	/**
	 * @return mEnvCount
	 */
	public int getEnvCount() {
		return mEnvCount;
	}
	/**
	 * @param envCount 要设置的 mEnvCount
	 */
	public void setEnvCount(int envCount) {
		this.mEnvCount = envCount;
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
}
