/* 
 * @Title: ZMCardObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;

/**
 * @ClassName: ZMCardObject
 * @Description: 名玺空间
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class ZMCardObject extends ZMObject {
	private final static String TAG = "ZMCardObject";
	/**
	 * 城市名称
	 */
	private String mCityName = "";
	/**
	 * 地址
	 */
	private String mAddress = "";
	/**
	 * 电话
	 */
	private String mTelephone = "";
	/**
	 * 公司
	 */
	private String mCompanyName = "";
	/**
	 * 职位
	 */
	private String mJob = "";
	/**
	 * 邮箱
	 */
	private String mEmail = "";
	/**
	 * 姓名
	 */
	private String mContactName = "";
	/**
	 * 新浪微博
	 */
	private String mWeibo = "";
	/**
	 * 腾讯微博
	 */
	private String mTencentweibo = "";
	/**
	 * QQ号码
	 */
	private String mQQ = "";	
	/**
	 * 签名
	 */
	private String mSignature = "";
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

	public ZMCardObject() {
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
	
	public String getCompanyName() {
		return mCompanyName;
	}
	public void setCompanyName(String companyName) {
		this.mCompanyName = companyName;
	}

	public String getJob() {
		return mJob;
	}
	public void setJob(String job) {
		this.mJob = job;
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

	public String getWeibo() {
		return mWeibo;
	}
	public void setWeibo(String weibo) {
		this.mWeibo = weibo;
	}

	public String getTencentweibo() {
		return mTencentweibo;
	}
	public void setTencentweibo(String tencentweibo) {
		this.mTencentweibo = tencentweibo;
	}

	public String getQQ() {
		return mQQ;
	}
	public void setQQ(String qq) {
		this.mQQ = qq;
	}

	public String getSignature() {
		return mSignature;
	}
	public void setSignature(String signature) {
		this.mSignature = signature;
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
	 * @Description: 解析机构印对象
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
		if (!jsonObject.isNull("companyName")) {
			mCompanyName = jsonObject.getString("companyName");
		}
		if (!jsonObject.isNull("post")) {
			mJob = jsonObject.getString("post");
		}
		if (!jsonObject.isNull("email")) {
			mEmail = jsonObject.getString("email");
		}
		if (!jsonObject.isNull("contactName")) {
			mContactName = jsonObject.getString("contactName");
		}
		if (!jsonObject.isNull("weibo")) {
			mWeibo = jsonObject.getString("weibo");
		}
		if (!jsonObject.isNull("tencentweibo")) {
			mTencentweibo = jsonObject.getString("tencentweibo");
		}
		if (!jsonObject.isNull("qqnumber")) {
			mQQ = jsonObject.getString("qqnumber");
		}
		if (!jsonObject.isNull("signature")) {
			mSignature = jsonObject.getString("signature");
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
