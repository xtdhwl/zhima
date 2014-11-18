package com.zhima.ui.share;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.Renren.RenrenAuth;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.photos.PhotoHelper;
import com.renren.api.connect.android.photos.PhotoUploadRequestParam;
import com.renren.api.connect.android.photos.PhotoUploadResponseBean;
import com.renren.api.connect.android.status.StatusSetRequestParam;
import com.renren.api.connect.android.status.StatusSetResponseBean;
import com.renren.api.connect.android.view.RenrenAuthListener;
import com.renren.api.connect.android.view.RenrenDialogListener;
import com.zhima.R;
import com.zhima.base.logger.Logger;
import com.zhima.base.utils.NetUtils;
import com.zhima.ui.common.view.HaloToast;

/**
 * 人人分享
 * @ClassName: RenRenSpaceShare
 * @Description: TODO
 * @author yusonglin
 * @date 2012-11-22 下午4:06:38
*/
public class RenRenSpaceShare {
	
	private static final String TAG = "RenRenSpaceShare";

	private static final String API_KEY = "b33960654b4941bbac83ef507c26b5bc";
	private static final String SECRET_KEY = "f2651830a728447bb232d2173d25c1ce";
	private static final String APP_ID = "207282";
	
	private Renren renren;
	private RenrenAuthListener mRenrenAuthListener;
	
