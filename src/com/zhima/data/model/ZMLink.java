/* 
* @Title: ZMLink.java
* Created by liubingsr on 2013-1-18 下午4:47:20 
* Copyright (c) 2013 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import com.zhima.base.consts.ZMConsts;

/**
 * @ClassName: ZMLink
 * @Description: 联系信息
 * @author liubingsr
 * @date 2013-1-18 下午4:47:20
 *
 */
public class ZMLink extends BaseData {
	/**
	 * id
	 */
	private long mId = ZMConsts.INVALID_ID;
	/**
	 * 名称
	 */
	private String mName = "";
	/**
	 * 电话
	 */
	private String mPhone = "";
	/**
	 * 地址
	 */
	private String mAddress = "";
	/**
	 * gps坐标
	 */
	private GeoCoordinate mGeo = null;
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}

	public String getPhone() {
		return mPhone;
	}
	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	public String getAddress() {
		return mAddress;
	}
	public void setAddress(String address) {
		this.mAddress = address;
	}

	public GeoCoordinate getGeo() {
		return mGeo;
	}
	public void setGeo(GeoCoordinate geo) {
		this.mGeo = geo;
	}	
}
