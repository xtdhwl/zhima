/* 
 * @Title: Card.java
 * Created by liubingsr on 2012-5-31 下午4:58:20 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

/**
 * @ClassName: Card
 * @Description: 优惠券
 * @author liubingsr
 * @date 2012-5-31 下午4:58:20
 * 
 */
public abstract class Card extends Promotion {	
	private final static String TAG = "Card";

	/**
	 * 优惠券id
	 */
	protected long mCardId;
	/**
	 * 兑现密码
	 */
	protected String mPassword;
	
	@Override
	public long getId() {
		return mCardId;
	}
	public void setId(long id) {
		this.mCardId = id;
	}
	
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String password) {
		this.mPassword = password;
	}
}
