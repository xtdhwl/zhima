/* 
* @Title: AppStartupHttpRequestCallback.java
* Created by liubingsr on 2012-5-14 下午4:37:21 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.service;

import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;

/**
* @ClassName: LoginHttpRequestCallback
* @Description: 登录处理
* @author liubingsr
* @date 2012-9-22 下午1:37:10
*
*/
public class LoginHttpRequestCallback implements IHttpRequestCallback {
	private final static String TAG = "LoginHttpRequestCallback";
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
		if (protocolType == ProtocolType.LOGIN_PROTOCOL) {
			if (protocol.isHttpSuccess()) {
				if (protocol.isHandleSuccess()) {
					// TODO登录处理
//					Logger.getInstance(TAG).debug("登录成功！帐号信息:\r\n" + AccountService.getInstance(protocol.getContext()).getMyself().toString());	
				}
			} else {
				// TODO 网络连接错误
			}
		}		
	}
}
