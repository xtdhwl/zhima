package com.zhima.data.service;

import android.content.Context;
import android.text.TextUtils;

import com.zhima.app.ZhimaApplication;
import com.zhima.base.consts.ZMConsts.AccessTokenType;
import com.zhima.base.consts.ZMConsts.ContactType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.BindExternalUserProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.ChangePasswordProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.EmailIsRegisteredProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.LoginFrom3ProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.LoginProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.LogoutProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.MobileIsRegisteredProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RegisterProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RegisterUserByAutoProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RequestSendActivationEmailProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RequestSendResetPwdProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RequestSendSmsVerifyCodeProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.ResetNewPasswordProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.UserIsRegisteredProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.ValidateVerifyCodeProtocolHandler;
import com.zhima.data.model.LaunchUploadInfo;
import com.zhima.data.model.LogonUser;

/**
* @ClassName: LoginService
* @Description: 用户注册、登录处理
* @author liubingsr
* @date 2012-7-30 下午7:26:21
*
*/
public class LoginService extends BaseService {
	private final static String TAG = "LoginService";
	private static LoginService mInstance = null;

	private LoginService(Context c) {
		super(c);
	}

	public static LoginService getInstance(Context c) {
		if (mInstance == null) {
			mInstance = new LoginService(c);
		}
		return mInstance;
	}
	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
		clear();
	}

	public void processAppStartupResp(LogonUser profile) {		
		if (profile != null && !TextUtils.isEmpty(profile.getAccessToken())) {
			saveUserProfile(profile, false);
		}
	}
	/**
	* @Title: saveUserProfile
	* @Description: 保存用户资料
	* @param profile
	* @return void
	*/
	private void saveUserProfile(LogonUser profile, boolean isFirstStartup){
		UserService.getInstance(mContext).addUser(profile);
		AccountService.getInstance(mContext).saveAccountData(profile, isFirstStartup);
	}
	/**
	* @Title: login
	* @Description: 登录
	* @param loginAccount 登录帐号
	* @param password 密码
	* @param loginType 登录方式
	* @param callBack 结果通知回调
	* @return void
	*/
	public void login(String loginAccount, String password, String loginType, IHttpRequestCallback callBack) {
		Logger.getInstance(TAG).debug("user:" + loginAccount + ",password:" + password);
		LoginProtocolHandler protocol = new LoginProtocolHandler(mContext, callBack);
		protocol.login(loginAccount, password, loginType);
	}
	/**
	* @Title: onLoginSuccess
	* @Description: 登录成功处理
	* @param profile 登录用户profile信息
	* @return void
	*/
	public void onLoginSuccess(LogonUser profile) {
		if (profile != null) {
			UserService.getInstance(mContext).addUser(profile);
			AccountService.getInstance(mContext).saveAccountData(profile, false);
			if (!profile.isGuest()) {
				// 非游客，下载数据
				downMyContactList();
			}
		} else {
			AccountService.getInstance(mContext).setLogin(false);
			AccountService.getInstance(mContext).setZMAccessTokenType(AccessTokenType.AUTO);
			AccountService.getInstance(mContext).setZMAccessToken("");
		}
	}
	private void downMyContactList() {
		ContactService.getInstance(mContext).getContactList(ContactType.PERSONAL, new DownContactHttpRequestCallback());
		ContactService.getInstance(mContext).getContactList(ContactType.SPACE, new DownContactHttpRequestCallback());
	}
	/**
	* @Title: onLoginFailed
	* @Description: 登录失败处理
	* @return void
	 */
	public void onLoginFailed() {
		AccountService.getInstance(mContext).setLogin(false);
		AccountService.getInstance(mContext).setZMAccessTokenType(AccessTokenType.AUTO);
		AccountService.getInstance(mContext).setZMAccessToken("");
    }
	/**
	* @Title: logout
	* @Description: 注销登录
	* @param callBack 结果通知回调
	* @return void
	*/
	public void logout(IHttpRequestCallback callBack) {
		LogoutProtocolHandler protocol = new LogoutProtocolHandler(mContext, callBack);
		protocol.logout();
	}
	/**
	* @Title: onLogoutSuccess
	* @Description: 注销登录成功处理
	* @return void
	*/
	public void onLogoutSuccess() {
		AccountService.getInstance(mContext).saveSetting("");
		AccountService.getInstance(mContext).setLogin(false);
		AccountService.getInstance(mContext).setZMAccessTokenType(AccessTokenType.AUTO);
		AccountService.getInstance(mContext).setZMAccessToken("");
		clearUserData();
	}
	private void clearUserData() {
		// 清空用户数据
		ContactService.getInstance(mContext).deleteAllContact();
		// 自动登录
		registerUserByAuto(new LoginHttpRequestCallback());
	}
	/**
	 * @Title: registerByEmail
	 * @Description: 邮箱注册
	 * @param email 邮箱
	 * @param password 密码明文
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void registerByEmail(String email, String password, IHttpRequestCallback callBack) {
		RegisterProtocolHandler protocol = new RegisterProtocolHandler(mContext, callBack);
		protocol.registerByEmail(email, password);
	}

	/**
	 * @Title: registerByMobile
	 * @Description: 手机号码注册
	 * @param mobileNumber 手机号码(注意：需要带+86前缀)
	 * @param password 密码明文
	 * @param verifyCode 短信验证码
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void registerByMobile(String mobileNumber, String password, String verifyCode, IHttpRequestCallback callBack) {
		RegisterProtocolHandler protocol = new RegisterProtocolHandler(mContext, callBack);
		protocol.registerByMobile(mobileNumber, password, verifyCode);
	}

	/**
	 * @Title: requestSendSmsVerifyCode
	 * @Description: 请求发送短信验证码
	 * @param mobileNumber 接收短信的手机号码(注意：需要带+86前缀)
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void requestSendSmsVerifyCode(String mobileNumber, IHttpRequestCallback callBack) {
		RequestSendSmsVerifyCodeProtocolHandler protocol = new RequestSendSmsVerifyCodeProtocolHandler(mContext, callBack);
		protocol.sendVerifyCode(mobileNumber);
	}
	/**
	* @Title: requestSendActivationEmail
	* @Description: 请求发送激活邮件
	* @param email 邮箱
	* @param callBack 结果通知回调
	* @return void
	*/
	public void requestSendActivationEmail(String email, IHttpRequestCallback callBack) {		
		RequestSendActivationEmailProtocolHandler protocol = new RequestSendActivationEmailProtocolHandler(mContext, callBack);
		protocol.sendActivationEmail(email);
	}
	/**
	* @Title: userNameIsRegistered
	* @Description: 用户名是否已经被注册
	* @param userName 用户名
	* @param callBack 结果通知回调
	* @return void
	*/
	public void userNameIsRegistered(String userName, IHttpRequestCallback callBack) {
		UserIsRegisteredProtocolHandler protocol = new UserIsRegisteredProtocolHandler(mContext, callBack);
		protocol.userIsRegistered(userName);
	}
	/**
	* @Title: mobileRegistered
	* @Description: 手机号码是否已经被注册
	* @param mobile 手机号码(注意：需要带+86前缀)
	* @param callBack 结果通知回调
	* @return void
	*/
	public void mobileIsRegistered(String mobile, IHttpRequestCallback callBack) {
		MobileIsRegisteredProtocolHandler protocol = new MobileIsRegisteredProtocolHandler(mContext, callBack);
		protocol.mobileIsRegistered(mobile);
	}
	/**
	* @Title: emailRegistered
	* @Description: 邮箱是否已经被注册
	* @param email 邮箱
	* @param callBack 结果通知回调
	* @return void
	*/
	public void emailIsRegistered(String email, IHttpRequestCallback callBack) {
		EmailIsRegisteredProtocolHandler protocol = new EmailIsRegisteredProtocolHandler(mContext, callBack);
		protocol.emailIsRegistered(email);
	}
	/**
	* @Title: validateRegisterVerifyCode
	* @Description: 请求检测验证码是否有效
	* @param mobile 手机号码(注意：需要带+86前缀)
	* @param verifyCode 要检测的验证码
	* @param callBack 结果通知回调
	* @return void
	*/
	public void validateRegisterVerifyCode(String mobile, String verifyCode, IHttpRequestCallback callBack) {
		ValidateVerifyCodeProtocolHandler protocol = new ValidateVerifyCodeProtocolHandler(mContext, callBack);
		protocol.validate(mobile, verifyCode);
	}
	/**
	* @Title: validateResetVerifyCode
	* @Description: 重置密码时请求检测验证码是否有效
	* @param mobile 手机号码(注意：需要带+86前缀)
	* @param verifyCode 要检测的验证码
	* @param callBack 结果通知回调
	* @return void
	*/
	public void validateResetVerifyCode(String mobile, String verifyCode, IHttpRequestCallback callBack) {
		ValidateVerifyCodeProtocolHandler protocol = new ValidateVerifyCodeProtocolHandler(mContext, callBack);
		protocol.validateReset(mobile, verifyCode);
	}
	/**
	* @Title: requestSendResetSmsVerifyCode
	* @Description: 重置密码时请求下发短信
	* @param mobileNumber 手机号码(注意：需要带+86前缀)
	* @param callBack 结果通知回调
	* @return void
	*/
	public void requestSendResetSmsVerifyCode(String mobile,IHttpRequestCallback callBack){
		RequestSendResetPwdProtocolHandler protocol = new RequestSendResetPwdProtocolHandler(mContext, callBack);
		protocol.sendVerifyCode(mobile);
	}
	/**
	* @Title: changePassword
	* @Description: 修改密码
	* @param oldPassword 旧密码
	* @param newPassword 新密码
	* @param callBack 结果通知回调
	* @return void
	*/
	public void changePassword(String oldPassword, String newPassword, IHttpRequestCallback callBack) {
		ChangePasswordProtocolHandler protocol = new ChangePasswordProtocolHandler(mContext, callBack);
		protocol.changePassword(oldPassword, newPassword);
	}
	
	/**
	* @Title: resetNewPasswordByMobile
	* @Description: 通过手机号码方式设置新的密码
	* @param verifyCode 短信验证码
	* @param mobile 手机号码(注意：需要带+86前缀)
	* @param newPassword 新密码
	* @param callBack 结果通知回调
	* @return void
	*/
	public void resetNewPasswordByMobile(String verifyCode, String mobile, String newPassword, IHttpRequestCallback callBack) {
		ResetNewPasswordProtocolHandler protocol = new ResetNewPasswordProtocolHandler(mContext, callBack);
		protocol.resetByMobile(verifyCode, mobile, newPassword);
	}
	/**
	* @Title: resetNewPasswordByEmail
	* @Description: 通过邮箱方式设置新的密码
	* @param verifyCode 验证码
	* @param email 邮箱
	* @param newPassword 新密码
	* @param callBack 结果通知回调
	* @return void
	*/
	public void resetNewPasswordByEmail(String verifyCode, String email, String newPassword, IHttpRequestCallback callBack) {
		ResetNewPasswordProtocolHandler protocol = new ResetNewPasswordProtocolHandler(mContext, callBack);
		protocol.resetByEmail(verifyCode, email, newPassword);
	}
	
	public void loginFrom3(String from,String uid,String returnUrl,IHttpRequestCallback callBack){
		LoginFrom3ProtocolHandler protocol = new LoginFrom3ProtocolHandler(mContext, callBack);
		protocol.Login(from, uid, returnUrl);
	}
	
	public void bindExternalUser(String username,String password,IHttpRequestCallback callBack){
		BindExternalUserProtocolHandler protocol = new BindExternalUserProtocolHandler(mContext, callBack);
		protocol.bind(username, password);
	}
	
	public void registerUserByAuto(IHttpRequestCallback callBack){
		LaunchUploadInfo launchUploadInfo = AppLaunchService.getInstance(mContext).getLaunchUploadInfo();
		RegisterUserByAutoProtocolHandler protocol = new RegisterUserByAutoProtocolHandler(mContext, callBack);
		protocol.register(launchUploadInfo);
	}
	
//	public void addToken(String userId,String token,String from, IHttpRequestCallback callBack){
//		OpenLoginProtocolHandler handler = new OpenLoginProtocolHandler(mContext, callBack);
//		handler.addToken(userId, token, from);
//	}
//	public void deleteToken(String userId,String token,IHttpRequestCallback callBack){
//		OpenLoginProtocolHandler handler = new OpenLoginProtocolHandler(mContext, callBack);
//		handler.deleteToken(userId,token);
//	}
//	public void findToken(String userId, String token,IHttpRequestCallback callBack){
//		OpenLoginProtocolHandler handler = new OpenLoginProtocolHandler(mContext, callBack);
//		handler.isTokenExist(userId,token);
//	}
//	public void updateToken(String userId,String token,String from, IHttpRequestCallback callBack){
//		OpenLoginProtocolHandler handler = new OpenLoginProtocolHandler(mContext, callBack);
//		handler.updateToken(userId, token, from);
//	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
