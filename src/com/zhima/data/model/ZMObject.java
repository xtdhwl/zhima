/* 
 * @Title: ZMObject.java
 * Created by liubingsr on 2012-5-25 下午12:01:58 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMObject
 * @Description: 知码对象。任何可以贴上码的事物(人、物、商号、花草、树木、抽象概念)
 * @author liubingsr
 * @date 2012-6-7 下午2:21:06
 * 
 */
public/* abstract */class ZMObject extends BaseData {
	private final static String TAG = "ZMObject";
	/**
	 * 标识(因为服务器端返回的id不唯一，必须将mZMObjectId与mZMObjectType组合起来才能保证唯一性)
	 */
	protected long mId;
	/**
	 * mRemoteId(服务器端返回的id值)
	 */
	protected long mRemoteId;
	/**
	 * 对象类型
	 * 
	 * @see com.zhima.base.consts.ZMConsts.ZMObjectKind
	 */
	protected int mZMObjectType = ZMObjectKind.UNKNOWN_OBJECT;
	/**
	 * 名称
	 */
	protected String mName;	
	/**
	 * zmcode
	 */
	protected String mZMCode = "";
	/**
	 * zmid。每个对象的身份标识，类似qq号码
	 */
	protected String mZMID = "";
	/**
	 * 空间所有者
	 */
	protected User mUser;
	/**
	 * 码的原始值
	 */
	protected String mSpaceHomepage;
	/**
	 * 类型
	 */
	protected String mType = "";
	/**
	 * 标题
	 */
	protected String mTitle = "";
	/**
	 * 简要描述
	 */
	protected String mDescription = "";
	/**
	 * 图片url
	 */
	protected String mImageUrl = "";
//	/**
//	 * 纬度
//	 */
//	protected double mLatitude;
//	/**
//	 * 经度
//	 */
//	protected double mLongitude;
	/**
	 * GPS坐标
	 */
	protected GeoCoordinate mGeo;
	/**
	 * 城市id
	 */
	protected long mCityId = 0;
	/**
	 * 类型id
	 */
	protected long mKindId = 0;
	/**
	 * 空间类型
	 */
	protected ZMSpaceKind mSpaceKind = null;
	/**
	 * json数据包是否经过了压缩
	 */
	protected boolean mJsonCompressed;
	/**
	 * json数据包
	 */
	protected String mJson = "";
	/**
	 * 获取时间
	 */
	protected long mTimeStamp;

	public ZMObject() {
		mId = 0;
		mRemoteId = 0;
		mZMObjectType = ZMObjectKind.UNKNOWN_OBJECT;
		mZMCode = "";
		mZMID = "";
		mUser = null;
		mSpaceHomepage = "http://www.zhima.net/";
		mName = "";
		mTitle = "";
		mDescription = "";
		mImageUrl = "";
		mGeo = new GeoCoordinate();
		mCityId = 0;
		mTimeStamp = System.currentTimeMillis();
		mJsonCompressed = false;
		mJson = "";
	}

	/**
	 * 为了适应服务器端的要求，客户端用 mZMObjectId+mZMObjectType 作为键值。详情参见：mId属性的注释
	 */
	@Override
	public long getId() {
		return ZMObject.createLocalId(mRemoteId, mZMObjectType);
	}

	/**
	 * @Title: createLocalId
	 * @Description: 生成本地的id
	 * @param remoteId 服务器端的id
	 * @param zmObjectType 类型
	 * @return long
	 */
	public static long createLocalId(long remoteId, int zmObjectType) {
		String str = String.valueOf(Math.abs(remoteId)) + String.valueOf(Math.abs(zmObjectType));
		long id = Long.parseLong(str);
		return id;
	}

	public long getRemoteId() {
		return mRemoteId;
	}
	public void setRemoteId(long remoteId) {
		this.mRemoteId = remoteId;
	}
	
	public String getType() {
		return mType;
	}
	public void setType(String type) {
		this.mType = type;
	}

	public int getZMObjectType() {
		return mZMObjectType;
	}
	public void setZMObjectType(int objectType) {
		this.mZMObjectType = objectType;
	}

	public String getZMCode() {
		return mZMCode;
	}
	public void setZMCode(String zmCode) {
		this.mZMCode = zmCode;
	}
	
	public String getZMID() {
		return mZMID;
	}
	public void setZMID(String zmid) {
		this.mZMID = zmid;
	}
	
	public User getUser() {
		return mUser;
	}
	public void setUser(User user) {
		this.mUser = user;
	}
	
	public String getSpaceHomepage() {
		return mSpaceHomepage;
	}
	public void setSpaceHomepage(String spaceHomepage) {
		this.mSpaceHomepage = spaceHomepage;
	}

	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		this.mDescription = description;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	public GeoCoordinate getGeo() {
		return mGeo;
	}
	public void setGeo(GeoCoordinate geo) {
		this.mGeo = geo;
	}
	
	public long getCityId() {
		return mCityId;
	}
	public void setCityId(long cityId) {
		this.mCityId = cityId;
	}
	
	public long getKindId() {
		return mKindId;
	}
	public void setKindId(long id) {
		this.mKindId = id;
	}
	
	public ZMSpaceKind getSpaceKind() {
		return mSpaceKind;
	}
	public void setSpaceKind(ZMSpaceKind spaceKind) {
		this.mSpaceKind = spaceKind;
	}
	
	public long getTimeStamp() {
		return mTimeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.mTimeStamp = timeStamp;
	}

	public String getJson() {
		return mJson;
	}
	public void setJson(String json) {
		this.mJson = json;
	}

	public boolean getJsonCompressed() {
		return mJsonCompressed;
	}
	public void setJsonCompressed(boolean compressed) {
		this.mJsonCompressed = compressed;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("zmObjectId:" + mRemoteId);
		sb.append(",objectType:" + mZMObjectType);
		sb.append(",cityId:" + mCityId);
		sb.append(",zmCode:" + mZMCode);
		sb.append(",zmid:" + mZMID);
		sb.append(",title:" + mTitle);
		sb.append(",name:" + mName);
		sb.append(",description:" + mDescription);
		sb.append(",imageUrl:" + mImageUrl);
		sb.append(",geo:" + mGeo.toString());
		sb.append(",json:" + mJson);
		sb.append(",mJsonCompressed:" + mJsonCompressed);

		return sb.toString();
	}

	public void parse(JSONObject jsonObject) throws JSONException {
		if (!jsonObject.isNull("id")) {
			mRemoteId = jsonObject.getLong("id");
		}
		if (!jsonObject.isNull("zmCode")) {
			mZMCode = StringHelper.jsonNullToEmpty(jsonObject.getString("zmCode"));
		}
		if (!jsonObject.isNull("zmid")) {
			mZMID = StringHelper.jsonNullToEmpty(jsonObject.getString("zmid"));
		}
		if (!jsonObject.isNull("userInfo")) {
			mUser = User.parse(jsonObject.getJSONObject("userInfo"));
		}
		if (!jsonObject.isNull("spaceHomepage")) {
			mSpaceHomepage = StringHelper.jsonNullToEmpty(jsonObject.getString("spaceHomepage"));
		}
		if (!jsonObject.isNull("name")) {
			mName = StringHelper.jsonNullToEmpty(jsonObject.getString("name"));
		}
		if (!jsonObject.isNull("description")) {
			mDescription = StringHelper.jsonNullToEmpty(jsonObject.getString("description"));
		}
		if (!jsonObject.isNull("imageUrl")) {
			mImageUrl = StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl"));
		}			
		if (!jsonObject.isNull("cityId")) {
			mCityId = jsonObject.getLong("cityId");
		}
		if (!jsonObject.isNull("kindId")) {
			mKindId = jsonObject.getLong("kindId");
		}
		if (!jsonObject.isNull("kind")) {
			mType = StringHelper.jsonNullToEmpty(jsonObject.getJSONObject("kind").getString("name"));
		}
		if (!jsonObject.isNull("spaceType")) {
			mZMObjectType = jsonObject.getInt("spaceType");
		}		
		if (!jsonObject.isNull("geoCoord")) {
			mGeo = GeoCoordinate.parse(jsonObject.getJSONObject("geoCoord"));
		}
		if (!jsonObject.isNull("kind")) {
			mSpaceKind = ZMSpaceKind.parse(jsonObject.getJSONObject("kind"));
		}
	}
}
