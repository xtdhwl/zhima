/* 
 * @Title: CommerceObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: CommerceObject
 * @Description: 商户
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class CommerceObject extends ZMObject {
	private final static String TAG = "CommerceObject";

	/**
	 * 铭牌号码
	 */
	private String mNameplate;
	/**
	 * 环境称赞次数
	 */
	private int mEnvCount;
	/**
	 * 服务称赞次数
	 */
	private int mServiceCount;
	/**
	 * 质量称赞次数
	 */
	private int mQualityCount;
	/**
	 * 手机号码
	 */
	private String mMobile;
	/**
	 * 电话
	 */
	private String mPhone;
	/**
	 * 地址
	 */
	private String mAddress;
	/**
	 * 营业时间
	 */
	private String mBusinessHours;
	/**
	 * 行车路线
	 */
	private String mDrivingRoute;
	/**
	 * 是否可以停车
	 */
	private boolean mCanParked;

	/**
	* <p>Title: </p>
	* <p>Description: </p>
	* @param objectType
	*/
	public CommerceObject() {
		super();
		mZMObjectType = ZMObjectKind.BUSINESS_OBJECT;
	}

	public String getNameplate() {
		return mNameplate;
	}

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
	 * @Title: parse
	 * @Description: 由json数据包解析出CommerceObject
	 * @param jsonObject
	 * @throws JSONException 
	 */
	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);
		mMobile = StringHelper.jsonNullToEmpty(jsonObject.getString("mobile"));
		mPhone = StringHelper.jsonNullToEmpty(jsonObject.getString("telephone"));
		mAddress = StringHelper.jsonNullToEmpty(jsonObject.getString("address"));
		mBusinessHours = StringHelper.jsonNullToEmpty(jsonObject.getString("businessHours"));
		mDrivingRoute = StringHelper.jsonNullToEmpty(jsonObject.getString("drivingRoute"));
		mCanParked = jsonObject.getBoolean("parkingPermitted");
	}
}
