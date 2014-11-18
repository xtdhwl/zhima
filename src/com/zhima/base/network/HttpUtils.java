/* 
 * @Title: HttpUtils.java
 * Created by liubingsr on 2012-5-17 下午5:04:44 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network;

import android.text.TextUtils;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: HttpUtils
 * @Description: http工具类
 * @author liubingsr
 * @date 2012-5-17 下午5:04:44
 * 
 */
public class HttpUtils {
	private final static String TAG = "HttpUtils";

	public static String urlEncode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		if (data != null)
			for (int i = 0; i < data.length; i++) {
				char ch = (char) (data[i] & 0xff);
				if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')
						|| (ch >= 'A' && ch <= 'Z')) {
					sb.append(ch);
					continue;
				}
				if (ch == ' ') {
					sb.append("+");
					continue;
				}
				sb.append("%");
				String value = Integer.toHexString(ch);
				if (value.length() == 1) {
					sb.append("0");
				}
				sb.append(value);
			}
		return sb.toString();
	}

	public static String urlEncode(String str, String enc) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		try {
			return urlEncode(str.getBytes(enc));//java.net.URLEncoder.encode(str, "utf-8")
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("urlEncode:" + e.getMessage());
			return str;
		}
	}
}
