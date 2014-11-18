package com.zhima.ui.share;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;
import com.zhima.R;
import com.zhima.base.logger.Logger;
import com.zhima.base.utils.NetUtils;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.share.sina.AccessTokenKeeper;
import com.zhima.ui.share.sina.StatusesAPI;
import com.zhima.ui.share.sina.WeiboLoginActivity;

/**
 * 新浪微博分享
 * @ClassName: SinaWeiboShare
 * @Description: TODO
 * @author yusonglin
 * @date 2012-11-21 下午6:40:51
*/
public class SinaWeiboShare implements RequestListener{

	private static final String TAG = "SinaWeiboShare";
	
//	public static final int SINA_LOGIN = 0;
//	public static final int SINA_SHARE_BIND = 1;
	
	private Weibo mWeibo;
	
	private String mAccessToken = "";
	
	private static final String CONSUMER_KEY = "3956858501";// 替换为开发者的appkey，例如"1646212860";
	private static final String REDIRECT_URL = "http://www.zhima.net";
	
	public static Oauth2AccessToken accessToken ;
	
	public static final int WEIBO_MAX_LENGTH = 140;
	
	private Context mContext;
	private Activity mActivity;

	private long errorCode; 
	private static SinaWeiboShare mSinaWeiboShare;
	
	private SendWeiboListener mSendWeiboListener;

	private SinaLoginListener mSinaLoginListener;
	
	private boolean isHaveSend;
	private String mContent;
	private String mPicPath;
	
	private SinaWeiboShare(){
		
	}
	
	private SinaWeiboShare(Context context,Activity activity){
		this.mActivity = activity;
		this.mContext = context;
		
		isHaveSend = false;
		
		mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
		accessToken = AccessTokenKeeper.readAccessToken(mContext);
		mAccessToken = accessToken.getToken();
	}
	
	public static SinaWeiboShare getInstance(Context context,Activity activity){
		if(mSinaWeiboShare == null){
			mSinaWeiboShare = new SinaWeiboShare(context,activity);
		}
		return mSinaWeiboShare;
	}
	
	public void setSendWeiboListener(SendWeiboListener sendWeiboListener){
		this.mSendWeiboListener = sendWeiboListener;
	}
	
	public void login() {
		
		if(!NetUtils.isNetworkAvailable(mContext)){
			HaloToast.show(mContext, R.string.network_request_failed);
			return;
		}
		
		Intent intent = new Intent(mContext,WeiboLoginActivity.class);
		intent.putExtra("app_key", CONSUMER_KEY);
		intent.putExtra("redirecturl", REDIRECT_URL);
		mContext.startActivity(intent);
//		mActivity.startActivityForResult(intent, requestCode);
//		mWeibo.authorize(mContext, new AuthDialogListener());
	}
	
	public void logout(){
		AccessTokenKeeper.clear(mContext);
		accessToken = AccessTokenKeeper.readAccessToken(mContext);
	}
	
	public boolean isLogin(){
		return accessToken.isSessionValid();
	}
	
	public void sendWeibo(String content,String picPath){
		StatusesAPI api = new StatusesAPI(accessToken);
		if (accessToken.isSessionValid()) {
			if(TextUtils.isEmpty(content)){
				HaloToast.show(mContext, "请输入内容",Toast.LENGTH_LONG);
			    return;
			}
			if (!TextUtils.isEmpty(picPath)) {
				api.upload( content, picPath, "", "", this);
			} else {
				api.update( content, "", "", this);
			}
			if(mSendWeiboListener!=null){
				mSendWeiboListener.sendBefore();
			}
		} else {
			this.mContent = content;
			this.mPicPath = picPath;
			isHaveSend = true;
			login();
		}
	}
	
	@Override
	public void onComplete(String arg0) {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				HaloToast.show(mContext,R.string.share_success,0);
				if(mSendWeiboListener!=null){
					mSendWeiboListener.sendAfter();
				}
			}
		});
	}

	@Override
	public void onError(final WeiboException e) {
		
		Logger.getInstance(TAG).debug(TAG+" : "+e);
		String message = e.getMessage();
		String jsonStr = message.substring(message.indexOf("{"));
		Logger.getInstance(TAG).debug(TAG+"_jsonStr : "+jsonStr);
		
		try {
			JSONObject json = new JSONObject(jsonStr);
			errorCode = json.getLong("error_code");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Logger.getInstance(TAG).debug(TAG+"_errorCode : "+errorCode);
		
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(21327 == errorCode){
					HaloToast.show(mContext,"分享失败,请重新登录");
					login();
				}else if(20019 == errorCode){
					HaloToast.show(mContext,"内容重复");
				}else if(20012 == errorCode){
					HaloToast.show(mContext,"分享失败,内容超过140字");
				}else if(e.getMessage().contains("ConnectTimeoutException")){
					HaloToast.show(mContext,"连接服务器超时");
				}else{
					HaloToast.show(mContext,"分享失败,请稍后再试");
				}
			}
		});
		if(mSendWeiboListener!=null){
			mSendWeiboListener.sendAfter();
		}
	}

	@Override
	public void onIOException(IOException arg0) {
		if(mSendWeiboListener!=null){
			mSendWeiboListener.sendAfter();
		}
	}
	
	public WeiboAuthListener getWeiboAuthListener(){
		return new AuthDialogListener();
	}
	
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values,String returnUrl) {
			String uid = values.getString("uid");
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			accessToken = new Oauth2AccessToken(token, expires_in);
			mAccessToken = accessToken.getToken();
			if (accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(accessToken.getExpiresTime()));
				AccessTokenKeeper.keepAccessToken(mContext, accessToken);
				if(isHaveSend){
					sendWeibo(mContent, mPicPath);
					isHaveSend = false;
				}
				if(mSinaLoginListener!=null){
					mSinaLoginListener.login(true, uid, returnUrl);
				}
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			if(mSinaLoginListener!=null){
				mSinaLoginListener.login(false,null,null);
			}
			HaloToast.show(mContext, "新浪微博绑定失败");
			Logger.getInstance(TAG).debug("Auth error : " + e.getMessage());
		}

		@Override
		public void onCancel() {
			if(mSinaLoginListener!=null){
				mSinaLoginListener.login(false,null,null);
			}
			HaloToast.show(mContext, "新浪微博绑定失败");
			Logger.getInstance(TAG).debug("Auth cancel");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			if(mSinaLoginListener!=null){
				mSinaLoginListener.login(false,null,null);
			}
			HaloToast.show(mContext, "新浪微博绑定失败");
			Logger.getInstance(TAG).debug("Auth exception : " + e.getMessage());
		}
	}
	
	public interface SendWeiboListener{
		void sendBefore();
		void sendAfter();
	}
	
	public interface SinaLoginListener{
		void login(boolean isSuccess,String uid,String returnUrl);
	}
	
	public void setSinaLoginListener(SinaLoginListener sinaLoginListener){
		this.mSinaLoginListener = sinaLoginListener;
	}
}
