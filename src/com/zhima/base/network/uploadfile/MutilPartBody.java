/* 
 * @Title: MutilPartBody.java
 * Created by liubingsr on 2012-5-16 下午8:00:55 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

import java.io.DataOutputStream;

/**
 * @ClassName: MutilPartBody
 * @Description: body基类
 * @author liubingsr
 * @date 2012-5-16 下午8:00:55
 * 
 */
public abstract class MutilPartBody {
	// 字节数
	protected int mLength;
	// 数据
	protected byte[] mBytes;

	public byte[] getBytes() {
		return mBytes;
	}

	public int getLength() {
		return mLength;
	}
	/**
	* @Title: write
	* @Description: 将字节数据写入输出流
	* @param os
	* @throws FileUploadException
	* void
	*/
	public abstract void write(DataOutputStream os) throws FileUploadException;
}
