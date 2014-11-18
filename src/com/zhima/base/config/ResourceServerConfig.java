/* 
* @Title: ResourceServerConfig.java
* Created by liubingsr on 2012-7-19 上午11:42:11 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.config;

import java.util.TreeMap;

import com.zhima.app.ZhimaApplication;
import com.zhima.data.service.AppLaunchService;

/**
 * @ClassName: ResourceServerConfig
 * @Description: 图片服务器配置信息
 * @author liubingsr
 * @date 2012-7-19 上午11:42:11
 *
 */
public final class ResourceServerConfig {
	private static ResourceServerConfig mInstance = null;
	private final static String WIDTH_HOLDER = "{width}";
	
	private TreeMap<String, String> mServerMap;
	private String mRestRootPath = "";
	
	private ResourceServerConfig() {
		mServerMap = new TreeMap<String, String>();
	}
	
	public static ResourceServerConfig getInstance() {
		if (mInstance == null) {
			mInstance = new ResourceServerConfig();
		}
		return mInstance;
	}
	public boolean isEmpty() {
		return mServerMap == null || mServerMap.isEmpty();
	}
	public void addServerUrl(String prefix, String serverUrl) {
		mServerMap.put(prefix, serverUrl);
	}
	/**
	* @Title: getServerUrl
	* @Description: 由前缀得到服务器网址
	* @param prefix
	* @return
	* String
	*/
	public String getServerUrl(String prefix) {
		if (!mServerMap.containsKey(prefix)) {
			return mServerMap.get(prefix);
		} else {
			return null;
		}
	}
	/**
	* @Title: getRealImageUrl
	* @Description: 得到真实的图片url地址
	* @param url 图片url片段
	* @param width 图片宽度
	* @return
	* String
	*/
	public String getRealImageUrl(String url, int width) {
		if (isEmpty()) {
			AppLaunchService.getInstance(ZhimaApplication.getContext()).initResourcePath();
		}
		for (String prefix : mServerMap.keySet()) {
			if (url.startsWith(prefix)) {
				String result = url.replace(prefix, mServerMap.get(prefix));
				// TODO 目前简单的用空字符替换
				return result.replace(WIDTH_HOLDER, "");
			}
		}
		return url;
	}
	/**
	* @Title: getRealImageUrl
	* @Description: 得到真实的图片url地址
	* @param url
	* @param scaleType 图片尺寸类型
	* @see com.zhima.base.consts.ZMConsts.ImageScaleType
	* @return
	* String
	*/
	public String getRealImageUrl(String url, String scaleType) {
		if (isEmpty()) {
			AppLaunchService.getInstance(ZhimaApplication.getContext()).initResourcePath();
		}
		for (String prefix : mServerMap.keySet()) {
			if (url.startsWith(prefix)) {
				String result = url.replace(prefix, mServerMap.get(prefix));
				return result.replace(WIDTH_HOLDER, scaleType);
			}
		}
		return url;
	}
}
