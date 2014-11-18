package com.zhima.ui.share;

import android.content.Context;
import android.view.View;

import com.zhima.R;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMObject;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.share.RenRenSpaceShare.SendRenrenListener;
import com.zhima.ui.share.SinaWeiboShare.SendWeiboListener;
import com.zhima.ui.tools.ShareHelper;

public class SharePopupMenu {
//	private String sms_message;
//	private String shareContent;
//	private BaseActivity mActivity;
//	private Context mContext;
//	private View v;
//
//	public SharePopupMenu(Context context,BaseActivity activity, View v, String sms_message, String shareContent) {
//		this.mContext = context;
//		this.mActivity = activity;
//		this.v = v;
//		this.sms_message = sms_message;
//		this.shareContent = shareContent;
//		
//	}

	public static void show(final Context mContext, final BaseActivity mActivity, final View v,
			final String sms_message, final String shareContent) {
		ZhimaPopupMenu menu = new ZhimaPopupMenu(mContext);
		menu.setMenuItems(R.menu.share);
		menu.show(v);
		menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(ZhimaMenuItem item, int position) {

				switch (item.getId()) {
				case R.id.sms:
					smsShare(mContext, mActivity, v, sms_message, shareContent);
					break;
				case R.id.sina_weibo:
					sinaShare(mContext, mActivity, v, sms_message, shareContent);
					break;
				case R.id.qq_weibo:
					qqShare(mContext, mActivity, v, sms_message, shareContent);
					break;
				case R.id.renren_space:
					renrenShare(mContext, mActivity, v, sms_message, shareContent);
					break;
				}
			}
		});
	}

	public static void smsShare(final Context mContext, final BaseActivity mActivity, View v, final String sms_message,
			final String shareContent) {
		if (sms_message != null) {
			ShareHelper.smsShare(mContext, sms_message);
		} else {
			ErrorManager.showErrorMessage(mContext);
		}
	}

	public static void sinaShare(final Context mContext, final BaseActivity mActivity, View v,
			final String sms_message, final String shareContent) {
		if (!NetUtils.isNetworkAvailable(mContext)) {
			HaloToast.show(mContext, R.string.network_request_failed);
			return;
		}
		SinaWeiboShare sinaWeiboShare = SinaWeiboShare.getInstance(mContext, (BaseActivity) mContext);
		sinaWeiboShare.setSendWeiboListener(new SendWeiboListener() {

			@Override
			public void sendBefore() {
				((BaseActivity) mContext).startWaitingDialog("", "正在发送");
			}

			@Override
			public void sendAfter() {
				((BaseActivity) mContext).dismissWaitingDialog();
			}

		});
		sinaWeiboShare.sendWeibo(shareContent, null);
	}

	public static void qqShare(final Context mContext, final BaseActivity mActivity, View v, final String sms_message,
			final String shareContent) {
		if (!NetUtils.isNetworkAvailable(mContext)) {
			HaloToast.show(mContext, R.string.network_request_failed);
			return;
		}
		TencentShare tencentShare = new TencentShare(mContext, mActivity, shareContent);
		tencentShare.doShare();

	}

	public static void renrenShare(final Context mContext, final BaseActivity mActivity, View v,
			final String sms_message, final String shareContent) {
		if (!NetUtils.isNetworkAvailable(mContext)) {
			HaloToast.show(mContext, R.string.network_request_failed);
			return;
		}
		RenRenSpaceShare renRenSpaceShare = RenRenSpaceShare.getInstance(mContext, (BaseActivity) mContext);
		renRenSpaceShare.setSendListener(new SendRenrenListener() {

			@Override
			public void sendBefore() {
				((BaseActivity) mContext).startWaitingDialog("", "正在发送");
			}

			@Override
			public void sendAfter() {
				((BaseActivity) mContext).dismissWaitingDialog();
			}
		});
		renRenSpaceShare.sendWithNoPic(shareContent);
	}

}
