package com.zhima.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.zhima.data.service.RequestService;

public class ZhimaService extends Service {
	private final static String TAG = "ZhimaService";
	private RequestService mRequestService = null;
	
	// binder
	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		public ZhimaService getService() {
			return ZhimaService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}	

	@Override
	public void onCreate() {
		super.onCreate();		
		mRequestService = RequestService.getInstance(this);
		mRequestService.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
}
