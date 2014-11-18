/* 
 * @Title: LoginRegisterProtocolHandler.java
 * Created by liubingsr on 2012-5-21 下午3:35:44 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

import android.content.Context;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpNetwork;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoLaunchUploadInfo;
import com.zhima.data.model.AppInitInfo;
import com.zhima.data.model.AppUpgrade;
import com.zhima.data.model.DictTimestamp;
import com.zhima.data.model.LaunchUploadInfo;
import com.zhima.data.model.LogonUser;
import com.zhima.data.model.ServerConfigInfo;
import com.zhima.data.model.ZMDictTimestamp;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.LoginService;

public final class AppLaunchProtocolHandler {
	/**
	* @ClassName: GetServerConfigProtocolHandler
	* @Description: 获取最新配置协议
	* @author liubingsr
	* @date 2012-9-26 下午3:11:10
	*
	*/
	public final static class GetServerConfigProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "GetServerConfigProtocolHandler";
		private ServerConfigInfo mServerConfigInfo = null;
		
		public GetServerConfigProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mBaseUrl = AppLaunchService.getInstance(mContext).getFixedServerUrl();
		}
		/**
		* @Title: getConfig
		* @Description: 得到最新的配置信息
		* @param platform 平台标识
		* @param version 版本
		* @return void
		*/
		public void getConfig(String platform, String version) {
			mSubUrl = "static/%s/%s/config.json";
			String url = mBaseUrl + String.format(mSubUrl, platform, version);
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.GET);			
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.GET_SERVER_CONFIG_PROTOCOL;
//			mRequestService.sendRequest(this);
			HttpNetwork httpNetwork = new HttpNetwork(mContext);
			httpNetwork.sendRequest(reqInfo);
			this.setHttpResultCode(reqInfo.getResultCode());
			if (reqInfo.getResultCode() == ErrorManager.NO_ERROR) {
				parseData();				
			}
			if (getHttpRequestCallback() != null) {
				getHttpRequestCallback().onHttpResult(this);
			} else if (mRequestCallback != null) {
				onHttpResult(this);
			}
		}
//		public ServerConfigInfo getServerConfigInfo() {
//			return mServerConfigInfo;
//		}
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
							if (!ids.isNull(0)) {
								mServerConfigInfo = ServerConfigInfo.parse(ids.getJSONObject(0));
								result = true;
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			return result;
//			boolean result = false;
//			// 解析服务器返回的json数据包
//			mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS;
//			try {
//				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
//					JSONTokener jsonParser = new JSONTokener(mJson);
//					mResponeVO = (JSONObject) jsonParser.nextValue();
//					if (mResponeVO != null) {
//						mServerConfigInfo = ServerConfigInfo.parse(mResponeVO);
//						result = true;
//					}
//				}
//			} catch (Exception ex) {
//				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
//			}
//			return result;
		}

		@Override
		public void afterParse() {
			// 成功处理
			AppLaunchService.getInstance(mContext).processServerConfig(mServerConfigInfo);
		}
	}
	/**
	* @ClassName: AutoUpgradeProtocolHandler
	* @Description: 版本升级协议
	* @author liubingsr
	* @date 2012-9-26 下午3:25:50
	*
	*/
	public final static class AutoUpgradeProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "AutoUpgradeProtocolHandler";
		private AppUpgrade mAppUpgrade = null;
		
		public AutoUpgradeProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mBaseUrl = AppLaunchService.getInstance(mContext).getFixedServerUrl();
		}
		public AppUpgrade getAppUpgrade() {
			return mAppUpgrade;
		}
		/**
		* @Title: checkUpdate
		* @Description: 检查版本更新信息
		* @param platform 平台标识
		* @param version 版本
		* @return void
		*/
		public void checkUpdate(String platform, String version) {
			mSubUrl = "static/%s/%s/update.json";
			String url = mBaseUrl + String.format(mSubUrl, platform, version);
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.GET);			
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.CHECK_APP_UPGRADE_PROTOCOL;
			mRequestService.sendRequest(this);
