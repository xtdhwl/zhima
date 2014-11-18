/* 
* @Title: IHttpRequestCallback.java
* Created by liubingsr on 2012-5-14 下午4:37:21 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.network;

import com.zhima.base.protocol.ProtocolHandlerBase;

/**
 * @ClassName: IHttpRequestCallback
 * @Description: Http 协议回调接口
 * @author liubingsr
 * @date 2012-5-14 下午4:37:21
 *
 */
public interface IHttpRequestCallback {
	/**
	* @Title: onHttpStart
	* @Description: 发送http请求前回调
	* @param protocol
	* @return void
	*/
	public abstract void onHttpStart(ProtocolHandlerBase protocol);
	/**
	* @Title: onHttpResult
	* @Description: 接收服务端http返回数据后回调
	* @param protocol
	* @return void
	*/
	public abstract void onHttpResult(ProtocolHandlerBase protocol);
}
