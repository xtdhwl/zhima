/* 
 * @Title: IdolJob.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: IdolJob
 * @Description: 知天使职业
 * @author liubingsr
 * @date 2012-6-11 上午11:37:03
 * 
 */
public final class IdolJob extends BaseData {
	private final static String TAG = "IdolJob";
	/**
	 *  id
	 */
	private long mId = 0;
	/**
	 * 职业名称
	 */
	private String mJob = "";

	public IdolJob() {
		mId = 0;
		mJob = "";
	}
	
	@Override
	public long getId() {
		return mId;
	}
	public void setId(long id) {
		this.mId = id;
	}
	
	public String getJob() {
		return mJob;
	}
	public void setJob(String job) {
		this.mJob = job;
	}
	/**
	* @Title: parse
	* @Description: 由json数据包解析出对象
	* @param jsonObject
	* @return null 解析失败
	*/
	public static IdolJob parse(JSONObject jsonObject) {
		IdolJob obj = new IdolJob();
		try {
			obj.setId(jsonObject.getLong("id"));
			if (!jsonObject.isNull("job")) {
				obj.setJob(jsonObject.getString("job"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
