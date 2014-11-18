package com.zhima.ui.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.Intents;
import android.text.TextUtils;

import com.zhima.ui.common.view.HaloToast;

/**
 * 对本地通讯录操作的工具类
 * @ClassName: ContactUtil
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-28 下午3:53:18
*/
public class ContactUtil {

	/**
	 * 保存至本地通讯录
	 * @Title: saveToContact
	 * @Description: TODO
	 * @param context
	 * @param name 姓名
	 * @param phone 电话
	 * @param email 邮箱
	 * @param company 公司
	 * @param position 职位
	 * @param address 地址
	 * @param notes 备注
	 */
	public static void saveToContact(
			Context context,String name,String phone ,String email,
			String company,String position,String address,String notes){
		
		if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)){
			HaloToast.show(context, "姓名和电话不能为空");
			return ;
		}
		
		Intent intent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(
				Uri.parse("content://com.android.contacts"),"contacts"));
		intent.putExtra(Intents.Insert.NAME, name);
		intent.putExtra(Intents.Insert.PHONE, phone);
		intent.putExtra(Intents.Insert.JOB_TITLE, position);
		intent.putExtra(Intents.Insert.EMAIL, email);
		intent.putExtra(Intents.Insert.COMPANY, company);
		intent.putExtra(Intents.Insert.POSTAL, address);
		intent.putExtra(Intents.Insert.NOTES, notes);
		context.startActivity(intent);
	}
	
}
