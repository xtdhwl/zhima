/* 
 * @Title: ZMOrganizationObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;

/**
 * @ClassName: ZMOrganizationObject
 * @Description: 誉玺对象
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class ZMOrganizationObject extends ZMObject {
	private final static String TAG = "ZMOrganizationObject";			
	/**
	 * 公司全称
	 */
	private String mFullName = "";
	/**
	 * 联系人
	 */
	private String mContactName = "";
	/**
	 * 电话
	 */
	private String mTelephone = "";
	/**
	 * 邮箱
	 */
	private String mEmail = "";
	/**
	 * 城市名称
	 */
	private String mCityName = "";
	/**
	 * 地址
	 */
	private String mAddress = "";	
	/**
	 * 网址
	 */
	private String mWebsiteUrl = "";
	/**
	 * 赞计数(次数)
	 */
	private int mBlessingCount = 0;	
	/**
	 * 是否公开。只有公开的才允许分享
	 */
	private boolean mIsPublic = false;
	/**
	 * 是否允许留言
	 */
	private boolean mAllowComment = false;
	/**
	 * 创建时间
	 */
	private long mCreatedOn = 0;

	public ZMOrganizationObject() {
		super();
		mZMObjectType = ZMObjectKind.ORGANIZATION_OBJECT;
	}
	
	public String getCityName() {
		return mCityName;
	}
	public void setCityName(String city) {
		this.mCityName = city;
	}
	
	public String getTelephone() {
		return mTelephone;
	}
	public void setTelephone(String telNum) {
		this.mTelephone = telNum;
	}

	public String getAddress() {
		return mAddress;
	}
	public void setAddress(String address) {
		this.mAddress = address;
	}
	
	public String getFullName() {
		return mFullName;
	}
	public void setFullName(String fullName) {
		this.mFullName = fullName;
	}

	public String getEmail() {
		return mEmail;
	}
	public void setEmail(String email) {
		this.mEmail = email;
	}

	public String getContactName() {
		return mContactName;
	}
	public void setContactName(String contactName) {
		this.mContactName = contactName;
	}
	
	public String getWebsiteUrl() {
		return mWebsiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		this.mWebsiteUrl = websiteUrl;
	}
	
	public int getBlessingCount() {
		return mBlessingCount;
	}
	public void setBlessingCount(int count) {
		this.mBlessingCount = count;
	}	

	public boolean isPublic() {
		return mIsPublic;
	}
	public void setPublic(boolean isPublic) {
		this.mIsPublic = isPublic;
	}

	public boolean isAllowComment() {
		return mAllowComment;
	}
	public void setAllowComment(boolean allow) {
		this.mAllowComment = allow;
	}
	
	public long getCreatedDate() {
		return mCreatedOn;
	}
	public void setCreatedDate(long date) {
		this.mCreatedOn = date;
	}
	
	/**
	 * @Title: parse
	 * @Description: 解析誉玺对象
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);		
		if (!jsonObject.isNull("cityName")) {
			mCityName = jsonObject.getString("cityName");
		}
		if (!jsonObject.isNull("address")) {
			mAddress = jsonObject.getString("address");
		}
		if (!jsonObject.isNull("telephone")) {
			mTelephone = jsonObject.getString("telephone");
		}
		if (!jsonObject.isNull("fullName")) {
			mFullName = jsonObject.getString("fullName");
		}
		if (!jsonObject.isNull("email")) {
			mEmail = jsonObject.getString("email");
		}
		if (!jsonObject.isNull("contactName")) {
			mContactName = jsonObject.getString("contactName");
		}
		if (!jsonObject.isNull("websiteUrl")) {
			mWebsiteUrl = jsonObject.getString("websiteUrl");
		}
		
		if (!jsonObject.isNull("privacyCode")) {
			mIsPublic = jsonObject.getInt("privacyCode") == 2;
		}
		if (!jsonObject.isNull("replyCode")) {
			mAllowComment = jsonObject.getInt("replyCode") == 2;
		}
		if (!jsonObject.isNull("createdOn")) {
			mCreatedOn = jsonObject.getLong("createdOn");
		}
	}
}
