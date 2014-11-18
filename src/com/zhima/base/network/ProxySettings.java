/* 
* @Title: ProxySettings.java
* Created by liubingsr on 2012-5-14 下午2:47:28 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.network;

import org.apache.http.HttpHost;

/**
 * @ClassName: ProxySettings
 * @Description: gprs、wlan http proxy
 * @author liubingsr
 * @date 2012-5-14 下午2:47:28
 *
 */
public class ProxySettings {
	/**
	* @Title: getProxyHost
	* @Description: 获取用户设置的http proxy
	* @return HttpHost
	*/
	public static HttpHost getProxyHost() {
		String host = android.net.Proxy.getDefaultHost();
		int port = android.net.Proxy.getDefaultPort();
		if (host != null && port != -1) {
			return new HttpHost(host, port);
		} else {
			return null;
		}
	}
}
