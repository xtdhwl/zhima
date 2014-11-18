/* 
 * @Title: LoginRegisterProtocolHandler.java
 * Created by liubingsr on 2012-5-21 下午3:35:44 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.SMSVerifyType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpUtils;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoLaunchUploadInfo;
import com.zhima.data.model.AppInitInfo;
import com.zhima.data.model.LaunchUploadInfo;
import com.zhima.data.model.LogonUser;
import com.zhima.data.service.LoginService;

public final class LoginRegisterProtocolHandler {	
	public final static class LoginProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "LoginProtocolHandler";
		
		private String mAccessToken = "";
		private LogonUser mUserProfile;
		
		public LoginProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mUserProfile = null;
		}
		/**
		* @Title: getUserProfile
		* @Description: 得到服务器返回的登录用户profile
		* @return null 登录失败
		*/
		public LogonUser getLogonUser() {
			return mUserProfile;
		}
		/**
		* @Title: login
		* @Description: 登录
		* @param loginAccount 登录帐号
		* @param password 密码明文
		* @return void
		*/
		public void login(String loginAccount, String password, String loginType) {
			mSubUrl = String.format("user/login?loginType=%s", loginType);
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",loginAccount,password);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.LOGIN_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							if (!objArray.isNull(0)) {
								mUserProfile = LogonUser.parse(objArray.getJSONObject(0));
								result = true;
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			if (!result) {
				LoginService.getInstance(mContext).onLoginFailed();
			}
			return result;
		}

		@Override
		public void afterParse() {
			// TODO 登录成功处理
			LoginService.getInstance(mContext).onLoginSuccess(mUserProfile);
		}
	}
	/**
	* @ClassName: LogoutProtocolHandler
	* @Description: 注销登录接口协议
	* @author liubingsr
	* @date 2012-8-1 上午10:00:47
	*
	*/
	public final static class LogoutProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "LogoutProtocolHandler";
		
		public LogoutProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		* @Title: logout
		* @Description: 注销登录
		* @return void
		*/
		public void logout() {
			mSubUrl = "user/logout";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.LOGOUT_PROTOCOL;
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
								result = ids.getBoolean(0);
							}
						}
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + mJson + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			if (!result) {
				// TODO
			}
			return result;
		}

		@Override
		public void afterParse() {
			// TODO 注销登录成功
			LoginService.getInstance(mContext).onLogoutSuccess();
		}
	}
	/**
	* @ClassName: RegisterProtocolHandler
	* @Description: 用户注册接口协议
	* @author liubingsr
	* @date 2012-7-28 下午6:08:45
	*
	*/
	public final static class RegisterProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "RegisterProtocolHandler";
		private long mUserId = 0;
		private LogonUser mUserProfile = null;
		
		public RegisterProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public String getZMUserId() {
			return mUserProfile.getZMUserId();
		}
		/**
		* @Title: registerByEmail
		* @Description: 邮箱注册
		* @param email 邮箱
		* @param password 密码明文
		* @return void
		*/
		public void registerByEmail(String email, String password) {
			mSubUrl = "user/register/email";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
			info.setBody(json);
			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.REGISTER_PROTOCOL;
//			mRequestService.sendRequest(this);
		}
		/**
		* @Title: registerByMobile
		* @Description: 手机号码注册
		* @param mobileNumber 手机号码
		* @param password 密码明文
		* @param verifyCode 短信验证码
		* @return void
		*/
		public void registerByMobile(String mobileNumber, String password, String verifyCode) {
			mSubUrl = "user/register/mobile?verifyCode=%s&autoLogin=true";
			String url = mBaseUrl + String.format(mSubUrl, verifyCode);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = String.format("{\"mobileNumber\":\"%s\",\"password\":\"%s\"}", mobileNumber, password);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.REGISTER_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {							
							if (!objArray.isNull(0)) {
								mUserProfile = LogonUser.parse(objArray.getJSONObject(0));
								result = true;
							}
						}
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + mJson + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return result;
		}

		@Override
		public void afterParse() {
			// TODO 注册成功
			LoginService.getInstance(mContext).onLoginSuccess(mUserProfile);
		}
	}
	/**
	* @ClassName: RequestSendSmsVerifyCodeProtocolHandler
	* @Description: 请求发送短信验证码协议
	* @author liubingsr
	* @date 2012-7-28 下午7:45:45
	*
	*/
	public final static class RequestSendSmsVerifyCodeProtocolHandler extends ProtocolHandlerBase {
		
		public RequestSendSmsVerifyCodeProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: sendVerifyCode
		* @Description: 发送短信验证码
		* @param mobileNumber 接收短信的手机号码
		* @return void
		*/
		public void sendVerifyCode(String mobileNumber) {
			mSubUrl = "user/register/mobile/send_sms";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = String.format("{\"mobileNumber\":\"%s\"}", mobileNumber);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.REQUEST_SEND_VERIFYCODE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void afterParse() {
			// TODO
		}
	}
	/**
	* @ClassName: RequestSendActivationEmailProtocolHandler
	* @Description: 请求发送邮箱激活邮件协议
	* @author liubingsr
	* @date 2012-8-1 上午10:22:44
	*
	*/
	public final static class RequestSendActivationEmailProtocolHandler extends ProtocolHandlerBase {
		
		public RequestSendActivationEmailProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: sendActivationEmail
		* @Description: 发送邮箱激活邮件
		* @param email 接收邮件的邮箱
		* @return void
		*/
		public void sendActivationEmail(String email) {
			mSubUrl = "user/email/send_email";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = String.format("{\"newEmail\":\"%s\"}", email);
			info.setBody(json);
			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.REQUEST_SEND_ACTIVATIONEMAIL_PROTOCOL;
//			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void afterParse() {
			// TODO
		}
	}
	/**
	* @ClassName: UserIsRegisteredProtocolHandler
	* @Description: 检测用户名是否已经被占用协议
	* @author liubingsr
	* @date 2013-1-2 上午11:29:55
	*
	*/
	public final static class UserIsRegisteredProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "UserIsRegisteredProtocolHandler";
		private boolean mIsRegistered = false;
		
		public UserIsRegisteredProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: isRegistered
		* @Description: 是否已经注册
		* @return true:已经注册,false:没有注册
		*/
		public boolean isRegistered() {
			return mIsRegistered;
		}
		/**
		* @Title: userIsRegistered
		* @Description: 是否已经注册
		* @param userName 手机号码
		* @return void
		*/
		public void userIsRegistered(String userName) {
			mSubUrl = "user/exist/username?value=%s";
			String url = mBaseUrl + String.format(mSubUrl, HttpUtils.urlEncode(userName,"utf-8"));
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.USERNAME_IS_REGISTERED_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mIsRegistered = array.getBoolean(0);
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO
		}
	}
	
	/**
	* @ClassName: MobileIsRegisteredProtocolHandler
	* @Description: 检测手机号码是否已经注册协议
	* @author liubingsr
	* @date 2012-8-18 上午11:12:33
	*
	*/
	public final static class MobileIsRegisteredProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "MobileIsRegisteredProtocolHandler";
		private boolean mIsRegistered = false;
		
		public MobileIsRegisteredProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: isRegistered
		* @Description: 手机号码是否已经注册
		* @return true:已经注册,false:没有注册
		*/
		public boolean isRegistered() {
			return mIsRegistered;
		}
		/**
		* @Title: mobileIsRegistered
		* @Description: 手机号码是否已经注册
		* @param mobile 手机号码
		* @return void
		*/
		public void mobileIsRegistered(String mobile) {
			mSubUrl = "user/exist/mobile?value=%s";
			String url = mBaseUrl + String.format(mSubUrl, HttpUtils.urlEncode(mobile,"utf-8"));
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.MOBILE_IS_REGISTERED_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mIsRegistered = array.getBoolean(0);
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO
		}
	}
	/**
	* @ClassName: EmailIsRegisteredProtocolHandler
	* @Description: 检测邮箱是否已经注册协议
	* @author liubingsr
	* @date 2012-8-18 上午11:17:45
	*
	*/
	public final static class EmailIsRegisteredProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "EmailIsRegisteredProtocolHandler";
		private boolean mIsRegistered = false;
		
		public EmailIsRegisteredProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: isRegistered
		* @Description: 邮箱是否已经注册
		* @return true:已经注册,false:没有注册
		*/
		public boolean isRegistered() {
			return mIsRegistered;
		}
		/**
		* @Title: emailIsRegistered
		* @Description: 邮箱是否已经注册
		* @param email 邮箱
		* @return void
		*/
		public void emailIsRegistered(String email) {
			mSubUrl = "user/exist/email?value=%s";
			String url = mBaseUrl + String.format(mSubUrl, HttpUtils.urlEncode(email,"utf-8"));
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.EMAIL_IS_REGISTERED_PROTOCOL;
//			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			Logger.getInstance(TAG).debug("数据包:<" + json + ">");
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mIsRegistered = array.getBoolean(0);
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO
		}
	}
	/**
	* @ClassName: ValidateVerifyCodeProtocolHandler
	* @Description: 检测验证码协议
	* @author liubingsr
	* @date 2012-8-13 上午11:53:35
	*
	*/
	public final static class ValidateVerifyCodeProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "ValidateVerifyCodeProtocolHandler";
		private boolean mValid = false;
		
		public ValidateVerifyCodeProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: isValid
		* @Description: 验证码是否有效
		* @return true 有效，false 无效
		*/
		public boolean isValid() {
			return mValid;
		}
		
		/**
		* @Title: validate
		* @Description: 检测验证码是否有效
		* @param verifyCode 验证码
		* @return void
		*/
		public void validate(String mobile, String verifyCode) {
			validate(SMSVerifyType.REGISTER, verifyCode, "", mobile);
		}
		
		public void validateReset(String mobile, String verifyCode) {
			validate(SMSVerifyType.RESET_PASSWORD, verifyCode, "", mobile);
		}
		
		private void validate(int verifyType, String verifyCode, String email, String mobile) {
			mSubUrl = "system/check/verify_code/%d/%s?email=%s&mobileNumber=%s";
			String url = mBaseUrl + String.format(mSubUrl, verifyType, verifyCode, HttpUtils.urlEncode(email,"utf-8"),
					HttpUtils.urlEncode(mobile,"utf-8"));
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.REQUEST_VALIDATE_VERIFYCODE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mValid = array.getBoolean(0);
							return true;
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
		}
	}
	/**
	* @ClassName: ChangePasswordProtocolHandler
	* @Description: 修改密码协议处理
	* @author liubingsr
	* @date 2012-8-14 上午10:21:01
	*
	*/
	public final static class ChangePasswordProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "ChangePasswordProtocolHandler";
		private boolean mSuccess = false;
		
		public ChangePasswordProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: isChangeSuccessful
		* @Description: 是否成功
		* @return true 成功，false 失败
		*/
		public boolean isChangeSuccessful() {
			return mSuccess;
		}
		
		/**
		* @Title: changePassword
		* @Description: 修改密码
		* @param oldPassword 旧密码
		* @param newPassword 新密码
		* @return void
		*/
		public void changePassword(String oldPassword, String newPassword) {
			mSubUrl = "user/password";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.PUT);
			String json = String.format("{\"oldPassword\":\"%s\",\"newPassword\":\"%s\"}", oldPassword, newPassword);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.CHANGE_PASSWORD_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mSuccess = array.getBoolean(0);
							return true;
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
		}
	}
	/**
	* @ClassName: ResetNewPasswordProtocolHandler
	* @Description: 设置新密码
	* @author liubingsr
	* @date 2013-1-2 下午2:24:21
	*
	*/
	public final static class ResetNewPasswordProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "ResetNewPasswordProtocolHandler";
		private boolean mSuccess = false;
		
		public ResetNewPasswordProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: isResetSuccessful
		* @Description: 是否成功
		* @return true 成功，false 失败
		*/
		public boolean isResetSuccessful() {
			return mSuccess;
		}
		
		/**
		* @Title: resetByMobile
		* @Description: 通过手机号重置新密码
		* @param verifyCode 短信验证码
		* @param mobile 手机号码
		* @param newPassword 新的密码
		* @return void
		*/
		public void resetByMobile(String verifyCode, String mobile, String newPassword) {
			String json = String.format("{\"email\":\"%s\",\"mobileNumber\":\"%s\",\"password\":\"%s\"}", "", mobile, newPassword);			
			doReset(verifyCode, json);
		}
		/**
		* @Title: resetByEmail
		* @Description: 通过邮箱重置新密码
		* @param verifyCode  验证码
		* @param email 邮箱
		* @param newPassword 新的密码
		* @return void
		*/
		public void resetByEmail(String verifyCode, String email, String newPassword) {
			String json = String.format("{\"email\":\"%s\",\"mobileNumber\":\"%s\",\"password\":\"%s\"}", email, "", newPassword);			
			doReset(verifyCode, json);
		}
		
		private void doReset(String verifyCode, String json) {
			mSubUrl = "user/password/reset?verifyCode=%s";
			String url = mBaseUrl + String.format(mSubUrl, verifyCode);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.PUT);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.RESET_NEW_PASSWORD_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mSuccess = array.getBoolean(0);
							return true;
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
		}
	}
	
	
