package com.zhima.base.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zhima.base.logger.Logger;
/**
* @ClassName: SimpleAlarmTimer
* @Description: 使用alarm实现的定时器(解决系统Timer休眠唤醒问题)
* @author liubingsr
* @date 2012-6-25 上午11:55:49
*
*/
public class SimpleAlarmTimer {
	private final static String TAG = "SimpleAlarmTimer";
	private Context mContext;
	private String mMessage;
	private boolean mRepeated;
	private int mTime;
	private Intent mIntent;
	private SimpleAlarmTimerCallback mCallback;
	private AlarmReceiver mAlarmReceiver = null;
	private AlarmManagerTimer mTimer = null;

	class AlarmReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(mMessage)) {
				if (mCallback != null) {
					mCallback.onSimpleAlarmTimer(mMessage);
				}
			}
		}
	}
	/**
	* @ClassName: SimpleAlarmTimerCallback
	* @Description: 定时器回调
	* @author liubingsr
	* @date 2012-6-25 上午11:55:17
	*
	*/
	public interface SimpleAlarmTimerCallback {
		abstract void onSimpleAlarmTimer(String message);
	}

	public SimpleAlarmTimer(Context c, String message, boolean repeated, int time,
			SimpleAlarmTimerCallback callback) {
		mContext = c;
		mCallback = callback;
		mMessage = message;
		mIntent = new Intent(message);
		mRepeated = repeated;
		mTime = time;
	}

	public boolean isRunning() {
		if (mTimer == null) {
			return false;
		}
		return mTimer.isRunning();
	}
	/**
	* @Title: start
	* @Description: 启动定时器
	* @return void
	*/
	public void start() {
		if (mTimer != null || mAlarmReceiver != null) {
			stop();
		}
		mAlarmReceiver = new AlarmReceiver();
		if (mRepeated) {
			mTimer = new AlarmManagerRepeatTimer(mContext);
		} else {
			mTimer = new AlarmManagerOneshotTimer(mContext);
		}
		mContext.registerReceiver(mAlarmReceiver, new IntentFilter(mMessage));
		mTimer.register(mTime, mIntent);
	}
	/**
	* @Title: stop
	* @Description: 停止定时器
	* @return void
	*/
	public void stop() {
		if (mAlarmReceiver != null) {
			try {
				mContext.unregisterReceiver(mAlarmReceiver);
			} catch (Exception e) {
				Logger.getInstance(TAG).debug("stop:" + e.getMessage(), e);
			} finally {
				mAlarmReceiver = null;
			}
		}
		if (mTimer != null) {
			mTimer.stop();
			mTimer = null;
		}		
	}
}