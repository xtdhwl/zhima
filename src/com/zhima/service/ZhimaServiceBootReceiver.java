package com.zhima.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ZhimaServiceBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent it = new Intent(context,ZhimaService.class);
		context.startService(it);
	}	
}