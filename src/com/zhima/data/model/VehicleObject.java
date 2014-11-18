/* 
 * @Title: VehicleObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: VehicleObject
 * @Description: 交通工具
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class VehicleObject extends ZMObject {
	private final static String TAG = "VehicleObject";

	/**
	 * 速度称赞次数
	 */
	private int mSpeedCount = 0;
	/**
	 * 服务称赞次数
	 */
	private int mServiceCount = 0;
	/**
	 * 安全称赞次数
	 */
	private int mSecurityCount = 0;
	/**
	 * 电话
	 */
	private String mPhone = "";
	/**
	 * 手机号码
	 */
	private String mMobile = "";
	/**
	 * 服务范围
	 */
	private int mServiceRegion = 0;
	/**
	 * 行车路线
	 */
	private String mDrivingRoute = "";

	public VehicleObject() {
		super();
		mZMObjectType = ZMObjectKind.VEHICLE_OBJECT;
	}

	public int getSpeedCount() {
		return mSpeedCount;
	}

	public void setSpeedCount(int count) {
		this.mSpeedCount = count;
	}

	public int getServiceCount() {
		return mServiceCount;
	}

	public void setServiceCount(int serviceCount) {
		this.mServiceCount = serviceCount;
	}

	public int getSecurityCount() {
		return mSecurityCount;
	}

	public void setSecurityCount(int count) {
		this.mSecurityCount = count;
	}

	public String getMobile() {
		return mMobile;
	}

	public void setMobile(String mobile) {
		this.mMobile = mobile;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	public int getServiceRegion() {
		return mServiceRegion;
	}

	public void setServiceRegion(int region) {
		this.mServiceRegion = region;
	}

	public String getDrivingRoute() {
		return mDrivingRoute;
	}

	public void setDrivingRoute(String drivingRoute) {
		this.mDrivingRoute = drivingRoute;
	}

	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);
		mMobile = StringHelper.jsonNullToEmpty(jsonObject.getString("mobile"));
		mPhone = StringHelper.jsonNullToEmpty(jsonObject.getString("telephone"));
		mDrivingRoute = StringHelper.jsonNullToEmpty(jsonObject.getString("drivingRoute"));
	}
}
