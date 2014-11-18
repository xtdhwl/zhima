/* 
 * @Title: ZMCouplesObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.BloodGroup;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMCouplesObject
 * @Description: 情侣知天使
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class ZMCouplesObject extends ZMObject {
	private final static String TAG = "ZMLoversObject";
	/**
	 * 男方名字
	 */
	private String mMaleName = "";
	/**
	 * 女方名字
	 */
	private String mFemaleName = "";
	/**
	 * 男方来自城市名称
	 */
	private String mMaleCityName = "";
	/**
	 * 女方来自城市名称
	 */
    private String mFemaleCityName = "";
	/**
	 * 男方血型
	 */
	private String mMaleBloodType = BloodGroup.UNKNOWN;
	/**
	 * 女方血型
	 */
	private String mFemaleBloodType = BloodGroup.UNKNOWN;
	/**
	 * 男方生日
	 */
	private long mMaleBirthday = System.currentTimeMillis();
	/**
	 * 女方生日
	 */
	private long mFemaleBirthday = System.currentTimeMillis();
	/**
	 * 男方星座
	 */
	private String mMaleAstro = "";
	/**
	 * 女方星座
	 */
	private String mFemaleAstro = "";
	/**
	 * 男方身高
	 */
	private int mMaleHeight = 0;
	/**
	 * 女方身高
	 */
	private int mFemaleHeight = 0;
	/**
	 * 男方学校名称
	 */
	private String mMaleSchoolName = "";
	/**
	 * 女方学校名称
	 */
	private String mFemaleSchoolName = "";
	/**
	 * 结婚所在城市名
	 */
	private String mMarriedCity = "";
	/**
	 * 男方头像地址
	 */
	private String mMaleImageUrl = "";	
	/**
	 * 女方头像地址
	 */
	private String mFemaleImageUrl = "";
	/**
	 * 祝福计数(次数)
	 */
	private int mBlessingCount = 0;
	/**
	 * 爱情宣言
	 */
	private String mLoveVow = "";	
	/**
	 * 结婚时间
	 */
	private long mMarriedDate = 0;
	/**
	 * 领证纪念日
	 */
	private long mRegisterDate = 0;
	/**
	 * 恋爱纪念日
	 */
	private long mMeetingDate = 0;
	/**
	 * 是否公开。只有公开的才允许分享
	 */
	private boolean mIsPublic = false;
	/**
	 * 是否允许留言
	 */
	private boolean mAllowComment = false;
	
	
	public ZMCouplesObject() {
		super();
		mZMObjectType = ZMObjectKind.WEDDING_OBJECT;
	}
	
	public String getMaleName() {
		return mMaleName;
	}

	public void setMaleName(String name) {
		this.mMaleName = name;
	}

	public String getFemaleName() {
		return mFemaleName;
	}
	public void setFemaleName(String name) {
		this.mFemaleName = name;
	}

	public String getMaleCityName() {
		return mMaleCityName;
	}

	public void setMaleCityName(String cityName) {
		this.mMaleCityName = cityName;
	}

	public String getFemaleCityName() {
		return mFemaleCityName;
	}
	public void setFemaleCityName(String cityName) {
		this.mFemaleCityName = cityName;
	}

	public String getMaleBloodType() {
		return mMaleBloodType;
	}
	public void setMaleBloodType(String bloodType) {
		this.mMaleBloodType = bloodType;
	}

	public String getFemaleBloodType() {
		return mFemaleBloodType;
	}
	public void setFemaleBloodType(String bloodType) {
		this.mFemaleBloodType = bloodType;
	}

	public long getMaleBirthday() {
		return mMaleBirthday;
	}
	public void setMaleBirthday(long birthday) {
		this.mMaleBirthday = birthday;
	}

	public long getFemaleBirthday() {
		return mFemaleBirthday;
	}

	public void setFemaleBirthday(long birthday) {
		this.mFemaleBirthday = birthday;
	}

	public String getMaleAstro() {
		return mMaleAstro;
	}

	public void setMaleAstro(String astro) {
		this.mMaleAstro = astro;
	}

	public String getFemaleAstro() {
		return mFemaleAstro;
	}

	public void setFemaleAstro(String astro) {
		this.mFemaleAstro = astro;
	}

	public int getMaleHeight() {
		return mMaleHeight;
	}

	public void setMaleHeight(int height) {
		this.mMaleHeight = height;
	}

	public int getFemaleHeight() {
		return mFemaleHeight;
	}

	public void setFemaleHeight(int height) {
		this.mFemaleHeight = height;
	}

	public String getMaleSchoolName() {
		return mMaleSchoolName;
	}

	public void setMaleSchoolName(String schoolName) {
		this.mMaleSchoolName = schoolName;
	}

	public String getFemaleSchoolName() {
		return mFemaleSchoolName;
	}

	public void setFemaleSchoolName(String schoolName) {
		this.mFemaleSchoolName = schoolName;
	}

	public String getMarriedCity() {
		return mMarriedCity;
	}

	public void setMarriedCity(String city) {
		this.mMarriedCity = city;
	}

	public String getMaleImageUrl() {
		return mMaleImageUrl;
	}

	public void setMaleImageUrl(String url) {
		this.mMaleImageUrl = url;
	}

	public String getFemaleImageUrl() {
		return mFemaleImageUrl;
	}

	public void setFemaleImageUrl(String url) {
		this.mFemaleImageUrl = url;
	}

	public int getBlessingCount() {
		return mBlessingCount;
	}
	public void setBlessingCount(int count) {
		this.mBlessingCount = count;
	}
	
	public String getLoveVow() {
		return mLoveVow;
	}
	public void setLoveVow(String vow) {
		this.mLoveVow = vow;
	}
	
	public long getMarriedDate() {
		return mMarriedDate;
	}
	public void setMarriedDate(long date) {
		this.mMarriedDate = date;
	}
	
	public long getRegisterDate() {
		return mRegisterDate;
	}
	public void setRegisterDate(long date) {
		this.mRegisterDate = date;
	}
	
	public long getMeetingDate() {
		return mMeetingDate;
	}
	public void setMeetingDate(long date) {
		this.mMeetingDate = date;
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
	/**
	* @Title: parse
	* @Description: 解析情侣知天使对象
	* @param jsonObject
	* @return
	* @throws JSONException 
	*/
	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);
		if (!jsonObject.isNull("maleCityName")) {
			mMaleCityName = StringHelper.jsonNullToEmpty(jsonObject.getString("maleCityName"));
		}
		if (!jsonObject.isNull("femaleCityName")) {
			mFemaleCityName = StringHelper.jsonNullToEmpty(jsonObject.getString("femaleCityName"));
		}
		if (!jsonObject.isNull("malebloodType")) {
			mMaleBloodType = StringHelper.jsonNullToEmpty(jsonObject.getString("malebloodType"));
		}
		if (!jsonObject.isNull("femalebloodType")) {
			mFemaleBloodType = StringHelper.jsonNullToEmpty(jsonObject.getString("femalebloodType"));
		}
		if (!jsonObject.isNull("maleDateOfBirth")) {
			mMaleBirthday = jsonObject.getLong("maleDateOfBirth");
		}
		if (!jsonObject.isNull("femaleDateOfBirth")) {
			mFemaleBirthday = jsonObject.getLong("femaleDateOfBirth");
		}		
		if (!jsonObject.isNull("maleConstallation")) {
			mMaleAstro = StringHelper.jsonNullToEmpty(jsonObject.getString("maleConstallation"));
		}
		if (!jsonObject.isNull("femaleConstallation")) {
			mFemaleAstro = StringHelper.jsonNullToEmpty(jsonObject.getString("femaleConstallation"));
		}
		if (!jsonObject.isNull("maleHeight")) {
			mMaleHeight = jsonObject.getInt("maleHeight");
		}
		if (!jsonObject.isNull("femaleHeight")) {
			mFemaleHeight = jsonObject.getInt("femaleHeight");
		}
		if (!jsonObject.isNull("maleSchoolName")) {
			mMaleSchoolName = StringHelper.jsonNullToEmpty(jsonObject.getString("maleSchoolName"));
		}
		if (!jsonObject.isNull("femaleSchoolName")) {
			mFemaleSchoolName = StringHelper.jsonNullToEmpty(jsonObject.getString("femaleSchoolName"));
		}
		if (!jsonObject.isNull("maleImageUrl")) {
			mMaleImageUrl = StringHelper.jsonNullToEmpty(jsonObject.getString("maleImageUrl"));
		}
		if (!jsonObject.isNull("femaleImageUrl")) {
			mFemaleImageUrl = StringHelper.jsonNullToEmpty(jsonObject.getString("femaleImageUrl"));
		}

		if (!jsonObject.isNull("signature")) {
			mLoveVow = StringHelper.jsonNullToEmpty(jsonObject.getString("signature"));
		}
		if (!jsonObject.isNull("marriedDate")) {
			mMarriedDate = jsonObject.getLong("marriedDate");
		}
		if (!jsonObject.isNull("cardDate")) {
			mRegisterDate = jsonObject.getLong("cardDate");
		}
		if (!jsonObject.isNull("meetingDate")) {
			mMeetingDate = jsonObject.getLong("meetingDate");
		}
		if (!jsonObject.isNull("privacyCode")) {
			mIsPublic = jsonObject.getInt("privacyCode") == 2;
		}
		if (!jsonObject.isNull("replyCode")) {
			mAllowComment = jsonObject.getInt("replyCode") == 2;
		}	
	}
}
