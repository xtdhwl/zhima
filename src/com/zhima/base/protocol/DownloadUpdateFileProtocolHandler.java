/* 
* @Title: DownloadUpdateFileProtocolHandler.java
* Created by liubingsr on 2012-9-26 下午5:28:52 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.protocol;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;

/**
 * @ClassName: DownloadUpdateFileProtocolHandler
 * @Description: 文件下载处理
 * @author liubingsr
 * @date 2012-9-26 下午5:28:52
 *
 */
public class DownloadUpdateFileProtocolHandler {
	
	public final static class DownloadUpdateApkProtocolHandler extends ProtocolHandlerBase {
		public DownloadUpdateApkProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean parse() {
			return true;
		}
		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
}
