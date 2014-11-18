/* 
* @Title: SpaceKindHttpRequestCallback.java
* Created by liubingsr on 2012-5-14 下午4:37:21 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.data.service;

import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.SpaceKindProtocolHandler.SyncSpacekindDictProtocol;
import com.zhima.base.utils.SettingHelper;

/**
* @ClassName: SpaceKindHttpRequestCallback
* @Description: 同步空间类型字典回调
* @author liubingsr
* @date 2012-9-22 下午1:37:10
*
*/
public class SpaceKindHttpRequestCallback implements IHttpRequestCallback {
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
		if (protocolType == ProtocolType.SYNC_SPACEKIND_DICT_PROTOCOL) {
			if (protocol.isHttpSuccess()) {
				if (protocol.isHandleSuccess()) {
					// TODO 同步空间类型字典处理成功，更新时间戳
					SyncSpacekindDictProtocol p = (SyncSpacekindDictProtocol)protocol;
					SettingHelper.setLong(protocol.getContext(), SettingHelper.Field.DICT_SPACE_TIMESTAMP, p.getLastTimestamp());	
				}
			} else {
				// TODO 网络连接错误
			}
		}
	}
}
