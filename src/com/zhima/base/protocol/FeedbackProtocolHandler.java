/* 
 * @Title: FeedbackProtocolHandler.java
 * Created by liubingsr on 2012-5-21 下午3:35:44 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import android.content.Context;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoContent;

public final class FeedbackProtocolHandler {
	/**
	* @ClassName: AddFeedbackProtocolHandler
	* @Description: 提交反馈信息协议
	* @author liubingsr
	* @date 2012-9-21 上午11:14:55
	*
	*/
	public final static class AddFeedbackProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "AddFeedbackProtocolHandler";
		private static Gson gson = null;    
	    static {  
	        if (gson == null) {  
	            gson = new Gson();
	        }  
	    }
		
		public AddFeedbackProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: addFeedback
		* @Description: 意见反馈
		* @param content 内容
		* @return void
		*/
		public void addFeedback(String content) {
			mSubUrl = "feedback";
			String url = mBaseUrl + mSubUrl;
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.POST);
			
			VoContent vo = new VoContent();
			vo.setContent(content);
			String json = gson.toJson(vo);
			reqInfo.setBody(json);
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.ADD_FEEDBACK_PROTOCOL;
			mRequestService.sendRequest(this);
		}		
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					result = true;
//					if (!mResponeVO.isNull("items")) {
//						JSONArray ids = mResponeVO.getJSONArray("items");
//						if (ids != null && ids.length() > 0) {
//							if (!ids.isNull(0)) {
//								result = true;
//							}
//						}
//					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			return result;
		}

		@Override
		public void afterParse() {
			// TODO 成功处理
		}
	}
}