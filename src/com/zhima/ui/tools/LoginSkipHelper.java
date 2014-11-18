/* 
* @Title: LoginSkipHelper.java
* Created by liubingsr on 2012-8-22 下午6:38:59 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.ui.tools;

import android.content.Context;

import com.zhima.base.utils.SettingHelper;
import com.zhima.ui.activity.BaseActivity;

/**
 * @ClassName: LoginSkipHelper
 * @Description: TODO(描述这个类的作用)
 * @author liubingsr
 * @date 2012-8-22 下午6:38:59
 *
 */
public final class LoginSkipHelper {
	/**
	 * 跳转到登录页面前需要暂存的数据。
	 */
	private static Object sLoginContextData = null;
	/**
	 * 登录成功后需要跳转到的页面
	 */
	private static BaseActivity sLoginSuccessActivity = null;
	
	/**
	 * @Title: isLogin
	 * @Description: 用户是否已经登录
	 * @return true:已经登录
	 */
	public static boolean isLogin(Context context) {
		boolean ret = SettingHelper.getBoolean(context, SettingHelper.Field.LOGIN_SUCCESS, false);
		return true;//ret && !TextUtils.isEmpty(SettingHelper.getString(context, Field.ACCESS_TOKEN, ""));
	}
	
	public static Object getLoginContextData() {
		return sLoginContextData;
	}
	public static void setLoginContextData(Object contextData) {
		sLoginContextData = contextData;
	}

	public static BaseActivity getLoginSuccessActivity() {
		return sLoginSuccessActivity;
	}
	public static void setLoginSuccessActivity(BaseActivity activity) {
		sLoginSuccessActivity = activity;
	}
	
	/**
	* @Title: switchLoginActivity
	* @Description: 跳转到登录页面
	* 如果需要暂存登录前的数据，那么调用setLoginContextData()函数设置，同时调用setLoginSuccessBaseActivity()设置登录成功后需要跳转到的页面
	* @param c
	* @param needBack 登录成功后是否需要跳转
	* @return void
	*/
	public static void switchLoginActivity(BaseActivity activity, boolean needBack) {
		if (!needBack) {
			sLoginContextData = null;
			sLoginSuccessActivity = null;
		}
//		Intent it = new Intent(activity, LoginMainActivity.class);
//		activity.startActivity(it);
	}
}
