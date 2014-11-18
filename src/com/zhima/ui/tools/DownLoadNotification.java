package com.zhima.ui.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.zhima.R;

/**
 * 文件下载进度 状态栏通知
 * 
 * @ClassName: DownLoadNotification
 * @Description: TODO
 * @author yusonglin
 * @date 2012-8-11 下午6:09:58
 */
public class DownLoadNotification {
	private final static int MAX_PROGRESS = 100;
	private Context mContext;
	private NotificationManager mManager;
	private Notification mNotification;
	private RemoteViews mViews;

	/**
	 * @param context
	 * @param intent 点击意图
	 * @param contentTitle 内容标题
	 * @param total 进度总大小
	 */
	public DownLoadNotification(Context context, Intent intent, String contentTitle) {
		this.mContext = context;

		mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mManager.cancel(1);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		mNotification = new Notification(R.drawable.icon, mContext.getString(R.string.app_name), System.currentTimeMillis());
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotification.defaults = Notification.DEFAULT_LIGHTS;
		mViews = new RemoteViews(context.getPackageName(), R.layout.notification_downloading);
		mNotification.contentView = mViews;
		mNotification.contentIntent = pendingIntent;
		mNotification.contentView.setTextViewText(R.id.txt_notification_contentTitle, contentTitle);
		mNotification.contentView.setProgressBar(R.id.pbr_notification_downloading, MAX_PROGRESS, 0, false);
		mManager.notify(1, mNotification);
	}

	public void setFinishIntent(Intent intent) {
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		mNotification.contentIntent = pendingIntent;
		setProgress(MAX_PROGRESS);
	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 */
	public void setProgress(final int progress) {
		if (progress >= MAX_PROGRESS) {
			mNotification.contentView.setTextViewText(R.id.txt_notification_contentTitle, "芝麻客下载完毕");
		} else {
			mNotification.contentView.setTextViewText(R.id.txt_notification_contentTitle, "芝麻客正在下载(" + (progress)
							+ "%)");
		}
		mViews.setProgressBar(R.id.pbr_notification_downloading, MAX_PROGRESS, progress, false);
		mNotification.contentView = mViews;
		mManager.notify(1, mNotification);
	}
	
	public void cancel() {
		mManager.cancelAll();
	}
}
