package com.zhima.ui.tools;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.zhima.base.consts.ZMConsts.ZMStats;
import com.zhima.base.utils.UmengStatUtil;

public class ShareHelper {

	public static void smsShare(Context context,String shareContent){
		UmengStatUtil.onEvent(context, ZMStats.SMS_SHARE_EVENT);
		Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"));//ACTION_SENDTO
		smsIntent.putExtra("sms_body", shareContent);
		smsIntent.setType("vnd.android-dir/mms-sms");
		context.startActivity(smsIntent);
	}
	
}
