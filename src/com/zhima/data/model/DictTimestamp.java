/* 
* @Title: DictUpdate.java
* Created by liubingsr on 2012-9-25 上午11:58:55 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.model;

import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * @ClassName: DictTimestamp
 * @Description: 检查字典更新
 * @author liubingsr
 * @date 2012-9-25 上午11:58:55
 *
 */
public class DictTimestamp {
	private final static String TAG = "DictTimestamp";
	private static Gson gson = null;    
    static {  
        if (gson == null) {  
            gson = new Gson();  
        }  
    }
	/**
	 * 字典数据最后更新时间 key值取自TargetType，value为目标表最后一次更新的时间
	 */
	private Map<String, Long> mDictLastUpdatedTimeMap = null;
	
	public Map<String, Long> getDictLastUpdatedTimeMap() {
		return mDictLastUpdatedTimeMap;
	}
	public void setDictLastUpdatedTimeMap(Map<String, Long> map) {
		this.mDictLastUpdatedTimeMap = map;
	}
	/**
	* @Title: parse
	* @Description: json数据包解析出对象
	* @param jsonObject
	* @return null 解析错误
	*/
	public static DictTimestamp parse(JSONObject jsonObject) {
		DictTimestamp obj = new DictTimestamp();
		if (jsonObject != null) {
			java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Long>>(){}.getType();
			Map<String, Long> map = gson.fromJson(jsonObject.toString(), type);
			obj.setDictLastUpdatedTimeMap(map);
		}
		return obj;
	}
}
