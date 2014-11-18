/* 
* @Title: SpaceKindHttpRequestCallback.java
* Created by liubingsr on 2012-5-14 下午4:37:21 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.service;

import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;

/**
* @ClassName: SpaceKindHttpRequestCallback
* @Description: 同步空间类型字典回调
* @author liubingsr
* @date 2012-9-22 下午1:37:10
*
*/
public class CheckDictHttpRequestCallback implements IHttpRequestCallback {
	private final static String TAG = "CheckDictHttpRequestCallback";
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
		if (protocolType == ProtocolType.CHECK_DICT_UPDATE_PROTOCOL) {
			if (protocol.isHttpSuccess()) {
				if (protocol.isHandleSuccess()) {
					// TODO 同步空间类型字典处理成功，更新时间戳
					Logger.getInstance(TAG).debug("CHECK_DICT_UPDATE_PROTOCOL Success");
				}
			} else {
				// TODO 网络连接错误
				Logger.getInstance(TAG).debug("CHECK_DICT_UPDATE_PROTOCOL network error");
			}
		}
	}
}
