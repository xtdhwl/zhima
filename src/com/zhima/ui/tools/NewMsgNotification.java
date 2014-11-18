package com.zhima.ui.tools;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zhima.R;

/**
 * 新消息状态栏通知
 * @ClassName: NewMsgNotification
 * @Description: TODO
 * @author yusonglin
 * @date 2012-8-13 上午10:23:07
*/
public class NewMsgNotification {

	
	/**
	 * 状态栏新消息通知
	 * @param context
	 * @param tickerText 提示标题
	 * @param contentTitle 内容标题
	 * @param contentText 主体内容
	 * @param intent 点击意图
	 */
	public static void showMsgNotification(Context context,String tickerText,String contentTitle,String contentText,Intent intent){
		NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		manager.cancel(0);
		Notification notification = new Notification(R.drawable.icon,tickerText, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		manager.notify(0, notification);
	}
	
}
