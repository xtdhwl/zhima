/* 
 * @Title: AppLaunchService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.zhima.R;
import com.zhima.base.config.ResourceServerConfig;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.TargetType;
import com.zhima.base.logger.Logger;
import com.zhima.base.mobile.SystemInfo;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.AppLaunchProtocolHandler.AppStartupProtocolHandler;
import com.zhima.base.protocol.AppLaunchProtocolHandler.AutoUpgradeProtocolHandler;
import com.zhima.base.protocol.AppLaunchProtocolHandler.CheckDictProtocolHandler;
import com.zhima.base.protocol.AppLaunchProtocolHandler.GetServerConfigProtocolHandler;
import com.zhima.base.protocol.FeedbackProtocolHandler.AddFeedbackProtocolHandler;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.model.AppVersion;
import com.zhima.data.model.LaunchUploadInfo;
import com.zhima.data.model.ServerConfigInfo;
import com.zhima.data.model.ZMDictTimestamp;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: AppLaunchService
 * @Description: app启动后服务
 * @author liubingsr
 * @date 2012-9-22 下午3:05:45
 * 
 */
public class AppLaunchService extends BaseService {
	private final static String TAG = "AppLaunchService";
	private final static String VERSION_SPLITCH = "\\.";
	private final static String VERSION_SPLITCH_DOT = ".";
	private final static String ITEM_SPLITCH = ";";
	private final static String KEY_SPLITCH = "#";
	protected final static int NO_SYNC_MSG = 0;
	private static AppLaunchService mInstance = null;
	private String mVersionName = "";
	private Handler mHandler;	
	/**
	 * rest网址
	 * 注意：这个地址是动态的
	 */
//	private String mRestRoot = "";

	private AppLaunchService(Context context) {		
		super(context);		
		String pkName = mContext.getPackageName();
		try {
			mVersionName = mContext.getPackageManager().getPackageInfo(pkName, PackageManager.GET_ACTIVITIES).versionName;
		} catch (NameNotFoundException e) {
			mVersionName = "";
		}
		if (TextUtils.isEmpty(mVersionName)) {
			mVersionName = mContext.getString(R.string.version);			
		}
		initResourcePath();
	}

	public static AppLaunchService getInstance(Context context) {
		if (mInstance == null) {			
			mInstance = new AppLaunchService(context);
		}
		//mInstance.mContext = context;
		return mInstance;
	}
	public void initResourcePath() {
		String paths = SettingHelper.getString(mContext, SettingHelper.Field.RESOURCE_PATH, "");
		if (!TextUtils.isEmpty(paths)) {
			String[] itemArray = paths.split(ITEM_SPLITCH);
			if (itemArray != null && itemArray.length > 0) {
				ResourceServerConfig instance = ResourceServerConfig.getInstance();
				for (String item : itemArray) {
					String[] keyArray = item.split(KEY_SPLITCH);
					if (keyArray != null && keyArray.length > 1) {
						instance.addServerUrl(keyArray[0], keyArray[1]);
					}
				}
			}
		}
	}
	/**
	* @Title: getVersionName
	* @Description: 版本号
	* @return
	* String
	*/
	public String getVersionName() {
		return mVersionName;
	}
	public String getFixedServerUrl() {
		return SettingHelper.getString(mContext, Field.FIXED_SERVER_URL, SystemConfig.FIXED_SERVER_URL);
	}
	public void setFixedServerUrl(String serverUrl) {
		if (!TextUtils.isEmpty(serverUrl)) {
			SettingHelper.setString(mContext, Field.FIXED_SERVER_URL, serverUrl);
		}
	}
	public String getRestRoot() {
		return SettingHelper.getString(mContext, Field.SERVER_REST_ROOTPATH, SystemConfig.FIXED_SERVER_URL);
	}
	public void setRestRoot(String restRoot) {
		if (!TextUtils.isEmpty(restRoot)) {
			SettingHelper.setString(mContext, Field.SERVER_REST_ROOTPATH, restRoot);
		}
	}
	public String getSSLRestRoot() {
		return SettingHelper.getString(mContext, Field.SERVER_SSL_REST_ROOTPATH, SystemConfig.FIXED_SERVER_URL);
	}
	public void setSSLRestRoot(String sslRestRoot) {
		if (!TextUtils.isEmpty(sslRestRoot)) {
			SettingHelper.setString(mContext, Field.SERVER_SSL_REST_ROOTPATH, sslRestRoot);
		}
	}
	public void setContext(Context context) {
		mContext = context;
	}

