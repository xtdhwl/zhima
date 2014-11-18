/* 
 * @Title: PublicPlaceObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: PublicPlaceObject
 * @Description: 公共场所对象
 * @author liubingsr
 * @date 2012-7-12 下午5:27:26
 * 
 */
public class PublicPlaceObject extends ZMObject {
	private final static String TAG = "PublicPlaceObject";

	/**
	 * 铭牌号码
	 */
	private String mNameplate = "";
	/**
	 * 环境称赞次数
	 */
	private int mEnvCount = 0;
	/**
	 * 服务称赞次数
	 */
	private int mServiceCount = 0;
	/**
	 * 质量称赞次数
	 */
	private int mQualityCount = 0;
	/**
	 * 手机号码
	 */
	private String mMobile = "";
	/**
	 * 电话
	 */
	private String mPhone = "";
	/**
	 * 地址
	 */
	private String mAddress = "";
	/**
	 * 营业时间
	 */
	private String mBusinessHours = "";
	/**
	 * 行车路线
	 */
	private String mDrivingRoute = "";
	/**
	 * 是否可以停车
	 */
	private boolean mCanParked = true;	
	
	public PublicPlaceObject() {
		super();
		mZMObjectType = ZMObjectKind.PUBLICPLACE_OBJECT;
	}

	/**
	 * @return mNameplate
	 */
	public String getNameplate() {
		return mNameplate;
	}

	/**
	 * @param nameplate
	 *            要设置的 mNameplate
	 */
	public void setNameplate(String nameplate) {
		this.mNameplate = nameplate;
	}

	/**
	 * @return mEnvCount
	 */
	public int getEnvCount() {
		return mEnvCount;
	}

	/**
	 * @param envCount
	 *            要设置的 mEnvCount
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
	 * @param serviceCount
	 *            要设置的 mServiceCount
	 */
	public void setServiceCount(int serviceCount) {
		this.mServiceCount = serviceCount;
	}

	/**
	 * @return mQualityCount
	 */
	public int getQualityCount() {
		return mQualityCount;
	}

	/**
	 * @param qualityCount
	 *            要设置的 qualityCount
	 */
	public void setQualityCount(int qualityCount) {
		this.mQualityCount = qualityCount;
	}

	public String getMobile() {
		return mMobile;
	}

	/**
	 * @param mobile
	 *            要设置的 mMobile
	 */
	public void setMobile(String mobile) {
		this.mMobile = mobile;
	}

	/**
	 * @return mPhone
	 */
	public String getPhone() {
		return mPhone;
	}

	/**
	 * @param phone
	 *            要设置的 mPhone
	 */
	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	/**
	 * @return mAddress
	 */
	public String getAddress() {
		return mAddress;
	}

	/**
	 * @param address
	 *            要设置的 mAddress
	 */
	public void setAddress(String address) {
		this.mAddress = address;
	}

	public String getBusinessHours() {
		return mBusinessHours;
	}

	public void setBusinessHours(String businessHours) {
		this.mBusinessHours = businessHours;
	}

	public String getDrivingRoute() {
		return mDrivingRoute;
	}

	public void setDrivingRoute(String drivingRoute) {
		this.mDrivingRoute = drivingRoute;
	}

	public boolean getCanParked() {
		return mCanParked;
	}

	public void setCanParked(boolean canParked) {
		this.mCanParked = canParked;
	}

	/**
	 * 固定电话是否有效
	 * @return true为有效，false为无效
	 */
	public boolean isTelepPhoneValid() {
		return !TextUtils.isEmpty(mPhone);
	}

	/**
	 * @Title: parse
	 * @Description: 由json数据包解析出PublicPlaceObject
	 * @param jsonObject
	 * @throws JSONException 
	 */
	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);		
		if (!jsonObject.isNull("mobile")) {
			mMobile = StringHelper.jsonNullToEmpty(jsonObject.getString("mobile"));
		}
		if (!jsonObject.isNull("telephone")) {
			mPhone = StringHelper.jsonNullToEmpty(jsonObject.getString("telephone"));
		}
		if (!jsonObject.isNull("address")) {
			mAddress = StringHelper.jsonNullToEmpty(jsonObject.getString("address"));
		}
		if (!jsonObject.isNull("businessHours")) {
			mBusinessHours = StringHelper.jsonNullToEmpty(jsonObject.getString("businessHours"));
		}
		if (!jsonObject.isNull("drivingRoute")) {
			mDrivingRoute = StringHelper.jsonNullToEmpty(jsonObject.getString("drivingRoute"));
		}
		if (!jsonObject.isNull("parkingPermitted")) {
			mCanParked = jsonObject.getBoolean("parkingPermitted");
		}
	}
}
