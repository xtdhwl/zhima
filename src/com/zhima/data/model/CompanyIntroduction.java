/* 
 * @Title: CompanyIntroduction.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
* @ClassName: CompanyIntroduction
* @Description: 商家介绍
* @author liubingsr
* @date 2012-9-17 上午11:58:21
*
*/
public class CompanyIntroduction extends BaseData {
	private final static String TAG = "CompanyIntroduction";
	/**
	 * 公司Id
	 */
	private long mCompanyId;
	/**
	 * 公司名称
	 */
	private String mCompanyName;
	/**
	 * 公司描述
	 */
	private String mDescription;
	/**
	 * 图片地址
	 */
	private String mImageUrl;
	/**
	 * 营业时间
	 */
	private String mBusinessHours;
	
	@Override
	public long getId() {
		return mCompanyId;
	}
	
	public long getCompanyId() {
		return mCompanyId;
	}
	public void setCompanyId(long id) {
		mCompanyId = id;
	}
	
	public String getCompanyName() {
		return mCompanyName;
	}
	public void setCompanyName(String companyName) {
		this.mCompanyName = companyName;
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

	public String getBusinessHours() {
		return mBusinessHours;
	}
	public void setBusinessHours(String businessHours) {
		this.mBusinessHours = businessHours;
	}
	
	/**
	* @Title: parse
	* @Description: 由json数据包解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static CompanyIntroduction parse(JSONObject jsonObject) {
		CompanyIntroduction company = new CompanyIntroduction();
		try {
			company.setCompanyId(jsonObject.getLong("id"));
			company.setCompanyName(StringHelper.jsonNullToEmpty(jsonObject.getString("companyName")));
			company.setDescription(StringHelper.jsonNullToEmpty(jsonObject.getString("description")));
			company.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			company.setBusinessHours(StringHelper.jsonNullToEmpty(jsonObject.getString("businessHours")));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return company;		
	}	
}