	public String getPublishChannel() {
		return SystemInfo.getPublishChannel(mContext);
//		String channel = SettingHelper.getString(mContext, SettingHelper.Field.PUBLISH_CHANNEL, "");
//		if (TextUtils.isEmpty(channel)) {
//			parseChannelText();
//			channel = SettingHelper.getString(mContext, SettingHelper.Field.PUBLISH_CHANNEL, "");
//		}
//		return channel;
	}
	
	private void parseChannelText() {
        String channel = "";        
        try {
            InputStream input = mContext.getResources().getAssets().open("channel.txt");	            
            channel = FileHelper.convertStreamToString(input, "UTF-8");
         } catch (IOException e) {
        	 Logger.getInstance(TAG).debug(e.getMessage(), e);
        } catch (Exception e) {
        } finally {
        	SettingHelper.setString(mContext, SettingHelper.Field.PUBLISH_CHANNEL, channel);
        }
    }
    
	public void appInit(IHttpRequestCallback callBack) {
//		Account accountInfo = AccountService.getInstance(mContext).getAccountObject();
//		boolean isFirstStartup = accountInfo.isFirstStartup();
//		LaunchUploadInfo info = new LaunchUploadInfo();
//		info.setPlatform(SystemConfig.PLATFORM);
//		info.setPlatformVersion(SystemInfo.getSDKVersion());
//		info.setPhoneModel(SystemInfo.getProductModel());
//		info.setInstalledVersion(createAppVersion());
//		info.setIMEI(SystemInfo.getIMEI(mContext));
//		info.setIMSI(SystemInfo.getIMSI(mContext));
//		info.setPublishChannel(getPublishChannel());
		appStartupInit(getLaunchUploadInfo(), callBack);
		// if (isFirstStartup) {
		// appStartupInit(info, callBack);
		// } else {
		// // TODO 非首次启动，请求另一个接口
		// LoginService.getInstance(mContext).login(accountInfo.getAccount(),
		// accountInfo.getPassword(), new LoginHttpRequestCallback());
		// }
	}
	
	public LaunchUploadInfo getLaunchUploadInfo() {
		//TODO
		LaunchUploadInfo info = new LaunchUploadInfo();
		info.setPlatform(SystemConfig.PLATFORM);
		info.setPlatformVersion(SystemInfo.getSDKVersion());
		info.setPhoneModel(SystemInfo.getProductModel());
		info.setInstalledVersion(createAppVersion());
		info.setIMEI(SystemInfo.getIMEI(mContext));
		info.setIMSI(SystemInfo.getIMSI(mContext));
		info.setPublishChannel(getPublishChannel());
		return info;
	}

