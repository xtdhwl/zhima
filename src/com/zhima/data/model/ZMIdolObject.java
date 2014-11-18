/* 
 * @Title: ZMIdolObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.DateUtils;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMIdolObject
 * @Description: 知天使
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class ZMIdolObject extends ZMObject {
	private final static String TAG = "ZMIdolObject";

	/**
	 * 昵称
	 */
	private String mNickname = "";
	/**
	 * 性别
	 */
	private String mGender = "";
	/**
	 * 生日
	 */
	private long mBirthday = 0;
	/**
	 * 年龄
	 */
	private int mAge = 0;
	/**
	 * 血型
	 */
	private String mBloodType = "";
	/**
	 * 星座
	 */
	private String mAstro = "";
	/**
	 * 职业
	 */
	private String mProfession = "";
	/**
	 * 爱好
	 */
	private String mInterest = "";
	/**
	 * 身高
	 */
	private int mHeight = 0;
	/**
	 * 喜欢计数(次数)
	 */
	private int mLoveCount = 0;
	/**
	 * 签名
	 */
	private String mSignature = "";
	/**
	 * 知天使职业id
	 */
	private long mJobId = 0;
	/**
	 * 知天使职业
	 */
	private String mJob = "";
	
	public ZMIdolObject() {
		super();
		mZMObjectType = ZMObjectKind.IDOL_OBJECT;
	}

	public String getNickname() {
		return mNickname;
	}
	public void setNickname(String nickname) {
		this.mNickname = nickname;
	}

	public String getGender() {
		return mGender;
	}
	public void setGender(String gender) {
		this.mGender = gender;
	}

	public long getBirthday() {
		return mBirthday;
	}
	public void setBirthday(long birthday) {
		this.mBirthday = birthday;
	}
	
	public int getAge() {		
		return mAge;		
	}
	public void setAge(int age) {
		this.mAge = age;
	}
	
	public String getBloodType() {
		return mBloodType;
	}
	public void setBloodType(String bloodType) {
		this.mBloodType = bloodType;
	}

	public String getAstro() {
		return mAstro;
	}
	public void setAstro(String astro) {
		this.mAstro = astro;
	}
	
	public String getProfession() {
		return mProfession;
	}
	public void setProfession(String profession) {
		this.mProfession = profession;
	}
	
	public String getInterest() {
		return mInterest;
	}
	public void setInterest(String interest) {
		this.mInterest = interest;
	}

	public int getHeight() {
		return mHeight;
	}
	public void setHeight(int height) {
		this.mHeight = height;
	}

	public int getLoveCount() {
		return mLoveCount;
	}
	public void setLoveCount(int count) {
		this.mLoveCount = count;
	}
	
	public String getSignature() {
		return mSignature;
	}
	public void setSignature(String count) {
		this.mSignature = count;
	}
	
	public long getJobId() {
		return mJobId;
	}
	public void setJobId(long id) {
		this.mJobId = id;
	}
	
	public String getJob() {
		return mJob;
	}
	public void setJob(String job) {
		this.mJob = job;
	}
	/**
	* @Title: parse
	* @Description: 解析知天使对象
	* @param jsonObject
	* @throws JSONException 
	*/
	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);
		mGender = jsonObject.getString("gender");			
		mBloodType = jsonObject.getString("bloodType");
		mAstro = StringHelper.jsonNullToEmpty(jsonObject.getString("constallation"));
		if (!jsonObject.isNull("height")) {
			mHeight = jsonObject.getInt("height");
		}
		if (!jsonObject.isNull("dateOfBirth")) {
			mBirthday = jsonObject.getLong("dateOfBirth");
			mAge = DateUtils.getIntervalYear(mBirthday, System.currentTimeMillis());
		}			
		if (!jsonObject.isNull("signature")) {
			mSignature = StringHelper.jsonNullToEmpty(jsonObject.getString("signature"));
		}
		if (!jsonObject.isNull("idolJobId")) {
			mJobId = jsonObject.getLong("idolJobId");
		}
		if (!jsonObject.isNull("job")) {
			mJob = StringHelper.jsonNullToEmpty(jsonObject.getString("job"));
		}
	}
}
