/* 
* @Title: initActivity.java
* @Description: app界面入口
* 
* Created by liubingsr on 2012-5-9 下午5:04:27 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.ui.main.activity;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.LoginType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.AppStartupHttpRequestCallback;
import com.zhima.data.service.CheckDictHttpRequestCallback;
import com.zhima.data.service.DataServiceManager;
import com.zhima.data.service.GetUserInfoHttpRequestCallback;
import com.zhima.data.service.LoginHttpRequestCallback;
import com.zhima.data.service.LoginService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: initActivity
 * @Description: app启动后第一个界面，在此进行一些启动初始化工作。
 * @author liubingsr
 * @date 2012-5-9 下午5:04:27
 *
 */
public class initActivity extends BaseActivity {
	private final static String TAG = "initActivity";
	private final static int OPENACTIVITY_MSG = 1;
	private final static int SHOW_TOAST = OPENACTIVITY_MSG + 1;
	private Context mApplicationContext = null;
	
	private String mSaveVersion;
	private String mCurrentVersion;
	private boolean mIsUpgrade = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_activity);
		mApplicationContext = getApplication().getApplicationContext();
		
		mSaveVersion = SettingHelper.getString(this, Field.VERSION,null);
		mCurrentVersion = AppLaunchService.getInstance(this).getVersionName();
		
		Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_in);
		LinearLayout initLayout = (LinearLayout) this.findViewById(R.id.layout_init);
		initLayout.setAnimation(loadAnimation);
		loadAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				newUpgrade();
				appInit();
				GraphicUtils.mScreenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
				GraphicUtils.mScreenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
				SystemConfig.MAP_KEY = getString(R.string.maps_api_key);
				AppLaunchService.getInstance(ZhimaApplication.getContext()).setContext(ZhimaApplication.getContext());			
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mHandler.sendEmptyMessage(OPENACTIVITY_MSG);
			}
		});
	}
	
	final Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OPENACTIVITY_MSG: 
				openIntent();
				break;
			case SHOW_TOAST: 
				HaloToast.show(initActivity.this, R.string.network_request_failed);
				break;
			}
			super.handleMessage(msg);
		}
	};
	/**
	* @Title: newUpgrade
	* @Description: 升级后首次启动
	* @return boolean
	*/
	private void newUpgrade() {
		String preVersion = SettingHelper.getString(this, Field.VERSION, "");
		if (TextUtils.isEmpty(preVersion) && TextUtils.isEmpty(AccountService.getInstance(this).getAccount())) {
			// 第一个版本 不做处理
			return;
		} else {
			String currentVersion = AppLaunchService.getInstance(this).getVersionName();
			mIsUpgrade = !preVersion.equalsIgnoreCase(currentVersion);
			if (mIsUpgrade) {
				// TODO 升级首次启动，需要进行的处理在此，处理完毕后把当前版本号写入
				SettingHelper.setString(this, Field.VERSION, currentVersion);
			}
		}
	}
	
	private void appInit() {
		new AppInitTask().execute();
	}
	
	public final class AppInitTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			DataServiceManager.getInstance(ZhimaApplication.getContext());
			AppLaunchService.getInstance(ZhimaApplication.getContext()).getServerConfig(initActivity.this);
			return null;
		}
	}
	
	private void openIntent() {
		Intent intent = null;
		if(TextUtils.isEmpty(mSaveVersion) || !mCurrentVersion.equals(mSaveVersion)){
			final File file = new File(FileHelper.getRootDir()+File.separator + SystemConfig.UPDATE_APK);
			if(file.exists()){
				FileHelper.deleteFile(file.getPath());
			}
//			intent = new Intent(this,HelpGuideActivity.class);
//			intent.putExtra("isFirstInstall", true);
		}
		intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		int protocolType = protocol.getProtocolType();
		if (protocol.isHttpSuccess()) {
			// 首次启动协议处理成功
			if (protocolType == ProtocolType.GET_SERVER_CONFIG_PROTOCOL) {
				boolean isFirstStartup = AccountService.getInstance(ZhimaApplication.getContext()).isFirstStartup();
				if (isFirstStartup) {
					AppLaunchService.getInstance(ZhimaApplication.getContext()).appInit(new AppStartupHttpRequestCallback(ZhimaApplication.getContext()));
				} else {
					// 非首次启动
					if (AccountService.getInstance(ZhimaApplication.getContext()).isGuest()) {
						// 是游客
						Logger.getInstance(TAG).debug(AccountService.getInstance(ZhimaApplication.getContext()).getDisplayName() + ":游客身份");
						if (mIsUpgrade) {							
							// 此处调用自动注册
//							AccountService.getInstance(ZhimaApplication.getContext()).setZMAccessToken("");
							LoginService.getInstance(ZhimaApplication.getContext()).registerUserByAuto(new LoginHttpRequestCallback());
						} else {
							// 注册用户
							Logger.getInstance(TAG).debug(AccountService.getInstance(ZhimaApplication.getContext()).getDisplayName() + ":注册用户身份");
							// 取用户信息
							UserService.getInstance(ZhimaApplication.getContext()).getMyself(new GetUserInfoHttpRequestCallback());
						}
					}
					// 检查字典
					AppLaunchService.getInstance(ZhimaApplication.getContext()).checkDictUpdate(new CheckDictHttpRequestCallback());
				}				
			} else {
				// TODO 获取失败
			}
		} else {
			// TODO 网络连接错误
			mHandler.sendEmptyMessage(SHOW_TOAST);
		}
	}
}
