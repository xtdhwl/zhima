/* 
 * @Title: ZMProductObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.utils.StringHelper;

/**
 * @ClassName: ZMProductObject
 * @Description: 3000种商品产品
 * @author liubingsr
 * @date 2012-5-17 下午5:33:19
 * 
 */
public class ZMProductObject extends ZMObject {
	private final static String TAG = "ZMProductObject";
	/**
	 * 商品码
	 */
	private String mBarcode = "";
	/**
	 * 参考价格
	 */
	private String mPrice = "";
	/**
	 * 保质期
	 */
	private String mShelfLife = "";
	/**
	 * 电话
	 */
	private String mPhone = "";
	/**
	 * 网站地址
	 */
	private String mWebsite = "";

	/**
	 * 创建时间
	 */
	private long mCreateOn = 0;

	/**
	 * 更新时间
	 */
	private long mUpdateOn = 0;

	/**
	 * 商品单位
	 */
	private String mUnit = "";

	/**
	 * 商品备注
	 */
	private String mRemark = "";

	/**
	 * 产品类型
	 */
	private String mProductType = "";

	/**
	 * 商品规格
	 */
	private String mStandard = "";

	/**
	 * 是否合格 ,true表示合格，false不合格
	 */
	private boolean mQualified;
	
	public ZMProductObject() {
		super();
		mZMObjectType = ZMObjectKind.ZMPRODUCT_OBJECT;
	}

	public String getRemark() {
		return mRemark;
	}

	public void setRemark(String remark) {
		this.mRemark = remark;
	}

	public long getCreateOn() {
		return mCreateOn;
	}

	public void setCreateOn(long createOn) {
		this.mCreateOn = createOn;
	}

	public long getUpdateOn() {
		return mUpdateOn;
	}

	public void setUpdateOn(long updateOn) {
		this.mUpdateOn = updateOn;
	}

	public String getUnit() {
		return mUnit;
	}

	public void setUnit(String unit) {
		this.mUnit = unit;
	}

	public boolean isQualified() {
		return mQualified;
	}

	public void setQualified(boolean qualified) {
		this.mQualified = qualified;
	}

	public String getStandard() {
		return mStandard;
	}

	public void setStandard(String standard) {
		this.mStandard = standard;
	}

	public String getBarcode() {
		return mBarcode;
	}

	public void setBarcode(String barcode) {
		this.mBarcode = barcode;
	}

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		this.mPrice = price;
	}

	public String getShelfLife() {
		return mShelfLife;
	}

	public void setShelfLife(String shelfLife) {
		this.mShelfLife = shelfLife;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	public String getWebsite() {
		return mWebsite;
	}

	public void setWebsite(String website) {
		this.mWebsite = website;
	}

	public String getProductType() {
		return mProductType;
	}

	public void setProductType(String productType) {
		this.mProductType = productType;
	}

	/**
	 * @Title: parse
	 * @Description: 由json数据包解析出ZMProductObject
	 * @param jsonObject
	 * @throws JSONException 
	 */
	@Override
	public void parse(JSONObject jsonObject) throws JSONException {
		super.parse(jsonObject);		
		if (!jsonObject.isNull("barcode")) {
			mBarcode = StringHelper.jsonNullToEmpty(jsonObject.getString("barcode"));
			mZMCode = mBarcode;
		}		
		if (!jsonObject.isNull("price")) {
			double price = jsonObject.getDouble("price");
			if (price <= 0.0) {
				mPrice = "未标价";
			} else {
				mPrice = String.format("%.2f", price) + "元";
			}
		} else {
			mPrice = "未标价";
		}

		mShelfLife = StringHelper.jsonNullToEmpty(jsonObject.getString("shelfLife"));
		mPhone = StringHelper.jsonNullToEmpty(jsonObject.getString("telephone"));
		mWebsite = StringHelper.jsonNullToEmpty(jsonObject.getString("website"));

		if (!jsonObject.isNull("createOn")) {
			mCreateOn = jsonObject.getLong("createOn");
		}
		if (!jsonObject.isNull("updatedOn")) {
			mUpdateOn = jsonObject.getLong("updatedOn");
		}

		mRemark = StringHelper.jsonNullToEmpty(jsonObject.getString("remark"));
		mUnit = StringHelper.jsonNullToEmpty(jsonObject.getString("unit"));
		mStandard = StringHelper.jsonNullToEmpty(jsonObject.getString("standard"));

		String standard = StringHelper.jsonNullToEmpty(jsonObject.getString("standard"));
		if (TextUtils.isEmpty(standard)) {
			mStandard = "暂无";
		} else {
			mStandard = standard;
		}
		mQualified = jsonObject.getBoolean("qualified");
	}
}
