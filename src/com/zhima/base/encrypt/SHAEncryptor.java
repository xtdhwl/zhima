/* 
 * @Title: SHAEncryptor.java
 * Created by liubingsr on 2012-5-22 上午9:59:25 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: SHAEncryptor
 * @Description: sha加密
 * @author liubingsr
 * @date 2012-5-22 上午9:59:25
 * 
 */
public class SHAEncryptor {
	public final static String TAG = "SHAEncryptor";
	/**
	* @Title: getSHA
	* @Description: 对字符串进行SHA加密，返回加密后的字符串。如果失败返回原值
	* @param val
	* @return
	* String
	*/
	public static String getSHA(String val) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("SHA-1");
			md5.update(val.getBytes("iso-8859-1"), 0, val.length());
			// 加密
			byte[] buffer = md5.digest();
			return getString(buffer);
		} catch (NoSuchAlgorithmException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
		}
		return val;
	}

	private static String getString(byte[] buffer) {
		StringBuffer sb = new StringBuffer();
		for (int index = 0; index < buffer.length; ++index) {
			sb.append(buffer[index]);
		}
		return sb.toString();
	}
}