	private String toServerVersion() {
		return mVersionName;
//		String version = mVersionName;
//		int pos = mVersionName.lastIndexOf(VERSION_SPLITCH_DOT);
//		if (pos > 0) {
//			version = mVersionName.substring(0, pos);
//		}
//		return version;
	}
	/**
	* @Title: getServerConfig
	* @Description: 获取服务器配置
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getServerConfig(IHttpRequestCallback callBack) {				
		GetServerConfigProtocolHandler protocol = new GetServerConfigProtocolHandler(mContext, callBack);
		protocol.getConfig(SystemConfig.PLATFORM, toServerVersion());
	}
	/**
	* @Title: canUpgrade
	* @Description: 是否可以升级。当服务端app的版本号比本机app新时，可以升级
	* @param newVersion 服务器端返回的app版本信息
	* @return
	* boolean
	*/
	public boolean canUpgrade(AppVersion newVersion) {
//		return true;
		if (TextUtils.isEmpty(mVersionName)) {
			return true;
		}
		String verString = newVersion.getMajorVersion() + VERSION_SPLITCH_DOT + newVersion.getMinorVersion() + VERSION_SPLITCH_DOT + newVersion.getRevision();
		String localVersion = toServerVersion();
		return !mVersionName.equals(verString);
//		String[] verArray = mVersionName.split(VERSION_SPLITCH);
//		if (verArray != null && verArray.length > 0) {
//			int num = Integer.parseInt(verArray[0]);
//			if (num < newVersion.getMajorVersion()) {
//				return true;
//			}
//			if (verArray.length > 1) {
//				num = Integer.parseInt(verArray[1]);
//				if (num < newVersion.getMinorVersion()) {
//					return true;
//				}
//				if (verArray.length > 2) {
//					try {
//						num = Integer.parseInt(verArray[2]);
//						if (num < newVersion.getBuildNumber()) {
//							return true;
//						}
//					} catch(NumberFormatException ex) {
//						return false;
//					}
//				}
//			}
//		} else {
//			return true;
//		}
//		return false;
	}
	/**
	* @Title: checkUpdate
	* @Description: 检查版本更新信息
	* @param callBack 结果通知回调
	* @return void
	*/
	public void checkAppUpdate(IHttpRequestCallback callBack) {
		AutoUpgradeProtocolHandler protocol = new AutoUpgradeProtocolHandler(mContext, callBack);
		protocol.checkUpdate(SystemConfig.PLATFORM, toServerVersion());
	}
	/**
	* @Title: checkDictUpdate
	* @Description: 检查字典是否有更新
	* @param callBack 结果通知回调
	* @return void
	*/
	public void checkDictUpdate(IHttpRequestCallback callBack) {
		CheckDictProtocolHandler protocol = new CheckDictProtocolHandler(mContext, callBack);
		protocol.getDictLastTimestamp();
	}
	/**
	* @Title: addFeedback
	* @Description: 提交意见反馈信息
	* @param content 意见内容
	* @param callBack 结果通知回调
	* @return void
	*/
	public void addFeedback(String content, IHttpRequestCallback callBack) {
		AddFeedbackProtocolHandler protocol = new AddFeedbackProtocolHandler(mContext, callBack);
		protocol.addFeedback(content);
	}
	/**
	* @Title: processServerConfig
	* @Description: 服务信息
	* @param cfg 最新配置信息
	* @return void
	*/
	public void processServerConfig(ServerConfigInfo cfg) {		
		if (cfg != null) {
			String restRoot = cfg.getRestRootUrl();
			if (!restRoot.endsWith("/")) {
				restRoot += "/";
			}
			setRestRoot(restRoot);
			savePingInterval(cfg.getPingInterval());
			updateResourcePath(cfg.getResourceRootPathMap());
		}
	}
	/**
	* @Title: savePingInterval
	* @Description: 保存心跳间隔时间值
	* @param seconds
	* @return void
	*/
	private void savePingInterval(int seconds) {
		PingService.getInstance(mContext).updatePingInterval(seconds);
	}
	/**
	* @Title: updateResourcePath
	* @Description: 更新资源服务器地址
	* @param pathMap
	* @return void
	*/
	private void updateResourcePath(Map<String, String> pathMap) {
		ResourceServerConfig instance = ResourceServerConfig.getInstance();
		Set<String> keys = pathMap.keySet();
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			instance.addServerUrl("{" + key + "}", pathMap.get(key));
			sb.append("{" + key + "}" + KEY_SPLITCH + pathMap.get(key) + ITEM_SPLITCH);
		}
		SettingHelper.setString(mContext, SettingHelper.Field.RESOURCE_PATH, sb.substring(0,  sb.length() - 1));
	}
	/**
	 * @Title: appStartupInit
	 * @Description: app安装以后第一次启动时上传信息
	 * @param callBack 结果通知回调
	 * @return void
	 */
	private void appStartupInit(LaunchUploadInfo info, IHttpRequestCallback callBack) {
		AppStartupProtocolHandler protocol = new AppStartupProtocolHandler(mContext, callBack);
		protocol.appStartup(info, true, true);
	}
