/* 
* @Title: SpaceQueryResult.java
* Created by liubingsr on 2012-9-22 上午11:47:50 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: SpaceQueryResult
 * @Description: 搜索结果
 * @author liubingsr
 * @date 2012-9-22 上午11:47:50
 *
 */
public class SpaceQueryResult extends BaseData {
	private final static String TAG = "SpaceQueryResult";
	private long mId = 0;
	/**
	 * 名称
	 */
	private String mName = "";
	/**
	 * 图片URL
	 */ 
	private String mImageUrl = "";
	/**
	 * 地址，仅适用于空间信息 
	 */
	private String mAddress = "";
	/**
	 * 电话号码，仅适用于空间信息
	 */
	private String mTelephone = "";
	/**
	 * 空间类型
	 */
	private String mKindName = "";
	/**
	 * 距离（单位米，目前仅适用于检索结果，搜索暂不考虑该字段） 
	 */
	private int mDistance = 0;
	/**
	 * 创建（或注册）时间
	 */
	private long mCreatedOn = System.currentTimeMillis();
	/**
	 * 目标类型：REST接口设计:TargetType
	 */
	private int mTargetType = 0;
	/**
	 * 目标ID
	 */
    private long mTargetId = 0;
    /**
     * 该空间被赞的次数（本属性适用于广场热度推荐）
     */
    private int mPraiseCount = 0;
    /**
     * 芝麻Id
     */
    private String mZMId = "";
    /**
     * 年龄
     */
    private int mAge = 0;

	@Override
	public long getId() {
    	String str = String.valueOf(mTargetId) + String.valueOf(mTargetType);
		long id = Long.parseLong(str);
		return id;
	}
//    public void setId(long id) {
//		this.mId = id;
//	}
    
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}
	
	public String getAddress() {
		return mAddress;
	}
	public void setAddress(String address) {
		this.mAddress = address;
	}
	
	public String getTelephone() {
		return mTelephone;
	}
	public void setTelephone(String telephone) {
		this.mTelephone = telephone;
	}
	
	public String getKindName() {
		return mKindName;
	}
	public void setKindName(String kindName) {
		this.mKindName = kindName;
	}
	
	public int getDistance() {
		return mDistance;
	}
	public void setDistance(int distance) {
		this.mDistance = distance;
	}
	
	public long getCreatedOn() {
		return mCreatedOn;
	}
	public void setCreatedOn(long createdOn) {
		this.mCreatedOn = createdOn;
	}
	
	public int getTargetType() {
		return mTargetType;
	}
	public void setTargetType(int targetType) {
		this.mTargetType = targetType;
	}
	
	public long getTargetId() {
		return mTargetId;
	}
	public void setTargetId(long targetId) {
		this.mTargetId = targetId;
	}
	
	public int getPraiseCount() {
		return mPraiseCount;
	}
	public void setPraiseCount(int count) {
		this.mPraiseCount = count;
	}
	
	public String getZMId() {
		return mZMId;
	}
	public void setZMId(String zmid) {
		this.mZMId = zmid;
	}
	
	public int getAge() {
		return mAge;
	}
	public void setAge(int age) {
		this.mAge = age;
	}
	
	/**
	* @Title: parse
	* @Description: json解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static SpaceQueryResult parse(JSONObject jsonObject) {
		SpaceQueryResult obj = new SpaceQueryResult();
		try {
//			obj.setId(UniqueIdGenerator.getInstance().genericId());
			obj.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			if (!jsonObject.isNull("imageUrl")) {
				obj.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}			
			if (!jsonObject.isNull("address")) {
				obj.setAddress(StringHelper.jsonNullToEmpty(jsonObject.getString("address")));
			}
			if (!jsonObject.isNull("telephone")) {
				obj.setTelephone(StringHelper.jsonNullToEmpty(jsonObject.getString("telephone")));
			}
			if (!jsonObject.isNull("kind")) {
				obj.setKindName(StringHelper.jsonNullToEmpty(jsonObject.getJSONObject("kind").getString("name")));
			}
			obj.setDistance(jsonObject.getInt("distance"));
			if (!jsonObject.isNull("createdOn")) {
				obj.setCreatedOn(jsonObject.getLong("createdOn"));
			}
			if (!jsonObject.isNull("targetType")) {
				obj.setTargetType(jsonObject.getInt("targetType"));
			}
			if (!jsonObject.isNull("targetId")) {
				obj.setTargetId(jsonObject.getLong("targetId"));
			}
			if (!jsonObject.isNull("praiseCount")) {
				obj.setPraiseCount(jsonObject.getInt("praiseCount"));
			}
			if (!jsonObject.isNull("zmid")) {
				obj.setZMId(StringHelper.jsonNullToEmpty(jsonObject.getString("zmid")));
			}
			if (!jsonObject.isNull("age")) {
				obj.setAge(jsonObject.getInt("age"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}	
}
