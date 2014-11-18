/* 
 * @Title: User.java
 * Created by liubingsr on 2012-5-18 下午5:02:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.BloodGroup;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: User
 * @Description: 用户
 * @author liubingsr
 * @date 2012-5-18 下午5:02:53
 * 
 */
public class User extends BaseData implements Cloneable{
	private final static String TAG = "User";
	/**
	 * 用户标识
	 */
	protected long mUserId = ZMConsts.INVALID_ID;
	/**
	 * 芝麻ID(zmuserid)登录帐号名
	 */
	protected String mZMUserId;
	/**
	 * 昵称
	 */
	protected String mNickname;
	/**
	 * 头像URL
	 */
	protected String mImageUrl;
	/**
	 * 性别
	 * 
	 * @see com.zhima.base.consts.ZMConsts.GenderType
	 */
	protected String mGender;
	/**
	 * 城市
	 */
	protected long mCityId = 0;
	/**
	 * 绑定的学校id
	 */
	protected long mSchoolId = 0;
	/**
	 * 绑定的未知庶版块id
	 */
	protected long mBoardId = 0;
	/**
	 * 个人签名
	 */
	protected String mSignature = "";
	/**
	 * 登录密码
	 */
	protected String mPassword = "";
	/**
	 * GPS坐标
	 */
	protected GeoCoordinate mGeo;	
	/**
	 * 手机号
	 */
	protected String mMobileNumber = "";
	/**
	 * 邮箱
	 */
	protected String mEmail = "";
	/**
	 * 生日
	 */
	protected long mBirthday = 0;
	/**
	 * 婚姻
	 */
	protected int mMarriage = -1;
	/**
	 * 血型
	 * @see com.zhima.base.consts.ZMConsts.BloodGroup
	 */
	protected String mBloodType = BloodGroup.UNKNOWN;
	/**
	 * 星座
	 */
	protected String mAstro = "";
	/**
	 * 年龄
	 */
	protected int mAge = 0;
	/**
	 * 帐号是否有效。false：帐号被删除，true：帐号有效
	 */
	protected boolean mValid = true;
	/**
	 * 注册日期
	 */
	protected long mCreateTime = System.currentTimeMillis();

