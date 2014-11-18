package com.zhima.ui.tools;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

import com.zhima.base.consts.ZMConsts.ZMStats;
import com.zhima.base.logger.Logger;
import com.zhima.base.utils.UmengStatUtil;

/**
 * @ClassName: IntentHelper
 * @Description: Intent工具类
 * @author luqilong
 * @date 2013-1-15 下午9:40:09
 * 
 */
public class IntentHelper {
	private static final String TAG = IntentHelper.class.getSimpleName();

	/**
	 * @Title: sendGallerySelectPhoto
	 * @Description: 从媒体库获取图片,并返回结果
	 * @param activity
	 * @return boolean
	 */
	public static boolean sendGalleryPhotoForResult(Activity activity, int requestCode) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			activity.startActivityForResult(intent, requestCode);
			return true;
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.toString());
		}
		return false;
	}

	/**
	 * @Title: sendCamreaSelectPhotoForResult
	 * @Description: 通过摄像机拍照。如果路径不为空直接保存，如果为null返回Bitmap结果
	 * @param activity
	 * @param path
	 * @param requestCode
	 * @return boolean
	 */
	public static boolean sendCamreaPhotoForResult(Activity activity, String path, int requestCode) {
		try {
			if (path != null) {
				String fileName = path;
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
				activity.startActivityForResult(intent, requestCode);
			} else {
				//TODO 
				throw new RuntimeException("方法未实现");
			}
			return true;
		} catch (Exception e) {
			Logger.getInstance(e.toString());
		}
		return false;
	}

	/**
	 * @Title: sendCropImage
	 * @Description: 
	 *               通过Intent剪切图片.在设置aspectX，aspectY方形使用1:1,如果是长方形应注意这两个值要为整数。注意默认是去除面部检测的
	 *               ，因为它会破坏掉特定的比例。
	 * @param activity
	 * @param aspectX aspectX宽
	 * @param aspectY aspectY高
	 * @param outputX outputX输出宽
	 * @param outputY outputX输出高
	 * @param scr 输出路径。Example: "file:///tmp/android.txt".使用Uri转换
	 * @param requestCode
	 * @return boolean
	 */
	public static boolean sendCropImageForResult(Activity activity, Uri scr, int aspectX, int aspectY, int outputX,
			int outputY, int requestCode) {
		try {
			//----------------------------------------------
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(scr, "image/*");
			//x3 : y4 == 1 : 1.3
			//x3 : y4 == 220 : 293.33

			//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪  
			cropIntent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例  
			//aspectX, aspectY ，两个值都需要为 整数，如果有一个为浮点数，就会导致比例失效。
			cropIntent.putExtra("aspectX", aspectX);
			cropIntent.putExtra("aspectY", aspectY);
			// outputX outputY 是裁剪图片宽高 ,注意这是像素大小的值 
			cropIntent.putExtra("outputX", outputX);
			cropIntent.putExtra("outputY", outputY);
			cropIntent.putExtra("return-data", true);
			// 是否去除面部检测，因为它会破坏掉特定的比例。
			cropIntent.putExtra("noFaceDetection", true);
			activity.startActivityForResult(cropIntent, requestCode);
			return true;
		} catch (Exception e) {
			Logger.getInstance(e.toString());
		}
		return false;
	}

	/**
	 * @Title: sendSmsShare
	 * @Description: 发送短信。注意默认使用友盟统计
	 * @param context
	 * @param shareContent 内容信息 void
	 */
	public static boolean sendSmsShare(Context context, String shareContent) {
		try {
			UmengStatUtil.onEvent(context, ZMStats.SMS_SHARE_EVENT);
			Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:"));//ACTION_SENDTO
			smsIntent.putExtra("sms_body", shareContent);
			smsIntent.setType("vnd.android-dir/mms-sms");
			context.startActivity(smsIntent);
			return true;
		} catch (Exception e) {
			Logger.getInstance(e.toString());
		}
		return false;
	}
	
	public static void callPhone(Context context,String phone){
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		context.startActivity(it);
	}
	
	public static void sendEmail(Context context,String content,String emailAdd){
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String[] recipients = new String[]{emailAdd, "",};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,content);
		emailIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
	
	
	public static void startAppByPackageName(Context context,String packageName){
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		 
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);
		 
		List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
		 
		ResolveInfo ri = apps.iterator().next();
		if (ri != null ) {
			String packageName1 = ri.activityInfo.packageName;
			String className = ri.activityInfo.name;
			 
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			 
			ComponentName cn = new ComponentName(packageName1, className);
			 
			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}
}
