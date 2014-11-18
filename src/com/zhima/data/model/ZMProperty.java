/* 
* @Title: ZMProperty.java
* Created by liubingsr on 2012-7-23 下午5:13:21 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

/**
 * @ClassName: ZMProperty
 * @Description: 属性对象
 * @author liubingsr
 * @date 2012-7-23 下午5:13:21
 *
 */
public final class ZMProperty<T> {
	private String mName;
	private T mValue;
	
	public ZMProperty(String name, T value) {
		mName = name;
		mValue = value;
	}

	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}

	public T getValue() {
		return mValue;
	}
	public void setValue(T value) {
		this.mValue = value;
	}	
}
