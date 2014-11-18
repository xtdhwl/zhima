package com.zhima.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;
import com.zhima.data.service.DataServiceManager;
import com.zhima.service.CrashHandler;

public class ZhimaApplication extends Application {
	private final static String TAG = "ZhimaApplication";
	private static Context mContext = null;
	private CrashHandler mCrashHandler = null;
	/**
	 * 设置最小堆内存6M
	 */
	private final static int MIN_HEAP_SIZE = 8 * 1024 * 1024;
	private static ConnectivityManager mConnectivityManager = null;

	public long mMainActivityId;
	
	public HashMap<Long, Activity> mActivitys;
	public HashMap<Long, Activity> mLoginActivitys;
	
	/**  */
	public boolean isLbsSucess = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		// VMRuntime.getRuntime().setMinimumHeapSize(MIN_HEAP_SIZE);
		initUmengStat();
		initCrashHandler();
		mActivitys = new HashMap<Long, Activity>();
		mLoginActivitys = new HashMap<Long, Activity>();
		mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		

		WindowManager wm= (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		GraphicUtils.mScreenWidth = wm.getDefaultDisplay().getWidth();
		GraphicUtils.mScreenHeight = wm.getDefaultDisplay().getHeight();
	}

	public static Context getContext() {
		return mContext;
	}

	public static ConnectivityManager getConnectivityManager() {
		return mConnectivityManager;
	}

	/**
	 * @Title: initUmengStat
	 * @Description: 友盟统计参数设置
	 * @return void
	 */
	private void initUmengStat() {
		MobclickAgent.setDebugMode(false);
		com.umeng.common.Log.LOG = false;
	}

	private void initCrashHandler() {
		mCrashHandler = CrashHandler.getInstance();
		mCrashHandler.init(mContext);
	}

	public void popAllActivity() {
		Iterator<Entry<Long, Activity>> iterator = mActivitys.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Long, Activity> entry = iterator.next();
			Activity activity = entry.getValue();
			long activityId = entry.getKey();
			if (mMainActivityId == activityId) {
				continue;
			}
			activity.finish();
			iterator.remove();
		}
	}
	
	public void dismissLoginActivity() {
		//TODO
		Iterator<Entry<Long, Activity>> iterator = mLoginActivitys.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Long, Activity> entry = iterator.next();
			Activity activity = entry.getValue();
			long activityId = entry.getKey();
			if (mMainActivityId == activityId) {
				continue;
			}
			activity.finish();
			iterator.remove();
		}
	}

	/**
	 * @Title: exitApplication
	 * @Description: 完全退出app
	 * @param context
	 *            void
	 */
	public static void exitApplication(Context context) {
		DataServiceManager.getInstance(context).onDestroy();
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startMain);
		System.exit(0);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DataServiceManager.getInstance(mContext).clearMemCache();
		Logger.getInstance(TAG).debug("app onLowMemory()");
	}

	@Override
	public void onTerminate() {
		Logger.getInstance(TAG).debug("app onTerminate()");
	}
}