	public User() {
	}
	public User(long userId) {
		mUserId = userId;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	public User(User user) {
		if (user != null) {
			mUserId = user.getUserId();
			mZMUserId = user.getZMUserId();
			mNickname = user.getNickname();
			mPassword = user.getPassword();
			mGender = user.getGender();
			mImageUrl = user.getImageUrl();
			mCityId = user.getCityId();			
			mBoardId = user.getBoardId();
			mGeo = user.getGeo();
		}
	}
	
	@Override
	public long getId() {
		return mUserId;
	}

	public long getUserId() {
		return mUserId;
	}
	public void setUserId(long userId) {
		this.mUserId = userId;
	}	
	
	public String getZMUserId() {
		return this.mZMUserId;
	}
	public void setZMUserId(String zmUserId) {
		this.mZMUserId = zmUserId;
	}
	
	public String getNickname() {
		return mNickname;
	}
	public void setNickname(String nickname) {
		this.mNickname = nickname;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	public String getGender() {
		return mGender;
	}
	public void setGender(String gender) {
		this.mGender = gender;
	}

	public long getCityId() {
		return this.mCityId;
	}
	public void setCityId(long cityId) {
		this.mCityId = cityId;
	}

	public long getSchoolId() {
		return this.mSchoolId;
	}
	public void setSchoolId(long schoolId) {
		this.mSchoolId = schoolId;
	}

	public long getBoardId() {
		return this.mBoardId;
	}
	public void setBoardId(long boardId) {
		this.mBoardId = boardId;
	}
	
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String password) {
		this.mPassword = password;
	}
	
	public String getSignature() {
		return this.mSignature;
	}
	public void setSignature(String signature) {
		this.mSignature = signature;
	}
	
	public GeoCoordinate getGeo() {
		return mGeo;
	}
	public void setGeo(GeoCoordinate geo) {
		this.mGeo = geo;
	}
	
	public String getMobileNumber() {
		return this.mMobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mMobileNumber = mobileNumber;
	}

	public String getEmail() {
		return this.mEmail;
	}
	public void setEmail(String email) {
		this.mEmail = email;
	}

	public long getBirthday() {
		return this.mBirthday;
	}
	public void setBirthday(long birthday) {
		this.mBirthday = birthday;
	}

	public int getMarriage() {
		return this.mMarriage;
	}
	public void setMarriage(int marriage) {
		this.mMarriage = marriage;
	}

	public String getBloodType() {
		return this.mBloodType;
	}
	public void setBloodType(String bloodType) {
		this.mBloodType = bloodType;
	}
	
	public int getAge() {
		return this.mAge;
	}
	public void setAge(int age) {
		this.mAge = age;
	}
	
	public String getAstro() {
		return this.mAstro;
	}
	public void setAstro(String astro) {
		this.mAstro = astro;
	}	

	public boolean isValid() {
		return mValid;
	}
	public void setValid(boolean valid) {
		this.mValid = valid;
	}
	
	public long getCreateTime() {
		return this.mCreateTime;
	}
	public void setCreateTime(long createTime) {
		this.mCreateTime = createTime;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("userId:" + mUserId);
		sb.append(",nickname:" + mNickname);
		sb.append(",gender:" + mGender);
		sb.append(",birthday:" + mBirthday);
		sb.append(",bloodType:" + mBloodType);
		sb.append(",astro:" + mAstro);
		sb.append(",age:" + mAge);
		sb.append(",signature:" + mSignature);
		sb.append(",imageUrl:" + mImageUrl);
		sb.append(",cityId:" + mCityId);
		sb.append(",createTime:" + mCreateTime);
		return sb.toString();	
	}

	public void create(JSONObject jsonObject) throws JSONException {
		mUserId = jsonObject.getLong("id");		
		if (!jsonObject.isNull("displayName")) {
			mNickname = jsonObject.getString("displayName");
		}
		if (!jsonObject.isNull("username")) {
			mZMUserId = jsonObject.getString("username");
		} else if (!jsonObject.isNull("zmUserid")) {
			mZMUserId = jsonObject.getString("zmUserid");
		}
		if (!jsonObject.isNull("gender")) {
			mGender = jsonObject.getString("gender");
		}
		if (!jsonObject.isNull("imageUrl")) {
			mImageUrl = jsonObject.getString("imageUrl");
		}
		if (!jsonObject.isNull("cityId")) {
			mCityId = jsonObject.getLong("cityId");
		}
		if (!jsonObject.isNull("schoolId")) {
			mSchoolId = jsonObject.getLong("schoolId");
		}
		if (!jsonObject.isNull("boardId")) {
			mBoardId = jsonObject.getLong("boardId");
		}
		if (!jsonObject.isNull("geoCoord")) {
			mGeo = GeoCoordinate.parse(jsonObject.getJSONObject("geoCoord"));
		}
		if (!jsonObject.isNull("dateOfBirth")) {
			mBirthday = jsonObject.getLong("dateOfBirth");
		}
		if (!jsonObject.isNull("bloodType")) {
			mBloodType = jsonObject.getString("bloodType");
		}
		if (!jsonObject.isNull("constallation")) {
			mAstro = jsonObject.getString("constallation");
		}
		if (!jsonObject.isNull("age")) {
			mAge = jsonObject.getInt("age");
		}
		if (!jsonObject.isNull("signature")) {
			mSignature = jsonObject.getString("signature");
		}
		if (!jsonObject.isNull("createdOn")) {
			mCreateTime = jsonObject.getLong("createdOn");
		}		
	}
	/**
	* @Title: parse
	* @Description: 由json解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static User parse(JSONObject jsonObject) {
		User user = new User();
		try {			
			user.create(jsonObject);			
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			user = null;
		}
		return user;
	}
}
