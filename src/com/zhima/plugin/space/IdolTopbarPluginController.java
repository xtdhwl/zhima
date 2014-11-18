package com.zhima.plugin.space;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhima.R;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.TopbarBaseViewPlugin;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;

/** 
 * @ClassName: BusinessTopbarPluginController 
 * @Description: 知天使空间标题栏 
 * @author luqilong 
 * @date 2013-1-5 上午11:36:33 
 */
public class IdolTopbarPluginController extends BaseViewPluginController {

	private TopbarBaseViewPlugin mTopbarViewPlugin;
	private ZMIdolObject mZMIdolObject;
	private ScanningcodeService mScanningcodeService;

	public IdolTopbarPluginController(BaseActivity activity, BaseViewPlugin parent) {
		super(activity, parent);
		mTopbarViewPlugin = (TopbarBaseViewPlugin) parent;
		mScanningcodeService = ScanningcodeService.getInstance(mParentActivity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMIdolObject = (ZMIdolObject) zmObject;

		setTopbar();
	}

	private void setTopbar() {
		ZhimaTopbar topBar = mTopbarViewPlugin.getTopbar();
		RelativeLayout ll_right = (RelativeLayout) View.inflate(mParentActivity, R.layout.topbar_rightview, null);
		topBar.addRightLayoutView(ll_right);
		ImageView image1 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_favorite);
		topBar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		ImageView image2 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton2);
		image2.setImageResource(R.drawable.topbar_share);
		topBar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(shareTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
	}

	/** 标题栏:保存 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 添加到收藏夹
			if (mZMIdolObject != null) {
				FavoriteEntry entry = new FavoriteEntry();
				entry.setImageUrl(mZMIdolObject.getImageUrl());
				entry.setObjectType(mZMIdolObject.getZMObjectType());
				entry.setObjectId(mZMIdolObject.getRemoteId());
				entry.setTitle(mZMIdolObject.getName());
				entry.setContent(mZMIdolObject.getSpaceHomepage());
				mScanningcodeService.addFavorite(entry, mParentActivity);
			} else {
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				String shareContent = String.format(mParentActivity.getString(R.string.share_content_no_id),
						mZMIdolObject.getName(), mZMIdolObject.getSpaceHomepage());
				String sms_message = String.format(mParentActivity.getString(R.string.idol_sms_message),
						mZMIdolObject.getName(), mZMIdolObject.getSpaceHomepage());
//				SharePopupMenu sharePopupMenu = new SharePopupMenu(mParentActivity, mParentActivity, v, sms_message,
//						shareContent);
				SharePopupMenu.show(mParentActivity, mParentActivity, v, sms_message,
						shareContent);
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
				//			HaloToast.show(getApplicationContext(), R.string.save_success);
				mHandler.obtainMessage(IntentMassages.SHOW_TOAST, R.string.save_success).sendToTarget();
			} else {
				//			HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				mHandler.obtainMessage(IntentMassages.SHOW_TOAST, protocol.getProtocolErrorMessage()).sendToTarget();
			}
		} else {
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}
	};

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
	}
}
