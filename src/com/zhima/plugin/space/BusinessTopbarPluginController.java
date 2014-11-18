package com.zhima.plugin.space;

import android.view.View;
import android.widget.RelativeLayout;

import com.zhima.R;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ContactService;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.TopbarBaseViewPlugin;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;

/**
 * @ClassName: BusinessTopbarPluginController
 * @Description: 商业空间标题栏
 * @author luqilong
 * @date 2013-1-5 上午11:36:33
 * 
 */
public class BusinessTopbarPluginController extends BaseViewPluginController {

	private TopbarBaseViewPlugin mTopbarViewPlugin;
	private CommerceObject mCommerceObject;
	private ZhimaTopbar mTopbar;

	public BusinessTopbarPluginController(BaseActivity activity, BaseViewPlugin parent) {
		super(activity, parent);
		mTopbarViewPlugin = (TopbarBaseViewPlugin) parent;
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mCommerceObject = (CommerceObject) zmObject;

		RelativeLayout ll_right = (RelativeLayout) View.inflate(mParentActivity, R.layout.topbar_rightview, null);
		mTopbar = mTopbarViewPlugin.getTopbar();
		mTopbar.addRightLayoutView(ll_right);

		mTopbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton3).setOnClickListener(shareTopbarClick);

		mTopbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
		mTopbar.findViewById(R.id.layout_topbar_rightButton3).setVisibility(View.VISIBLE);
	}

	/** 标题栏:保存 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 添加到收藏夹
			if (mCommerceObject != null) {
				ContactEntry entry = new ContactEntry();
				entry.setTitle(mCommerceObject.getName());
				entry.setObjectType(mCommerceObject.getZMObjectType());
				entry.setObjectId(mCommerceObject.getRemoteId());
				entry.setTelephone(mCommerceObject.getPhone());
				entry.setImageUrl(mCommerceObject.getImageUrl());
				ContactService.getInstance(mParentActivity).addContact(entry, false, BusinessTopbarPluginController.this);
			} else {
				//				ErrorManager.showErrorMessage(mParentActivity);
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCommerceObject != null) {
				String shareContent = String.format(mParentActivity.getString(R.string.share_content),
						mCommerceObject.getName(), mCommerceObject.getZMID(), mCommerceObject.getSpaceHomepage());
				String sms_message = String.format(mParentActivity.getString(R.string.business_sms_message),
						mCommerceObject.getName(), mCommerceObject.getZMID(), mCommerceObject.getSpaceHomepage());
//				SharePopupMenu sharePopupMenu = new SharePopupMenu(mParentActivity, mParentActivity, v, sms_message,
//						shareContent);
				SharePopupMenu.show(mParentActivity, mParentActivity, v, sms_message,
						shareContent);
			} else {
				//				ErrorManager.showErrorMessage(mParentActivity);
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		super.onHttpResult(protocol);
		// 收藏
		if (protocol.isHandleSuccess()) {
			//			HaloToast.show(getApplicationContext(), R.string.save_success);
			mHandler.obtainMessage(IntentMassages.SHOW_TOAST, R.string.save_success).sendToTarget();
		} else {
			//			HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
			mHandler.obtainMessage(IntentMassages.SHOW_TOAST, protocol.getProtocolErrorMessage()).sendToTarget();
		}
	};

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
	}
}
