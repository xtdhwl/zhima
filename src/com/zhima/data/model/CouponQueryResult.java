/* 
* @Title: CouponQueryResult.java
* Created by liubingsr on 2012-9-22 上午11:47:50 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: CouponQueryResult
 * @Description: 优惠券搜索结果
 * @author liubingsr
 * @date 2012-9-22 上午11:47:50
 *
 */
public class CouponQueryResult extends BaseData {
	private final static String TAG = "CouponQueryResult";
	/**
	 * 优惠券id
	 */
	private long mId = 0;
	/**
	 * 名称
	 */
	private String mName = "";
	/**
	 * 剩余张数
	 */
	private int mRemainCount = -1;
	/**
	 * 图片URL
	 */
	private String mImageUrl = "";
	/**
	 * 活动详细描述
	 */
	private String mDescription = "";
	/**
	 * 开始时间
	 */
	protected long mBeginTime = System.currentTimeMillis();
	/**
	 * 截止时间。如果值是：Long.MAX_VALUE，表示没有结束时间
	 */
	protected long mDeadlineTime;
	/**
	 * 创建（或注册）时间
	 */
	private long mCreatedOn = System.currentTimeMillis();
	/**
	 * 目标名字
	 */
	private String mTargetName = "";
	/**
	 * 目标类型：REST接口设计:TargetType
	 */
	private int mTargetType = 0;
	/**
	 * 目标ID
	 */
    private long mTargetId = 0;
    /**
	 * 距离（单位米，目前仅适用于检索结果，搜索暂不考虑该字段） 
	 */
	private int mDistance = 0;
    
    @Override
	public long getId() {
		return mId;
	}
    public void setId(long id) {
		this.mId = id;
	}
    
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
	
	public int getRemainCount() {
		return mRemainCount;
	}
	/**
	* @Title: setRemainCount
	* @Description: TODO(描述这个方法的作用)
	* @param int1
	* void
	*/
	private void setRemainCount(int count) {
		// TODO Auto-generated method stub
		this.mRemainCount = count;
	}
	
	public String getDescription() {
		return mDescription;
	}
	/**
	* @Title: setDescription
	* @Description: TODO(描述这个方法的作用)
	* @param jsonNullToEmpty
	* void
	*/
	private void setDescription(String description) {
		this.mDescription = description;	
	}
	
	public long getDeadlineTime() {
		return mDeadlineTime;
	}
	/**
	* @Title: setDeadlineTime
	* @Description: TODO(描述这个方法的作用)
	* @param maxValue
	* void
	*/
	private void setDeadlineTime(long time) {
		// TODO Auto-generated method stub
		this.mDeadlineTime = time;
	}
	
	public long getBeginTime() {
		return mBeginTime;
	}
	/**
	* @Title: setBeginTime
	* @Description: TODO(描述这个方法的作用)
	* @param long1
	* void
	*/
	private void setBeginTime(long time) {
		// TODO Auto-generated method stub
		this.mBeginTime = time;
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
	
	public long getTtargetId() {
		return mTargetId;
	}
	public void setTargetId(long targetId) {
		this.mTargetId = targetId;
	}
	
	public String getTargetName() {
		return mTargetName;
	}
	public void setTargetName(String targetName) {
		this.mTargetName = targetName;
	}
	
	public int getTargetType() {
		return mTargetType;
	}
	public void setTargetType(int targetType) {
		this.mTargetType = targetType;
	}
	
	/**
	* @Title: parse
	* @Description: json解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static CouponQueryResult parse(JSONObject jsonObject) {
		CouponQueryResult obj = new CouponQueryResult();
		try {
			obj.setId(jsonObject.getLong("id"));
			obj.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			if (!jsonObject.isNull("imageUrl")) {
				obj.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			}			
			if (!jsonObject.isNull("description")) {
				obj.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}
			if (!jsonObject.isNull("residueCount")) {
				obj.setRemainCount(jsonObject.getInt("residueCount"));
			}
			obj.setBeginTime(jsonObject.getLong("beginTime"));
			if (jsonObject.isNull("endTime")) {
				obj.setDeadlineTime(Long.MAX_VALUE);
			} else {
				obj.setDeadlineTime(jsonObject.getLong("endTime"));
			}			
			if (!jsonObject.isNull("createdOn")) {
				obj.setCreatedOn(jsonObject.getLong("createdOn"));
			}			
			if (!jsonObject.isNull("targetId")) {
				obj.setTargetId(jsonObject.getLong("targetId"));
			}
			if (!jsonObject.isNull("targetType")) {
				obj.setTargetType(jsonObject.getInt("targetType"));
			}
			if (!jsonObject.isNull("targetName")) {
				obj.setTargetName(StringHelper.jsonNullToEmpty(jsonObject.getString("targetName")));
			}
			if (!jsonObject.isNull("distance")) {
				obj.setDistance(jsonObject.getInt("distance"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}		
}
