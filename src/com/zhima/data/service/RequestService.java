/* 
 * @Title: RequestService.java
 * Created by liubingsr on 2012-5-21 上午11:51:31 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.zhima.app.ZhimaApplication;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpNetwork;
import com.zhima.base.network.HttpProcessData;
import com.zhima.base.network.IHttpProcessNotify;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.DownloadUpdateFileProtocolHandler.DownloadUpdateApkProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.task.ITask;
import com.zhima.base.task.TaskManager;
import com.zhima.ui.setting.activity.SettingMainActivity;
import com.zhima.ui.tools.DownLoadNotification;

/**
 * @ClassName: RequestService
 * @Description: Http处理
 * @author liubingsr
 * @date 2012-6-11 下午3:41:02
 * 
 */
public class RequestService {
	private final static String TAG = "RequestService";
	private static final int HTTP_PROCESS_MSG = 3;
	private static RequestService mInstance = null;
	private TaskManager mHttpTaskManager;
	private IHttpRequestCallback mCallback;
	private Context mContext;
	private Handler mHandler;
	private RequestInfo mDownLoadRequestInfo = null;	
	private DownLoadNotification mDownLoadNotification;
	private Activity mActivity;	
	
	private RequestService(Context context) {
		mContext = ZhimaApplication.getContext();
		mHttpTaskManager = new TaskManager(SystemConfig.MAX_TASK_SIZE);
		onCreate();
	}
	
	public static RequestService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new RequestService(context);
		}
		return mInstance;
	}
	public void onCreate() {
		
	}
	public void bindCallback(IHttpRequestCallback callback) {
		mCallback = callback;
	}

	public void execute(ProtocolHandlerBase protocol) {
		if (!protocol.isDeleted()) {
			new HttpRequestTask(protocol).execute(protocol);
		} else {
			mHttpTaskManager.removeTask(protocol);
		}
	}

	public void sendRequest(ProtocolHandlerBase protocol) {
		callbackStart(protocol);
		mHttpTaskManager.addTask(protocol);
	}

	private Handler getMainThreadHandler() {
		if (mHandler == null)
			mHandler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case HTTP_PROCESS_MSG:
						if (msg.arg1 % 10 == 0) {
							if (msg.arg1 > 100) {
								// 更新状态栏进度
								setFinishIntent();
							} else {
								// 更新状态栏进度
								mDownLoadNotification.setProgress(msg.arg1);
							}
							if (msg.arg1 == 100) {
								// 文件下载完毕
								setFinishIntent();
							}
						}
						break;
					default:
						break;
					}
				}
			};
		return mHandler;
	}
	
	private void setFinishIntent(){
		String oldFilePath = mDownLoadRequestInfo.getDownFilePath();
		String apkFilePath = oldFilePath.replace(".tmp", "");
		boolean ret = FileHelper.rename(oldFilePath, apkFilePath);
		
		if (ret) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
			mContext.startActivity(intent);
		}
		mDownLoadNotification.cancel();		
		if (mActivity != null) {
			((SettingMainActivity)mActivity).setDownstate(false);
		}
	}
	
	/**
	* @Title: downLoadUpdateFile
	* @Description: 下载文件
	* @param url 要下载的文件url
	* @param callBack 结果通知回调
	* @return void
	*/
	public void downLoadUpdateFile(Activity activity,DownLoadNotification downLoadNotification,String url, IHttpRequestCallback callBack) {
		this.mActivity = activity;
		this.mDownLoadNotification = downLoadNotification;
		if (TextUtils.isEmpty(url)) {
			return;
		}
		DownloadUpdateApkProtocolHandler protocol = new DownloadUpdateApkProtocolHandler(mContext, callBack);
		downAppFile(protocol, url);
	}
	
	private void downAppFile(ProtocolHandlerBase protocol, String url) {
		mDownLoadRequestInfo  = new RequestInfo(url);
		Logger.getInstance(TAG).debug("down file<" + url + ">");
		String fileName = SystemConfig.UPDATE_APK + ".tmp";
		mDownLoadRequestInfo.setDownloadFile(FileHelper.getRootDir() + fileName);
		mDownLoadRequestInfo.setRequestType(RequestType.DOWNLOAD);
		protocol.setRequestInfo(mDownLoadRequestInfo);
		protocol.setProtocolType(ProtocolType.DOWN_FILE_PROTOCOL);
		new HttpRequestTask(protocol).execute(protocol);
	}
	/**
	 * @ClassName: HttpRequestTask
	 * @Description: AsyncTask任务
	 * @author liubingsr
	 * @date 2012-5-21 下午12:15:09
	 * 
	 */
	public class HttpRequestTask extends
			AsyncTask<ProtocolHandlerBase, HttpProcessData, ProtocolHandlerBase>
			implements IHttpProcessNotify {
		private ProtocolHandlerBase mProtocol = null;
		
		public HttpRequestTask(ProtocolHandlerBase protocol) {
//			mProtocol = protocol;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();			
		}
		
		@Override
		protected ProtocolHandlerBase doInBackground(ProtocolHandlerBase... protocol) {
			ProtocolHandlerBase protocolHandler = protocol[0];
			try {
				HttpNetwork httpNetwork = new HttpNetwork(mContext);
				RequestInfo info = protocolHandler.getRequestInfo();
				info.setProcessNotifyCallback(this);
				httpNetwork.sendRequest(info);				
			} catch(Exception e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			} finally {
//				mHttpTaskManager.removeTask(protocolHandler);
			}
			return protocolHandler;
		}

		@Override
		protected void onPostExecute(ProtocolHandlerBase protocol) {
			if (protocol != null) {
				RequestInfo info = protocol.getRequestInfo();
				if (info != null) {
					protocol.setHttpResultCode(info.getResultCode());
					if (info.getResultCode() == ErrorManager.NO_ERROR) {
						if (protocol.getProtocolType() == ProtocolType.LOGIN_PROTOCOL) {
							AccountService.getInstance(mContext).setZMAccessToken("");
						}
						protocol.parseData();
					}
				}
				if (!protocol.isDeleted()) {
					callbackResult(protocol);
				}
				mHttpTaskManager.removeTask(protocol);
			}
			super.onPostExecute(protocol);
		}

		@Override
		public void onProcessChange(HttpProcessData data) {
			Message msg = getMainThreadHandler().obtainMessage(HTTP_PROCESS_MSG, data.getPercentage(), 0);
			getMainThreadHandler().sendMessage(msg);
		}
	}

	private void callbackStart(ProtocolHandlerBase protocol) {
		if (protocol.getHttpRequestCallback() != null) {
			protocol.getHttpRequestCallback().onHttpStart(protocol);
		} else if (mCallback != null) {
			mCallback.onHttpStart(protocol);
		}
	}

	private void callbackResult(ProtocolHandlerBase protocol) {
		if (protocol.getHttpRequestCallback() != null) {
			protocol.getHttpRequestCallback().onHttpResult(protocol);
		} else if (mCallback != null) {
			mCallback.onHttpResult(protocol);
		}
	}

	/**
	 * @Title: deleteByCallback
	 * @Description: 移走Callback
	 * @param callback void
	 */
	public void deleteByCallback(IHttpRequestCallback callback) {
		for (ITask task : mHttpTaskManager.getTaskList()) {
			ProtocolHandlerBase data = (ProtocolHandlerBase) task;
			data.deleteByCallback(callback);
		}
	}
	public void onDestroy() {
		clear();
		mHttpTaskManager.onDestroy();
		System.gc();
	}
	public void clear() {
		if (mHttpTaskManager != null) {
			mHttpTaskManager.clear();
		}
	}
}