//
//	/**
//	 * @Title: appLaunchUpload
//	 * @Description: app启动上传信息
//	 * @param info 要上传的信息
//	 * @param callBack 结果通知回调
//	 * @return void
//	 */
//	private void appLaunchUpload(LaunchUploadInfo info, IHttpRequestCallback callBack) {
//		AppStartupProtocolHandler protocol = new AppStartupProtocolHandler(mContext, callBack);
//		protocol.appStartup(info, false, true);
//	}

	private AppVersion createAppVersion() {
		AppVersion appVersion = new AppVersion();
//		if (TextUtils.isEmpty(mVersionName)) {
//			mVersionName = mContext.getString(R.string.version);			
//		}
		String[] versionArray = mVersionName.split(VERSION_SPLITCH);
		if (versionArray != null) {
			if (versionArray.length > 0) {
				appVersion.setMajorVersion(Integer.parseInt(versionArray[0]));
				if (versionArray.length > 1) {
					appVersion.setMinorVersion(Integer.parseInt(versionArray[1]));
					if (versionArray.length > 2) {
						try {
							appVersion.setRevision(Integer.parseInt(versionArray[2]));
						} catch(NumberFormatException e) {
							appVersion.setRevision(0);
						}
						if (versionArray.length > 3) {
							appVersion.setBuildNumber(versionArray[3]);
						}
					}
				}
			}
		}
		appVersion.setPlatform(SystemConfig.PLATFORM);
		return appVersion;
	}

	public void syncDict(List<ZMDictTimestamp> timestamp) {
		new SyncDickTask().execute(timestamp);		
	}

	/**
	 * @ClassName: SyncDickTask
	 * @Description: 同步字典任务
	 * @author liubingsr
	 * @date 2012-9-25 下午12:22:42
	 * 
	 */
	public final class SyncDickTask extends AsyncTask<List<ZMDictTimestamp>, Void, Void> {
		@Override
		protected Void doInBackground(List<ZMDictTimestamp>... params) {
			boolean hasSyn = false;
			long lastTimestamp = 0;
			List<ZMDictTimestamp> timeStampList = params[0];
			for (ZMDictTimestamp timeStamp : timeStampList) {
				switch(timeStamp.getDictType()) {
				case TargetType.TARGET_TYPE_SPACE_KIND:
					lastTimestamp = SettingHelper.getLong(mContext, SettingHelper.Field.DICT_SPACE_TIMESTAMP, 0);
					if (timeStamp.getTimestamp() > lastTimestamp) {
						hasSyn = true;
						SpaceKindService.getInstance(mContext).sync(timeStamp.getTimestamp(), new SpaceKindHttpRequestCallback());
					}
					break;
				case TargetType.TARGET_TYPE_CITY:
					lastTimestamp = SettingHelper.getLong(mContext, SettingHelper.Field.DICT_CITY_TIMESTAMP, 0);
					if (timeStamp.getTimestamp() > lastTimestamp) {
						hasSyn = true;
						RegionService.getInstance(mContext).sync(timeStamp.getTimestamp(), new RegionHttpRequestCallback());
					}
					break;
				case TargetType.TARGET_TYPE_IDOL_JOB:
					lastTimestamp = SettingHelper.getLong(mContext, SettingHelper.Field.DICT_IDOL_JOB_TIMESTAMP, 0);
					if (timeStamp.getTimestamp() > lastTimestamp) {
						hasSyn = true;
						IdolJobService.getInstance(mContext).sync(timeStamp.getTimestamp(), new IdolJobDictHttpRequestCallback());
					}
					break;
				}
			}
//			if (!hasSyn) {
//				Message msg = getMainThreadHandler().obtainMessage(NO_SYNC_MSG, 0, 0);
//				getMainThreadHandler().sendMessage(msg);
//			}
			return null;
		}
	}
	
	private Handler getMainThreadHandler() {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case NO_SYNC_MSG:
						HaloToast.show(mContext, R.string.no_sync_data, 0);
						break;
					default:
						break;
					}
				}
			};
		}
		return mHandler;
	}
	
	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
		clear();
		System.gc();
	}

	@Override
	public void clear() {
	}
}
