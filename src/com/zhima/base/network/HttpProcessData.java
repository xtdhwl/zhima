/* 
* @Title: HttpProcessData.java
* Created by liubingsr on 2012-5-14 下午5:42:12 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.network;

/**
 * @ClassName: HttpProcessData
 * @Description: 进度信息
 * @author liubingsr
 * @date 2012-5-14 下午5:42:12
 *
 */
public class HttpProcessData {
	private int mPercentage = 0;
	
	public HttpProcessData(int percentage) {
        mPercentage = percentage;
    }
	
    /**
	 * @return 进度百分比
	 */
	public int getPercentage() {
		return mPercentage;
	}

	/**
	 * @param percentage 要设置的 mPercentage
	 */
	public void setPercentage(int percentage) {
		this.mPercentage = percentage;
	}	
}
