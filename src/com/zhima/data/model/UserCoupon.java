/* 
 * @Title: UserCoupon.java
 * Created by liubingsr on 2012-5-31 下午4:58:20 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: UserCoupon
 * @Description: 用户领取的优惠券
 * @author liubingsr
 * @date 2012-5-31 下午4:58:20
 * 
 */
public class UserCoupon extends Card {
	private final static String TAG = "UserCoupon";
	private static final int OBJECT_STATUS_USED = 5;

	/**
	 * 商户id
	 */
	private long mCommerceId;

	/**
	 * 是否已经使用
	 */
	private boolean mUsed = false;
	/**
	 * 领取时间
	 */
	private long mReceiveTime = 0;
	/**
	 * 使用时间
	 */
	private long mUseTime = 0;
	/**
	 * 对应空间类型名称："business" | "public" | "vehicle" | "idol" | "product"
	 */
	private String mSpaceKind;
	/**
	 * 对应空间ID
	 */
	private long mSpaceId;
	/**
	 * 对应空间名称
	 */
	private String mSpaceName;

	public String getSpaceKind() {
		return mSpaceKind;
	}

	public void setSpaceKind(String spaceKind) {
		this.mSpaceKind = spaceKind;
	}

	public long getSpaceId() {
		return mSpaceId;
	}

	public void setSpaceId(long spaceId) {
		this.mSpaceId = spaceId;
	}

	public String getSpaceName() {
		return mSpaceName;
	}

	public void setSpaceName(String spaceName) {
		this.mSpaceName = spaceName;
	}

	public long getCommerceId() {
		return mCommerceId;
	}

	public void setCommerceId(long commerceId) {
		this.mCommerceId = commerceId;
	}

	public boolean getUsed() {
		return mUsed;
	}

	public void setUsed(boolean used) {
		this.mUsed = used;
	}

	public long getReceiveTime() {
		return mReceiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.mReceiveTime = receiveTime;
	}

	public long getUseTime() {
		return mUseTime;
	}

	public void setUseTime(long useTime) {
		this.mUseTime = useTime;
	}

	/**
	 * @Title: parse
	 * @Description: 解析json生成对象
	 * @param jsonObject
	 * @return null 解析失败
	 */
	public static UserCoupon parse(JSONObject jsonObject) {
		UserCoupon coupon = new UserCoupon();
		try {
			coupon.setId(jsonObject.getLong("id"));
			coupon.setActionId(coupon.getId());
			coupon.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			if (!jsonObject.isNull("description")) {
				coupon.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}
			coupon.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			if (!jsonObject.isNull("statusCode")) {
				coupon.setUsed(jsonObject.getInt("statusCode") == OBJECT_STATUS_USED);
			}
			if (jsonObject.isNull("beginTime")) {
				coupon.setBeginTime(System.currentTimeMillis());
			} else {
				coupon.setBeginTime(jsonObject.getLong("beginTime"));
			}
			if (jsonObject.isNull("endTime")) {
				coupon.setDeadlineTime(Long.MAX_VALUE);
			} else {
				coupon.setDeadlineTime(jsonObject.getLong("endTime"));
			}
			if (!jsonObject.isNull("receiveTime")) {
				coupon.setReceiveTime(jsonObject.getLong("receiveTime"));
			}
			if (!jsonObject.isNull("lastUseTime")) {
				coupon.setUseTime(jsonObject.getLong("lastUseTime"));
			}
			if (!jsonObject.isNull("mSpaceKind")) {
				coupon.setSpaceKind(StringHelper.jsonNullToEmpty(jsonObject.getString("mSpaceKind")));
			}
			if (!jsonObject.isNull("mSpaceName")) {
				coupon.setSpaceName(StringHelper.jsonNullToEmpty(jsonObject.getString("mSpaceName")));
			}
			if (!jsonObject.isNull("mSpaceId")) {
				coupon.setSpaceId(jsonObject.getLong("mSpaceId"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return coupon;
	}
}
