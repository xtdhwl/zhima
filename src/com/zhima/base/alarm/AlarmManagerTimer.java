package com.zhima.base.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.zhima.base.logger.Logger;
/**
* @ClassName: AlarmManagerTimer
* @Description: 闹铃包装类
* @author liubingsr
* @date 2012-6-25 上午11:41:48
*
*/
public abstract class AlarmManagerTimer {
	private final static String TAG = "AlarmManagerTimer";
	protected Context mContext;
	protected PendingIntent mPi;
	protected volatile boolean mRunning = false;	

	public abstract long register(long interval, Intent intent);

	public AlarmManagerTimer(Context context) {
		mPi = null;
		mContext = context;
	}

	public boolean isRunning() {
		return mRunning;
	}

	protected long register(long interval, Intent intent, boolean oneshot) {
		if (mPi != null) {
			stop();
		}
		mRunning = true;
		long elapsedRealtime = SystemClock.elapsedRealtime() + interval;

		AlarmManager alarmmanager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		mPi = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (oneshot) {
			alarmmanager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, elapsedRealtime, mPi);
		} else {
			alarmmanager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, elapsedRealtime, interval, mPi);
		}
		Logger.getInstance(TAG ).debug("alarmmanager register:" + interval + this + " (" + mPi
				+ ")");
		return elapsedRealtime;
	}

	public void stop() {
		Logger.getInstance(TAG ).debug("alarmmanager stop:" + this + " (" + mPi + ")");
		mRunning = false;
		if (mPi != null) {
			AlarmManager alarmmanager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			alarmmanager.cancel(mPi);
			mPi = null;
		}
	}
}
