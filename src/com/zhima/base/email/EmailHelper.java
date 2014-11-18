/* 
* @Title: EmailHelper.java
* Created by liubingsr on 2012-5-22 下午2:44:41 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.email;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.zhima.R;
import com.zhima.base.logger.Logger;
import com.zhima.base.storage.FileHelper;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: EmailHelper
 * @Description: email发送
 * @author liubingsr
 * @date 2012-5-22 下午2:44:41
 *
 */
public class EmailHelper {
	public final static String TAG = "EmailHelper";
	
	/**
	* @Title: sendEmail
	* @Description: 打开发送邮件界面
	* @param context
	* @param addr 邮件接收地址
	* @return void
	*/
	public static void sendEmail(Context context, String addr) {
		Uri uri = Uri.parse("mailto:" + addr);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		
		try {
			context.startActivity(it);
		} catch (ActivityNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			HaloToast.show(context, context.getString(R.string.mail_failed));
		}
	}
	
	/**
	* @Title: sendEmail
	* @Description: 发送邮件
	* @param context
	* @param subject 邮件标题
	* @param body 邮件正文
	* @param address 收件人列表
	* @return void
	*/
	public static void sendEmail(Context context, String subject, String body, String... address) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_EMAIL, address);
		it.putExtra(Intent.EXTRA_SUBJECT, subject);
		it.putExtra(Intent.EXTRA_TEXT, body);
		it.setType("plain/text");
		try {
			context.startActivity(it);
		} catch (ActivityNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			HaloToast.show(context, context.getString(R.string.mail_failed));
		}
	}
	 
	/**
	* @Title: sendEmailWithAttach
	* @Description: 发送带附件的邮件
	* @param context
	* @param subject 邮件标题
	* @param body 邮件正文
	* @param uri 附件uri
	* @param address 收件人列表
	* @return void
	*/
	public static void sendEmailWithAttach(Context context, String subject, String body, Uri uri, String... address) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.putExtra(Intent.EXTRA_EMAIL, address);
		it.putExtra(Intent.EXTRA_SUBJECT, subject);
		it.putExtra(Intent.EXTRA_TEXT, body);
		it.putExtra(Intent.EXTRA_STREAM, uri);
		it.setType(FileHelper.getMIME(uri.toString()));
		
		try {
			context.startActivity(it);
		} catch (ActivityNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			HaloToast.show(context, context.getString(R.string.mail_failed));
		}
	}
	
}