	private boolean isHaveSend;
	private boolean isNoPicShare = true;
	private String mContent;
	private String mPicPath ;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {  
			case 0:
				if(isHaveSend){
					if(isNoPicShare){
						sendWithNoPic(mContent);
					}else{
						sendWithPic(mContent, mPicPath);
					}
					isHaveSend = false;
				}
				if(mRenrenLoginListener!=null){
					mRenrenLoginListener.login(true);
				}
				if(mLoginStatusListener!=null){
					mLoginStatusListener.login(true);
				}
				break;
			case 1:
				if(mRenrenLoginListener!=null){
					mRenrenLoginListener.login(false);
				}
				if(mLoginStatusListener!=null){
					mLoginStatusListener.login(false);
				}
				break;
			}
		}
	};
	
	private static RenRenSpaceShare mRenRenSpaceShare;

	private static RenrenDialogListener mListener;
	private Context mContext;
	private Activity mActivity;
	private SendRenrenListener mSendListener;

	private RenrenLoginListener mRenrenLoginListener;

	private LoginStatusListener mLoginStatusListener;
	
	private RenRenSpaceShare(){
		
	}
	
	private RenRenSpaceShare(Context context,Activity activity){
		this.mContext = context;
		this.mActivity = activity;
		
		isHaveSend = false;
		
		renren = new Renren(API_KEY, SECRET_KEY, APP_ID, mContext);
		renren.setRenrenAuth(new RenrenAuth() {
			
			@Override
			public void auth(String arg0, RenrenDialogListener listener) {
				mListener = listener;
				Intent intent = new Intent(mContext,RenrenLoginActivity.class);
				intent.putExtra("url", arg0);
				mContext.startActivity(intent);
			}
		});
		mRenrenAuthListener = new RenrenAuthListener() {

			@Override
			public void onComplete(Bundle values) {
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onCancelLogin() {
			}

			@Override
			public void onCancelAuth(Bundle values) {
			}
		};
	}
	
	public static RenRenSpaceShare getInstance(Context context,Activity activity){
		if(mRenRenSpaceShare==null){
			mRenRenSpaceShare = new RenRenSpaceShare(context,activity);
		}
		return mRenRenSpaceShare;
	}
	
	
	public void sendWithNoPic(String content){
		isNoPicShare = true;
		if(!renren.isAccessTokenValid()){
			this.mContent = content;
			isHaveSend = true;
			login();
		}else{
			StatusSetRequestParam param = new StatusSetRequestParam(content);
			StatusSetListener listener = new StatusSetListener(mContext);
			
			try {
				AsyncRenren aRenren = new AsyncRenren(renren);
				aRenren.publishStatus(param, listener, // 对结果进行监听
						true); // 若超过140字符，则自动截短
				
				if(mSendListener!=null){
					mSendListener.sendBefore();
				}
			} catch (Throwable e) {
				String errorMsg = e.getMessage();
			}
		}
	}
	
	public void sendWithPic(String content,String picPath) {
		isNoPicShare = false;
		if(!renren.isAccessTokenValid()){
			this.mContent = content;
			this.mPicPath = picPath;
			isHaveSend = true;
			login();
		}else{
			File file = null;
			try {
				file = new File(Environment.getExternalStorageDirectory(),"renren_1346649436061.png");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/**
			 * 上传的照片请求参数实体
			 */
			PhotoUploadRequestParam photoParam = new PhotoUploadRequestParam();
			
			photoParam.setCaption(content);
			photoParam.setFile(file);
			
			PhotoHelper photoHelper = new PhotoHelper(renren);
			
			photoHelper.asyncUploadPhoto(photoParam,new AbstractRequestListener<PhotoUploadResponseBean>() {
					
						@Override
						public void onRenrenError(final RenrenError renrenError) {
							if(mSendListener!=null){
								mSendListener.sendAfter();
							}
							if (renrenError != null) {
								new Handler(mActivity.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										Logger.getInstance(TAG).debug("onRenrenError : "+renrenError.getMessage());
										HaloToast.show(mContext,"分享失败");
									}
								});
							}
						}

						@Override
						public void onFault(final Throwable fault) {
							if(mSendListener!=null){
								mSendListener.sendAfter();
							}
							if (fault != null) {
								new Handler(mActivity.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										Logger.getInstance(TAG).debug("onFault : "+fault.getMessage());
										HaloToast.show(mContext,"分享失败");
									}
								});
							}
						}

						@Override
						public void onComplete(final PhotoUploadResponseBean photoResponse) {
							if(mSendListener!=null){
								mSendListener.sendAfter();
							}
							
							if (photoResponse != null) {

								new Handler(mActivity.getMainLooper()).post(new Runnable() {
									@Override
									public void run() {
										HaloToast.show(mContext,"分享成功");
									}
								});

							}
						}
					});
			if(mSendListener!=null){
				mSendListener.sendBefore();
			}
		}
	}
	
	public void login(){
		if(!NetUtils.isNetworkAvailable(mContext)){
			HaloToast.show(mContext, R.string.network_request_failed);
			return;
		}
		renren.authorize(mActivity, null, mRenrenAuthListener, 1);
	}
	
	public void logout(){
		renren.logout(mContext);
	}
	
	public boolean isLogin(){
		return renren.isAccessTokenValid();
	}
	
	public static RenrenDialogListener getRenrenAuthListener(){
    	return mListener;
    }
	
	private void resumeInit() {
		renren.init(mContext);
	}
	
	private void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (renren != null) {
			renren.authorizeCallback(requestCode, resultCode, data);
		}
	}
	
	/**
	 * 监听异步调用分享状态接口的响应
	 * 
	 * @author Shaofeng Wang (shaofeng.wang@renren-inc.com)
	 */
	private class StatusSetListener extends
			AbstractRequestListener<StatusSetResponseBean> {

		private Context context;

		private Handler handler;

		public StatusSetListener(Context context) {
			this.context = context;
			this.handler = new Handler(context.getMainLooper());
		}

		@Override
		public void onRenrenError(RenrenError renrenError) {
			if(mSendListener!=null){
				mSendListener.sendAfter();
			}
			final int errorCode = renrenError.getErrorCode();
			final String errorMsg = renrenError.getMessage();
			handler.post(new Runnable() {
				@Override
				public void run() {
					Logger.getInstance(TAG).debug("onRenrenError : "+errorMsg);
					if (errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
						HaloToast.show(context, "分享被取消");
					} else {
						HaloToast.show(context, "分享失败");
					}
				}
			});
		}

		@Override
		public void onFault(Throwable fault) {
			if(mSendListener!=null){
				mSendListener.sendAfter();
			}
			final String errorMsg = fault.toString();
			handler.post(new Runnable() {
				@Override
				public void run() {
					Logger.getInstance(TAG).debug("onFault : "+errorMsg);
					HaloToast.show(context, "分享失败");
				}
			});
		}

		@Override
		public void onComplete(StatusSetResponseBean bean) {
			if(mSendListener!=null){
				mSendListener.sendAfter();
			}
			final String responseStr = bean.toString();
			handler.post(new Runnable() {
				

				@Override
				public void run() {
					Logger.getInstance(TAG).debug("onComplete : "+responseStr);
					HaloToast.show(context, "分享成功");
				}
			});
		}
	}
	
	public interface SendRenrenListener{
		void sendBefore();
		void sendAfter();
	}
	
	public void setSendListener(SendRenrenListener sendListener){
		this.mSendListener = sendListener;
	}
	
	public interface RenrenLoginListener{
		void login(boolean isSuccess);
	}
	
	public void setRenrenLoginListener(RenrenLoginListener renrenLoginListener){
		this.mRenrenLoginListener = renrenLoginListener;
	}
	
	public interface LoginStatusListener{
		void login(boolean isSuccess);
	}
	
	public void setLoginStatusListener(LoginStatusListener loginStatusListener){
		this.mLoginStatusListener = loginStatusListener;
	}
}
