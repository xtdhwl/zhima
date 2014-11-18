/* 
* @Title: ZMPair.java
* Created by liubingsr on 2012-9-26 下午4:25:49 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

/**
 * @ClassName: ZMPair
 * @Description: pair类
 * @author liubingsr
 * @date 2012-9-26 下午4:25:49
 *
 */
public class ZMPair<T1, T2> {
	private T1 mFirst;
	private T2 mSecond;
	
	public ZMPair() {
	}
	
	public ZMPair(T1 first, T2 second) {
		mFirst = first;
		mSecond = second;
	}	

	public T1 getFirst() {
		return mFirst;
	}
	public void setFirst(T1 first) {
		this.mFirst = first;
	}
	
	public T2 getSecond() {
		return mSecond;
	}
	public void setSecond(T2 second) {
		this.mSecond = second;
	}
}
