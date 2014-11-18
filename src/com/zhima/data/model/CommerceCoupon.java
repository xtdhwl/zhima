/* 
 * @Title: CommerceCoupon.java
 * Created by liubingsr on 2012-5-31 下午4:58:20 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: CommerceCoupon
 * @Description: 商家优惠券
 * @author liubingsr
 * @date 2012-5-31 下午4:58:20
 * 
 */
public class CommerceCoupon extends Card {	
	private final static String TAG = "CommerceCoupon";
	
	/**
	 * 优惠活动id
	 */
	private long mPromotionId;
	/**
	 * 剩余张数
	 */
	private int mRemainCount;
	
	public long getPromotionId() {
		return mPromotionId;
	}
	public void setPromotionId(long promotionId) {
		this.mPromotionId = promotionId;
	}
	
	public int getRemainCount() {
		return mRemainCount;
	}
	public void setRemainCount(int remainCount) {
		this.mRemainCount = remainCount;
	}
	
	
	/**
	* @Title: parse
	* @Description: 解析json生成对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static CommerceCoupon parse(JSONObject jsonObject) {
		CommerceCoupon coupon = new CommerceCoupon();
		try {
			coupon.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("activityId")) {
				coupon.setPromotionId(jsonObject.getLong("activityId"));
			}
			coupon.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			if (!jsonObject.isNull("description")) {
				coupon.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}
			if (!jsonObject.isNull("residueCount")) {
				coupon.setRemainCount(jsonObject.getInt("residueCount"));
			}
			coupon.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			coupon.setBeginTime(jsonObject.getLong("beginTime"));
			if (jsonObject.isNull("endTime")) {
				coupon.setDeadlineTime(Long.MAX_VALUE);
			} else {
				coupon.setDeadlineTime(jsonObject.getLong("endTime"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return coupon;
	}
}
