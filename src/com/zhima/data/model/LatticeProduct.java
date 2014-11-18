/* 
 * @Title: LatticeshopProduct.java
 * Created by liubingsr on 2012-7-28 下午4:48:11 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: LatticeProduct
 * @Description: 格子铺物品
 * @author liubingsr
 * @date 2012-7-28 下午4:48:11
 * 
 */
public class LatticeProduct extends BaseData implements Serializable {
	
	/**
	* @Fields serialVersionUID : TODO(描述这个变量表示什么)
	*/
	private static final long serialVersionUID = 1396942873798847775L;
	private final static String TAG = "LatticeProduct";
	/**
	 * id
	 */
	private long mId = 0;
	/**
	 * 所有者用户
	 */
	private long mOwnerId = 0;
	/**
	 * 标题
	 */
	private String mTitle = "";
	/**
	 * 描述
	 */
	private String mDescription = "";
	/**
	 * 价格
	 */
	private String mPrice = "";
	/**
	 * 数量
	 */
	private int mAmount = 0;
	/**
	 * 图片
	 */
	private String mImageUrl = "";
	/**
	 * 交易方式描述
	 */
	private String mTradeMode = "";

	@Override
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public long getOwnerId() {
		return mOwnerId;
	}

	public void setOwnerId(long ownerId) {
		this.mOwnerId = ownerId;
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

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		this.mPrice = price;
	}

	public int getAmount() {
		return mAmount;
	}

	public void setAmount(int amount) {
		this.mAmount = amount;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	public String getTradeMode() {
		return mTradeMode;
	}

	public void setTradeMode(String tradeMode) {
		this.mTradeMode = tradeMode;
	}

	/**
	 * @Title: parse
	 * @Description: 解析出对象
	 * @param jsonObject
	 * @return null 解析错误
	 */
	public static LatticeProduct parse(JSONObject jsonObject) {
		LatticeProduct product = new LatticeProduct();
		try {
			product.setId(jsonObject.getLong("id"));
			product.setTitle(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			String price = "";
			if (!jsonObject.isNull("price")) {
				double data = jsonObject.getDouble("price");
				price = String.format("%.2f", data);
			}
			product.setPrice(price);
			if (!jsonObject.isNull("userId")) {
				product.setOwnerId(jsonObject.getLong("userId"));
			}
			product.setAmount(jsonObject.getInt("count"));
			product.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			product.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			product.setTradeMode(StringHelper.jsonNullToEmpty(jsonObject.getString("dealType")));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return product;
	}
}
