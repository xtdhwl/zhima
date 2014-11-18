/* 
* @Title: CommerceProduct.java
* Created by liubingsr on 2012-7-9 上午11:12:42 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: CommerceProduct
 * @Description: 商户商品信息
 * @author liubingsr
 * @date 2012-7-9 上午11:12:42
 *
 */
public class CommerceProduct extends BaseData {
	private final static String TAG = "CommerceProduct";
	/**
	 * 商品id
	 */
	private long mProductId = 0;
	/**
	 * 所属的商户id
	 */
	private long mCommerceId = 0;
	/**
	 * 商品名称
	 */
	private String mName = "";
	/**
	 * 价格
	 */
	private String mPrice = "";
	/**
	 * 详细
	 */
	private String mDescription = "";
	/**
	 * 商品类型
	 */
	private String mProductType = "";
	/**
	 * 图片
	 */
	private String mImageUrl = "";
	
	@Override
	public long getId() {
		return mProductId;
	}

	public long getProductId() {
		return mProductId;
	}
	public void setProductId(long productId) {
		this.mProductId = productId;
	}
	public long getCommerceId() {
		return mCommerceId;
	}
	public void setCommerceId(long commerceId) {
		this.mCommerceId = commerceId;
	}

	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getPrice() {
		return mPrice;
	}
	public void setPrice(String price) {
		this.mPrice = price;
	}
	
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		this.mDescription = description;
	}

	public String getProductType() {
		return mProductType;
	}
	public void setProductType(String productType) {
		this.mProductType = productType;
	}

	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	/**
	* @Title: parse
	* @Description: 解析json生成对象
	* @param jsonObject
	* @return
	* CommerceProduct
	*/
	public static CommerceProduct parse(JSONObject jsonObject) {
		CommerceProduct product = new CommerceProduct();
		try {
			product.setProductId(jsonObject.getLong("id"));
			product.setName(StringHelper.jsonNullToEmpty(jsonObject.getString("name")));
			if (!jsonObject.isNull("price")) {
				double price = jsonObject.getDouble("price");
				if (price <= 0.0) {
					product.setPrice("暂无价格");
				} else {
					product.setPrice(String.format("%.2f", price) + "元");
				}
			} else {
				product.setPrice("暂无价格");
			}
			//product.setPrice(StringHelper.jsonNullToEmpty(jsonObject.getString("price")));
			if (!jsonObject.isNull("description")) {
				product.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			}			
			product.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			if (!jsonObject.isNull("kind")) {
				product.setProductType(StringHelper.jsonNullToEmpty(jsonObject.getJSONObject("kind").getString("name")));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return product;
	}
}
