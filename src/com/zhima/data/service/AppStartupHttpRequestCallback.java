/* 
* @Title: AppStartupHttpRequestCallback.java
* Created by liubingsr on 2012-5-14 下午4:37:21 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.service;

import java.util.ArrayList;

import android.content.Context;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.TargetType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.ZMDictTimestamp;
import com.zhima.ui.common.view.HaloToast;

/**
* @ClassName: AppStartupHttpRequestCallback
* @Description: appStartup
* @author liubingsr
* @date 2012-9-22 下午1:37:10
*
*/
public class AppStartupHttpRequestCallback implements IHttpRequestCallback {
	private Context mContext = null;
	public AppStartupHttpRequestCallback(Context c) {
		mContext = c;
	}
	/**
	* @Title: onHttpStart
	* @Description: 发送http请求前回调
	* @param protocol
	* @return void
	*/
	public void onHttpStart(ProtocolHandlerBase protocol) {
		
	}
	/**
	* @Title: onHttpResult
	* @Description: 接收服务端http返回数据后回调
	* @param protocol
	* @return void
	*/
	public void onHttpResult(ProtocolHandlerBase protocol) {
		int protocolType = protocol.getProtocolType();
		if (protocolType == ProtocolType.APP_STARTUP_PROTOCOL) {
			if (protocol.isHttpSuccess()) {
				// 首次启动同步字典数据
				ArrayList<ZMDictTimestamp> list = new ArrayList<ZMDictTimestamp>();
				list.add(new ZMDictTimestamp(TargetType.TARGET_TYPE_SPACE_KIND, System.currentTimeMillis()));
				list.add(new ZMDictTimestamp(TargetType.TARGET_TYPE_CITY, System.currentTimeMillis()));
				list.add(new ZMDictTimestamp(TargetType.TARGET_TYPE_IDOL_JOB, System.currentTimeMillis()));
				AppLaunchService.getInstance(mContext).syncDict(list);
			} else {
				// TODO 网络连接错误
				HaloToast.show(mContext, R.string.network_request_failed);
			}
		}		
	}
}
