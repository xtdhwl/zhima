package com.zhima.plugin.space.couples.controller;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ContactService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.UserService;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.TopbarBaseViewPlugin;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MsgDialog;
import com.zhima.ui.common.view.MsgDialog.OnBtClickListener;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.login.activity.LoginMainActivity;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.space.activity.IdolActivity;
import com.zhima.ui.space.activity.NoticeActivity;
import com.zhima.ui.space.zmspace.activity.PassedRecordActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpacePlazaActivity;

/**
 * @ClassName: CouplesTopbarPluginController
 * @Description: 喜印标题栏
 * @author luqilong
 * @date 2013-1-5 上午11:36:33
 * 
 */
public class CouplesTopbarPluginController extends BaseViewPluginController {

	private TopbarBaseViewPlugin mTopbarViewPlugin;
	private ZMCouplesObject mZMcouplesObject;

	public CouplesTopbarPluginController(BaseActivity activity, BaseViewPlugin parent) {
		super(activity, parent);
		mTopbarViewPlugin = (TopbarBaseViewPlugin) parent;
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMcouplesObject = (ZMCouplesObject) zmObject;

		//判断是否为自己 
		User zmUser = mZMcouplesObject.getUser();

		boolean bl = false;
		if (zmUser != null) {
			bl = UserService.getInstance(mParentActivity).isMySelf(zmUser.getId());
		}
		setTopbar();
		refreshTopbar(bl);
	}

	/** 标题栏:保存 添加到收藏夹 */
	public void favoriteTopbaClick(View v) {
		if (mZMcouplesObject != null) {
			FavoriteEntry entry = new FavoriteEntry();
			entry.setImageUrl(mZMcouplesObject.getImageUrl());
			entry.setObjectType(mZMcouplesObject.getZMObjectType());
			entry.setObjectId(mZMcouplesObject.getRemoteId());
			entry.setTitle(mZMcouplesObject.getName());
			entry.setContent(mZMcouplesObject.getSpaceHomepage());
			ScanningcodeService.getInstance(mParentActivity).addFavorite(entry, this);
			//XXX 
		} else {
			ErrorManager.showErrorMessage(mParentActivity);
		}

	}

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			if (mZMcouplesObject != null) {

				ZhimaPopupMenu menu = new ZhimaPopupMenu(mParentActivity);
				menu.setMenuItems(R.menu.couples_share);
				menu.setSytle(mTopbarViewPlugin.getSytle());
				menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public void onMenuItemClick(ZhimaMenuItem item, int position) {
						//分享
						String shareContent = String.format(mParentActivity.getString(R.string.share_content),
								mZMcouplesObject.getName(), mZMcouplesObject.getZMID(),
								mZMcouplesObject.getSpaceHomepage());
						String sms_message = String.format(mParentActivity.getString(R.string.business_sms_message),
								mZMcouplesObject.getName(), mZMcouplesObject.getZMID(),
								mZMcouplesObject.getSpaceHomepage());

						switch (item.getId()) {
						case R.id.sms:
							SharePopupMenu.smsShare(mParentActivity, mParentActivity, v, shareContent, shareContent);
							break;
						case R.id.sina_weibo:
							SharePopupMenu.sinaShare(mParentActivity, mParentActivity, v, sms_message, shareContent);
							break;
						case R.id.qq_weibo:
							SharePopupMenu.qqShare(mParentActivity, mParentActivity, v, sms_message, shareContent);
							break;
						case R.id.renren_space:
							SharePopupMenu.renrenShare(mParentActivity, mParentActivity, v, sms_message, shareContent);
							break;
						case R.id.favorite:
							favoriteTopbaClick(v);
							break;
						}
					}
				});
				menu.show(v);
			} else {
				//				ErrorManager.showErrorMessage(mParentActivity);
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};
	/** 标题栏:公告 */
	private View.OnClickListener noticeTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			if (mZMcouplesObject != null) {
				Intent it = new Intent(mParentActivity, NoticeActivity.class);
				it.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMcouplesObject.getZMCode());
				startActivity(it);
			} else {
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};
	/** 标题栏:看看谁来过 */
	private View.OnClickListener spaceTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			if (mZMcouplesObject != null) {
				boolean isGuest = AccountService.getInstance(mParentActivity).isGuest();
				if (isGuest) {
					Intent it = new Intent(mParentActivity, LoginMainActivity.class);
					startActivity(it);
				} else {
					MsgDialog dialog = new MsgDialog(mParentActivity);
					dialog.setTitle("提示");
					dialog.setMessage("查看谁来过的同时将保留您的浏览记录");
					dialog.setLeftBtText("取消");
					dialog.setRightBtText("确定");
					dialog.setOnBtClickListener(new OnBtClickListener() {
						@Override
						public void onRightBtClick() {
							Intent it = new Intent(mParentActivity, PassedRecordActivity.class);
							it.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMcouplesObject.getId());
							startActivity(it);
						}

						@Override
						public void onLeftBtClick() {

						}
					});
					dialog.show();

				}

			} else {
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		super.onHttpResult(protocol);
		// 收藏
		if (protocol.isHttpSuccess()) {
			if (protocol.isHandleSuccess()) {
				if (protocol.getProtocolType() == ProtocolType.ADD_FAVORITE_PROTOCOL) {
					HaloToast.show(mParentActivity, R.string.favorite_success);
				}
			} else {
				HaloToast.show(mParentActivity, protocol.getProtocolErrorMessage(), 0);
			}
		} else {
			ErrorManager.showErrorMessage(mParentActivity);
		}

	};

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
	}

	public void refreshTopbar(boolean bl) {
		ZhimaTopbar topbar = mTopbarViewPlugin.getTopbar();
		topbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(spaceTopbarClick);
		ImageView img2 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton2);
		img2.setImageResource(R.drawable.couples_topbar_bless_edit);
		topbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);

		topbar.findViewById(R.id.layout_topbar_rightButton3).setOnClickListener(noticeTopbarClick);
		ImageView img3 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton3);
		img3.setImageResource(R.drawable.topbar_notice);
		topbar.findViewById(R.id.layout_topbar_rightButton3).setVisibility(View.VISIBLE);

		topbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(shareTopbarClick);
		ImageView img1 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton1);
		img1.setImageResource(R.drawable.topbar_overflow);
		topbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = mTopbarViewPlugin.getTopbar();
		LinearLayout ll_left = (LinearLayout) View.inflate(mParentActivity, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(mParentActivity, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mHandler.sendEmptyMessage(IntentMassages.ACTIVITY_FINISH);
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(mTopbarViewPlugin.getTitle());
	}
}
