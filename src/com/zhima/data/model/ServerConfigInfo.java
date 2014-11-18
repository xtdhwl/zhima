/* 
 * @Title: ServerConfigInfo.java
 * Created by liubingsr on 2012-9-21 上午10:46:22 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: ServerConfigInfo
 * @Description: appInit服务器返回信息
 * @author liubingsr
 * @date 2012-9-21 上午10:46:22
 * 
 */
public class ServerConfigInfo {
	private final static String TAG = "ServerConfigInfo";
	private static Gson gson = null;    
    static {  
        if (gson == null) {  
            gson = new Gson();  
        }  
    }

	/**
	 * REST服务根路径
	 */
	private String mRestRootUrl = "";
	/**
	 * SSL请求根地址
	 */
	private String mSSLRestRootUrl = "";
	/**
	 * 资源根路径 key值如"img1"，value如"http://172.16.16.12/image"
	 */
	private Map<String, String> mResourceRootPathMap = null;
	/**
	 * App端ping服务器间隔时间（秒）
	 */
	private int mPingInterval = 60;
	
	public String getRestRootUrl() {
		return mRestRootUrl;
	}
	public void setRestRootUrl(String url) {
		this.mRestRootUrl = url;
	}
	
	public String getSSLRestRootUrl() {
		return mSSLRestRootUrl;
	}
	public void setSSLRestRootUrl(String url) {
		this.mSSLRestRootUrl = url;
	}
	
	public Map<String, String> getResourceRootPathMap() {
		return mResourceRootPathMap;
	}
	public void setResourceRootPathMap(Map<String, String> map) {
		this.mResourceRootPathMap = map;
	}

	public int getPingInterval() {
		return mPingInterval;
	}
	public void setPingInterval(int seconds) {
		this.mPingInterval = seconds;
	}
	/**
	* @Title: parse
	* @Description: json数据包解析出对象
	* @param jsonObject
	* @return null 解析错误
	*/
	public static ServerConfigInfo parse(JSONObject jsonObject) {
		ServerConfigInfo obj = new ServerConfigInfo();
		try {
			obj.setRestRootUrl(jsonObject.getString("restServiceRootPath"));
			if (!jsonObject.isNull("sslRestServiceRootPath")) {
				obj.setSSLRestRootUrl(jsonObject.getString("sslRestServiceRootPath"));
			}
			if (!jsonObject.isNull("resourceRootPaths")) {
				String text = jsonObject.getJSONObject("resourceRootPaths").toString();
				Logger.getInstance(TAG).debug(text);
				java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>(){}.getType();
				Map<String, String> map = gson.fromJson(text, type);//new TypeReference<Map<String, String>>() {}
				obj.setResourceRootPathMap(map);
			}
			if (!jsonObject.isNull("pingInterval")) {
				obj.setPingInterval(jsonObject.getInt("pingInterval"));
			}
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return obj;
	}
}
