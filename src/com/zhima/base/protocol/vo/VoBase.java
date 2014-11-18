/* 
 * @Title: VoBase.java
 * Created by liubingsr on 2012-6-28 下午1:47:30 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

/**
 * @ClassName: VoBase
 * @Description: 服务器返回数据包
 * @author liubingsr
 * @date 2012-6-28 下午1:47:30
 * 
 */
public class VoBase {
	/**
	 * 处理状态
	 */
	private int status;
	/**
	 * 状态对应的文本信息
	 */
	private String message;
	/**
	 * 数据包类型
	 */
	private long kind;
	/**
	 * 时间戳
	 */
	private long timestamp;

	/**
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            要设置的 status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            要设置的 message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return kind
	 */
	public long getKind() {
		return kind;
	}

	/**
	 * @param kind
	 *            要设置的 kind
	 */
	public void setKind(long kind) {
		this.kind = kind;
	}

	/**
	 * @return timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            要设置的 timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
