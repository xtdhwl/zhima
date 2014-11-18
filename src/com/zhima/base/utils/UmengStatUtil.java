/* 
* @Title: UmengStatUtil.java
* Created by liubingsr on 2012-10-20 下午3:36:22 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.zhima.base.config.SystemConfig;

/**
 * @ClassName: UmengStatUtil
 * @Description: 友盟统计工具类
 * @author liubingsr
 * @date 2012-10-20 下午3:36:22
 *
 */
public class UmengStatUtil {
	/**
	* @Title: onResume
	* @Description: activity onResume中调用
	* @param context
	* @return void
	*/
	public static void onResume(Context context) {
		if (SystemConfig.STATS_ENABLE) {
			MobclickAgent.onResume(context);
		}
	}
	/**
	* @Title: onPause
	* @Description: activity onPause中调用
	* @param context
	* @return void
	*/
	public static void onPause(Context context) {
		if (SystemConfig.STATS_ENABLE) {
			MobclickAgent.onPause(context);
		}
	}
	/**
	* @Title: onEvent
	* @Description: 单击事件统计
	* @param context
	* @param eventId 事件id
	* @return void
	*/
	public static void onEvent(Context context, String eventId) {
		if (SystemConfig.STATS_ENABLE) {
			MobclickAgent.onEvent(context, eventId);
		}		
	}
}