//	//----------------------开放平台登录测试--------------------------------
//	
//	
//	/**
//	 * @ClassName: OpenLoginProtocolHandler
//	 * @Description: 开放平台登录测试
//	 * @author yusonglin
//	 * @date 2013-1-3 下午4:13:18
//	*/
//	public final static class OpenLoginProtocolHandler extends ProtocolHandlerBase {
//		private final static String TAG = "OpenLoginProtocolHandler";
//		
//		public OpenLoginProtocolHandler(Context context, IHttpRequestCallback callBack) {
//			super(context, callBack);
//		}
//		
//		public void addToken(String userId,String token,String from) {
//			String url = String.format("http://172.16.16.101:8080/rest/service/token/%s/%s?from=%s", token,userId,from);
//			doRequest(url, ProtocolType.OPEN_LOGIN_ADD_TOKEN, RequestType.POST);
//		}
//		
//		public void deleteToken(String userId,String token) {
//			String url = String.format("http://172.16.16.101:8080/rest/service/token/%s/%s", token,userId);
//			doRequest(url, ProtocolType.OPEN_LOGIN_DELETE_TOKEN, RequestType.DELETE);
//		}
//		
//		public void isTokenExist(String userId,String token) {
//			String url = String.format("http://172.16.16.101:8080/rest/service/token/%s/%s", token,userId);
//			doRequest(url, ProtocolType.OPEN_LOGIN_FIND_TOKEN, RequestType.GET);
//		}
//		
//		public void updateToken(String userId,String token,String from) {
//			String url = String.format("http://172.16.16.101:8080/rest/service/token/%s/%s?from=%s", token,userId,from);
//			doRequest(url, ProtocolType.OPEN_LOGIN_UPDATE_TOKEN, RequestType.PUT);
//		}
//		
//		private void doRequest(String url,int protocolType,RequestType requestType) {
//			RequestInfo info = new RequestInfo(url);
//			info.setRequestType(requestType);
//			this.setRequestInfo(info);
//			mProtocolType = protocolType;
//			mRequestService.sendRequest(this);
//		}
//		
//		@Override
//		public boolean parse() {
//			
//			String json = mRequestInfo.getRecieveData();
//			// 解析服务器返回的json数据包
//			try {
//				JSONTokener jsonParser = new JSONTokener(json);
//				JSONObject vo = (JSONObject) jsonParser.nextValue();
//				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
//				setProtocolStatus(responseHeader.getStatus());
//				if (responseHeader.getStatus() == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
//					if (!vo.isNull("items")) {
//						JSONArray ids = vo.getJSONArray("items");
//						if (ids != null && ids.length() > 0) {
//							if (!ids.isNull(0)) {
////								mUserId = ids.getLong(0);
//							}
//						}
//					}
//				} else {
//					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
//				}
//			} catch (Exception ex) {
//				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
//			}
//			return true;
//		}
//
//		@Override
//		public void afterParse() {
//			
//		}
//	}
	
	/**
	 * @ClassName: RequestSendResetPwdProtocolHandler
	 * @Description: 找回密码时请求下发手机验证码或者请求发送邮件
	 * @author yusonglin
	 * @date 2012-8-27 下午5:50:58
	*/
	public final static class RequestSendResetPwdProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "RequestSendResetSmsVerifyCodeProtocolHandler";
		private boolean mSuccess = false;
		
		public RequestSendResetPwdProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		 * @Title: isResetSuccessful
		 * @Description: 是否请求发送验证码成功
		 * @return true 成功，false 失败
		 */
		public boolean isResetSuccessful() {
			return mSuccess;
		}
		
		/**
		 * @Title: resetPassword
		 * @Description: 向指定的手机号码发送验证码
		 * @param mobile 手机号码
		 */
		public void sendVerifyCode(String mobile) {
			mSubUrl = "user/password/reset";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			
			String json = String.format("{\"email\":\"\",\"mobileNumber\":\"%s\"}",mobile);
			
			Logger.getInstance(TAG).debug("发送数据包:<" + json + ">");
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
//		/**
//		 * @Title: resetPassword
//		 * @Description: 向指定的邮箱发送邮件
//		 * @param email 邮箱账号
//		 */
//		public void sendEmail(String email) {
//			mSubUrl = "user/password/reset";
//			String url = mBaseUrl + mSubUrl;
//			RequestInfo info = new RequestInfo(url);
//			info.setRequestType(RequestType.POST);
//			
//			String json = String.format("{\"email\":\"%s\",\"mobileNumber\":\"\"}",email);
//			
//			Logger.getInstance(TAG).debug("发送数据包:<" + json + ">");
//			info.setBody(json);
//			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.REQUEST_SEND_RESET_EMAIL_PROTOCOL;
//			mRequestService.sendRequest(this);
//		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mSuccess = array.getBoolean(0);
							return true;
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}
		
		@Override
		public void afterParse() {
			
		}
	}
	
	/**
	 * @ClassName: loginFrom3ProtocolHandler
	 * @Description: TODO
	 * @author yusonglin
	 * @date 2013-1-16 下午3:00:24
	*/
	public final static class LoginFrom3ProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "LoginFrom3ProtocolHandler";
		
		private LogonUser mUserProfile;
		
		public LoginFrom3ProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public LogonUser getUserProfile(){
			return mUserProfile;
		}
		
		public void Login(String from,String uid,String returnUrl) {
			mSubUrl = "user/login_3rd";
			String url = mBaseUrl + mSubUrl;
			url = String.format(url+"?from_3rd=%s&uid_3rd=%s", from,uid);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			info.setBody(returnUrl);
			Logger.getInstance(TAG).debug("发送数据包:<" + returnUrl + ">");
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.LOGIN_FROM3_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							if (!array.isNull(0)) {
								mUserProfile = LogonUser.parse(array.getJSONObject(0));
								result = true;
							}
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			if (!result) {
				LoginService.getInstance(mContext).onLoginFailed();
			}
			return result;
		}
		
		@Override
		public void afterParse() {
			// TODO 登录成功处理
			LoginService.getInstance(mContext).onLoginSuccess(mUserProfile);
		}
	}
	/**
	 * 第三方用户登录之后 绑定 芝麻用户
	 * @ClassName: BindExternalUserProtocolHandler
	 * @Description: TODO
	 * @author yusonglin
	 * @date 2013-1-16 下午5:00:07
	*/
	public final static class BindExternalUserProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "BindExternalUserProtocolHandler";
		
		private LogonUser mUserProfile;
		
		public BindExternalUserProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public LogonUser getUserProfile(){
			return mUserProfile;
		}
		
		public void bind(String username,String password) {
			mSubUrl = "external/userbind";
			String url = mBaseUrl + mSubUrl;
			url = String.format(url+"?username=%s&password=%s", username,password);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.LOGIN_BIND_EXTERNAL_USER;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray array = vo.getJSONArray("items");
						if (array != null && array.length() > 0) {
							if (!array.isNull(0)) {
								mUserProfile = LogonUser.parse(array.getJSONObject(0));
								result = true;
							}
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			if (!result) {
				LoginService.getInstance(mContext).onLoginFailed();
			}
			return result;
		}
		
		@Override
		public void afterParse() {
			// TODO 登录成功处理
			LoginService.getInstance(mContext).onLoginSuccess(mUserProfile);
		}
	}
	
	/**
	 * 自动注册用户，并自动登录返回登录的用户信息。 
	 * @ClassName: RegisterUserByAutoProtocolHandler
	 * @Description: TODO
	 * @author yusonglin
	 * @date 2013-1-17 上午10:48:47
	*/
	public final static class RegisterUserByAutoProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "RegisterUserByAutoProtocolHandler";
		private static Gson gson = null;    
	    static {  
	        if (gson == null) {  
	            gson = new Gson();
	        }  
	    }
		private LogonUser mUserProfile;
		
		public RegisterUserByAutoProtocolHandler(Context context, IHttpRequestCallback callBack) {
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
		public void register(LaunchUploadInfo info) {
			mSubUrl = "user/register/auto";
			String url = mBaseUrl + mSubUrl;
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.POST);
			VoLaunchUploadInfo vo = VoLaunchUploadInfo.createInstance(info);
			String json = gson.toJson(vo);
			reqInfo.setBody(json);
			Logger.getInstance(TAG).debug(TAG+":"+json);
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.REGISTER_USER_BY_AUTO;
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