//			HttpNetwork httpNetwork = new HttpNetwork(mContext);
//			httpNetwork.sendRequest(reqInfo);
//			this.setHttpResultCode(reqInfo.getResultCode());
//			if (reqInfo.getResultCode() == ErrorManager.NO_ERROR) {
//				parseData();				
//			}
//			if (getHttpRequestCallback() != null) {
//				getHttpRequestCallback().onHttpResult(this);
//			} else if (mRequestCallback != null) {
//				onHttpResult(this);
//			}
		}
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
							if (!ids.isNull(0)) {
								mAppUpgrade = AppUpgrade.parse(ids.getJSONObject(0));
								result = true;
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			return result;
//			boolean result = false;
//			// 解析服务器返回的json数据包
//			mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS;
//			try {
//				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
//					JSONTokener jsonParser = new JSONTokener(mJson);
//					mResponeVO = (JSONObject) jsonParser.nextValue();
//					if (mResponeVO != null) {
//						mAppUpgrade = AppUpgrade.parse(mResponeVO);
//						result = true;
//					}
//				}
//			} catch (Exception ex) {
//				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
//			}
//			return result;
		}
		@Override
		public void afterParse() {
			// 成功处理			
		}
	}
	/**
	* @ClassName: CheckDictProtocolHandler
	* @Description: TODO(描述这个类的作用)
	* @author liubingsr
	* @date 2012-9-26 下午8:57:16
	*
	*/
	public final static class CheckDictProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "CheckDictProtocolHandler";
		private DictTimestamp mDictTimestamp = null;
		
		public CheckDictProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getDictTimestamp
		* @Description: 得到服务器返回的最新时间戳
		* @return
		* DictTimestamp
		*/
		public DictTimestamp getDictTimestamp() {
			return mDictTimestamp;
		}
		/**
		* @Title: checkUpdate
		* @Description: 检查更新信息
		* @return void
		*/
		public void getDictLastTimestamp() {
			mSubUrl = "system/check/dict";
			String url = mBaseUrl + mSubUrl;
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.GET);	
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.CHECK_DICT_UPDATE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
//			mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS;
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mDictTimestamp = DictTimestamp.parse(objArray.getJSONObject(0));
							result = true;
						}						
					} else {
						// 没有数据
					}					
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			return result;
		}
		@Override
		public void afterParse() {
			// 成功处理
			Map<String, Long> timestampMap = mDictTimestamp.getDictLastUpdatedTimeMap();
			Set<String> keys = timestampMap.keySet();
			ArrayList<ZMDictTimestamp> timeStampList = new ArrayList<ZMDictTimestamp>();
			for (String dictType : keys) {
				ZMDictTimestamp timeStamp = new ZMDictTimestamp(Integer.parseInt(dictType), timestampMap.get(dictType));
				timeStampList.add(timeStamp);
			}
			AppLaunchService.getInstance(this.mContext).syncDict(timeStampList);
		}
	}
	/**
	* @ClassName: AppStartupProtocolHandler
	* @Description: app启动信息协议
	* @author liubingsr
	* @date 2012-9-21 上午11:14:55
	*
	*/
	public final static class AppStartupProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "AppStartupProtocolHandler";
		private static Gson gson = null;    
	    static {  
	        if (gson == null) {  
	            gson = new Gson();
	        }  
	    }
		private LogonUser mUserProfile;
		
		public AppStartupProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mUserProfile = null;
		}
		/**
		* @Title: getUserProfile
		* @Description: 得到服务器返回的信息
		* @return null
		*/
		public LogonUser getUserProfile() {
			return mUserProfile;
		}
		/**
		* @Title: appStartup
		* @Description: TODO(描述这个方法的作用)
		* @param info
		* @param newInstall 是否安装后首次初始化
		* @param autoRegister 是否需要自动注册，true则在返回结果中会包含自动注册并登录的用户信息
		* @return void
		*/
		public void appStartup(LaunchUploadInfo info, boolean newInstall, boolean autoRegister) {
			mSubUrl = "system/init?newInstall=%s&autoRegister=%s";
			String url = mBaseUrl + String.format(mSubUrl, String.valueOf(newInstall), String.valueOf(autoRegister));
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.POST);
			
			VoLaunchUploadInfo vo = VoLaunchUploadInfo.createInstance(info);
			String json = gson.toJson(vo);
			reqInfo.setBody(json);
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.APP_STARTUP_PROTOCOL;
			mRequestService.sendRequest(this);
		}		
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
							if (!ids.isNull(0)) {
								AppInitInfo appInfo = AppInitInfo.parse(ids.getJSONObject(0));
								mUserProfile = appInfo.getUserProfile();
								result = true;
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			return result;
		}

		@Override
		public void afterParse() {
			// TODO 成功处理
			LoginService.getInstance(mContext).processAppStartupResp(mUserProfile);
		}
	}
}