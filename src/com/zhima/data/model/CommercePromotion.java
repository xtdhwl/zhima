/* 
 * @Title: CommercePromotion.java
 * Created by liubingsr on 2012-5-31 下午4:58:20 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: CommercePromotion
 * @Description: 商户促销活动
 * @author liubingsr
 * @date 2012-5-31 下午4:58:20
 * 
 */
public class CommercePromotion extends Promotion {
	private final static String TAG = "CommercePromotion";
	/**
	 * 商户id
	 */
	private long mCommerceId;
	/**
	 * 优惠券剩余张数
	 */
	private int mRemainCount;

	public long getCommerceId() {
		return mCommerceId;
	}
	public void setCommerceId(long commerceId) {
		this.mCommerceId = commerceId;
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
	* @return
	* CommercePromotion
	*/
	public static CommercePromotion parse(JSONObject jsonObject) {
		CommercePromotion promotion = new CommercePromotion();
		try {
			promotion.setActionId(jsonObject.getLong("id"));
			promotion.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			String description = StringHelper.jsonNullToEmpty(jsonObject.getString("description"));
			promotion.setDescription(description);
			promotion.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			if (jsonObject.isNull("beginTime")) {
				promotion.setBeginTime(System.currentTimeMillis());
			} else {
				promotion.setBeginTime(jsonObject.getLong("beginTime"));
			}			
			if (jsonObject.isNull("endTime")) {
				promotion.setDeadlineTime(Long.MAX_VALUE);
			} else {
				promotion.setDeadlineTime(jsonObject.getLong("endTime"));
			}
			if (!jsonObject.isNull("residueCount")) {
				promotion.setRemainCount(jsonObject.getInt("residueCount"));
			}
			if (!jsonObject.isNull("targetId")) {
				promotion.setCommerceId(jsonObject.getLong("targetId"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return promotion;
	}
}
