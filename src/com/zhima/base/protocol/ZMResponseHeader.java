/* 
 * @Title: VoBase.java
 * Created by liubingsr on 2012-6-28 下午1:47:30 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: ZMResponseHeader
 * @Description: 服务端返回数据包头信息
 * @author liubingsr
 * @date 2012-7-3 下午2:15:30
 * 
 */
public class ZMResponseHeader {
	private final static String TAG = "ZMResponseHeader";
	/**
	 * 处理状态
	 */
	private int mStatus = ProtocolStatus.RESULT_ERROR;
	/**
	 * 状态对应的文本信息
	 */
	private String mMessage = "";
	/**
	 * 数据包类型
	 */
	private String mKind = "";
	/**
	 * 时间戳
	 */
	private long mTimestamp = 0;

	/**
	 * @return status
	 */
	public int getStatus() {
		return mStatus;
	}

	/**
	 * @param status
	 *            要设置的 status
	 */
	public void setStatus(int status) {
		this.mStatus = status;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * @param message
	 *            要设置的 message
	 */
	public void setMessage(String message) {
		this.mMessage = message;
	}

	/**
	 * @return kind
	 */
	public String getKind() {
		return mKind;
	}

	/**
	 * @param kind
	 *            要设置的 kind
	 */
	public void setKind(String kind) {
		this.mKind = kind;
	}

	/**
	 * @return mTimestamp
	 */
	public long getTimestamp() {
		return mTimestamp;
	}

	/**
	 * @param timestamp
	 *            要设置的 mTimestamp
	 */
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("status:" + mStatus + "\n");
		sb.append("message:" + mMessage + "\n");
		sb.append("kind:" + mKind + "\n");
		sb.append("timestamp:" + mTimestamp + "\n");
		return sb.toString();
	}

	/**
	 * @Title: parse
	 * @Description: response头
	 * @param jsonObject
	 * @return ZMResponseHeader
	 */
	public static ZMResponseHeader parse(JSONObject jsonObject) {
		ZMResponseHeader resp = new ZMResponseHeader();
		try {
			resp.setStatus(jsonObject.getInt("status"));
			if (!jsonObject.isNull("message")) {
				resp.setMessage(jsonObject.getString("message"));
			}
			if (!jsonObject.isNull("kind")) {
				resp.setKind(jsonObject.getString("kind"));
			}
			// TODO
			//resp.setTimestamp(jsonObject.getLong("timestamp"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return resp;
	}
}
