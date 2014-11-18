package com.zhima.plugin.space.zmcard.controller;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSelfRecommendedZMObjectListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.adapter.CouplesSpaceAdapter;
import com.zhima.plugin.space.zmcard.activity.ZMCardSpaceViewPlugin;
import com.zhima.plugin.space.zmcard.adapter.ZMCardGalleryAdapter;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: ZMCardSapcePluginController
 * @Description: 列表推荐
 * @author luqilong
 * @date 2013-1-7 上午11:16:49
 */
public class ZMCardSapcePluginController extends BaseViewPluginController {
	// 最多显示几个
	private static final int ACQIEREMENT_COUNT = 3;

	private static final String TAG = ZMCardSapcePluginController.class.getSimpleName();

	private ZMObject mZMObject;
	private ZMCardSpaceViewPlugin mViewPlugin;

	private ZMSpaceService mZMSpaceService;

	private RefreshListData<ZMObject> mCacheSelfRecommendedSpaceList;
	private ArrayList<ZMObject> mDataList;

	private ZMCardGalleryAdapter mZMCardGalleryAdapter;

	public ZMCardSapcePluginController(BaseActivity activity, ZMCardSpaceViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;
		mZMSpaceService = ZMSpaceService.getInstance(activity);
		mDataList = new ArrayList<ZMObject>();
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMObject = zmObject;
		mViewPlugin.getPluginView().setVisibility(View.GONE);
		// 文字推荐空间
		mCacheSelfRecommendedSpaceList = mZMSpaceService.getCacheSelfRecommendedSpaceList(zmObject);
		if (mCacheSelfRecommendedSpaceList.isEmpty()) {
			mZMSpaceService.getSelfRecommendedSpaceList(mZMObject, false, this);
		} else {
			setSpaceView();
		}
	}

	private void setSpaceView() {
		mDataList = mCacheSelfRecommendedSpaceList.getDataList();
		if (mDataList.size() > 0) {
			mViewPlugin.getPluginView().setVisibility(View.VISIBLE);
		}
		if (mZMCardGalleryAdapter == null) {
			mZMCardGalleryAdapter = new ZMCardGalleryAdapter(mParentActivity, R.layout.plugin_card_space_item,
					mDataList, R.color.light_gray);
			mViewPlugin.setOnItemSelectedListener(onItemSelectedListener);
			mViewPlugin.setAdatper(mZMCardGalleryAdapter);
			mViewPlugin.setOnItemClickListner(mItemClick);
			mViewPlugin.setItemSpace(0);
			mViewPlugin.setItemSelect(mDataList.size() / 2);
		} else {
			mZMCardGalleryAdapter.setData(mDataList);
			mZMCardGalleryAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到空间推荐
					GetSelfRecommendedZMObjectListProtocol p = (GetSelfRecommendedZMObjectListProtocol) protocol;
					mCacheSelfRecommendedSpaceList = p.getDataList();
					setSpaceView();
				} else {

				}
			}
		} else {
			// TODO 网络访问错误
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		startWaitingDialog();
	}

	/**
	 * 当前选择的item
	 */
	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			int count = parent.getChildCount();
			for (int i = 0; i < count; i++) {
				View childAt = parent.getChildAt(i);
				TextView iv = (TextView) childAt.findViewById(R.id.text_space_name);
				iv.setTextColor(mParentActivity.getResources().getColor(R.color.light_gray));
			}
			View selectedView = parent.getSelectedView();
			TextView iv = (TextView) selectedView.findViewById(R.id.text_space_name);
			iv.setTextColor(mParentActivity.getResources().getColor(R.color.card_text_gallery));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	private OnItemClickListener mItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ZMObject zmObj = mZMCardGalleryAdapter.getItem(position);
			if (zmObj != null) {
				// 启动
				Bundle bundleExtra = mParentActivity.getIntent().getBundleExtra(BaseActivity.ACTIVITY_BUNDLE);
				boolean isFinish = false;

				if (bundleExtra != null) {
					isFinish = bundleExtra.getBoolean("flag");
				}

				Bundle flagBundle = new Bundle();
				flagBundle.putBoolean("flag", true);
				ActivitySwitcher.openSpaceActivity(mParentActivity, zmObj, flagBundle, isFinish);
			} else {
				HaloToast.show(mParentActivity, R.string.load_failed);
			}

		}
	};

}
