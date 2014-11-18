package com.zhima.plugin.space;

import android.view.View;
import android.widget.RelativeLayout;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.PublicPlaceObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ContactService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.TopbarBaseViewPlugin;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;

/**
 * @ClassName: ElevatorTopbarPluginController
 * @Description: 公共空间标题栏
 * @author luqilong
 * @date 2013-1-7 下午5:54:09
 */
public class ElevatorTopbarPluginController extends BaseViewPluginController {

	private TopbarBaseViewPlugin mViewPlugin;
	private PublicPlaceObject mZMObject;
	private ZhimaTopbar mTopbar;

	public ElevatorTopbarPluginController(BaseActivity activity, TopbarBaseViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;

	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMObject = (PublicPlaceObject) zmObject;

		mTopbar = mViewPlugin.getTopbar();
		RelativeLayout ll_right = (RelativeLayout) View.inflate(mParentActivity, R.layout.topbar_rightview, null);
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
			if (mZMObject != null) {
				ContactEntry entry = new ContactEntry();
				entry.setTitle(mZMObject.getName());
				entry.setObjectType(mZMObject.getZMObjectType());
				entry.setObjectId(mZMObject.getRemoteId());
				entry.setTelephone(mZMObject.getPhone() + "");
				entry.setImageUrl(mZMObject.getImageUrl());
				ContactService.getInstance(mParentActivity).addContact(entry, false, ElevatorTopbarPluginController.this);
			} else {
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMObject != null) {
				String sms_message = String.format(mParentActivity.getString(R.string.business_sms_message),
						mZMObject.getName(), mZMObject.getZMID(), mZMObject.getSpaceHomepage());
				String shareContent = String.format(mParentActivity.getString(R.string.share_content),
						mZMObject.getName(), mZMObject.getZMID(), mZMObject.getSpaceHomepage());

//				SharePopupMenu sharePopupMenu = new SharePopupMenu(mParentActivity, mParentActivity, v, sms_message,
//						shareContent);
				SharePopupMenu.show(mParentActivity, mParentActivity, v, sms_message,
						shareContent);
			} else {
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	// TODO Auto-generated method stub
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.ADD_CONTACT_PROTOCOL) {
				// 收藏
				if (protocol.isHandleSuccess()) {
					mHandler.obtainMessage(IntentMassages.SHOW_TOAST, R.string.save_success);
				} else {
					mHandler.obtainMessage(IntentMassages.SHOW_TOAST, protocol.getProtocolErrorMessage());
				}
			}
		} else {
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}

	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
		// 请求服务器前
		mHandler.sendEmptyMessage(IntentMassages.START_WAITING_DIALOG);
	}

}
