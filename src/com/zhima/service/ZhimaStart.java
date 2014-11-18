package com.zhima.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
* @ClassName: ZhimaStart
* @Description: 开机广播。启动服务
* @author liubingsr
* @date 2012-6-25 下午7:34:23
*
 =*/
public class ZhimaStart extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			context.startService(new Intent(context, ZhimaService.class));
		}
	}
}
