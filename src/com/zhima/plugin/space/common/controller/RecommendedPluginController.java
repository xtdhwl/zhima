package com.zhima.plugin.space.common.controller;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetBottomRecommendedListProtocol;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.adapter.RecommendedAdapter;
import com.zhima.plugin.space.common.viewplugin.RecommendedViewPlugin;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: RecommendedPluginController
 * @Description: 周边推荐
 * @author luqilong
 * @date 2013-1-6 下午7:35:50
 */
public class RecommendedPluginController extends BaseViewPluginController {
	private int preview_size = SystemConfig.VIEW_PLUGIN_PREVIEW_COUNT;
	private int item_count = SystemConfig.VIEW_PLUGIN_LIST_MAX_COUNT;

	private RecommendedViewPlugin mViewPlugin;

	private ArrayList<ZMObject> mCacheZMObjectList;
	private RecommendedAdapter mCouplesRecommendedAdapter;

	private ZMSpaceService mZMSpaceService;

	public RecommendedPluginController(BaseActivity activity, RecommendedViewPlugin parent) {
		super(activity, parent);
		mViewPlugin = parent;
		mZMSpaceService = ZMSpaceService.getInstance(mParentActivity);
		mCacheZMObjectList = new ArrayList<ZMObject>();
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {

		GeoCoordinate geo = ZMLocationManager.getInstance().getLastKnownGeoCoordinate();
		mZMSpaceService.getBottomRecommendedList(zmObject.getZMObjectType(), zmObject, item_count,
				zmObject.getCityId(), geo, true, this);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_BOTTOM_RECOMMENDED_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 周边推荐列表
					GetBottomRecommendedListProtocol p = (GetBottomRecommendedListProtocol) protocol;
					setRecommendedView(p.getDataList().getDataList());
				} else {

				}
			}
		} else {
			//TODO 网络错误
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		startWaitingDialog();
	}

	private void setRecommendedView(ArrayList<ZMObject> arrayList) {
		//保存周围推荐缓存
		mCacheZMObjectList.clear();
		for (ZMObject zmObj : arrayList) {
			mCacheZMObjectList.add(zmObj);
		}

		if (mCouplesRecommendedAdapter == null) {
			mCouplesRecommendedAdapter = new RecommendedAdapter(mParentActivity, R.layout.space_darwable_card_item,
					mCacheZMObjectList, preview_size, item_count);
			mViewPlugin.setAdapter(mCouplesRecommendedAdapter);
			if (mCouplesRecommendedAdapter.getCount() >= 2) {
				mViewPlugin.setSelection(1);
			}
			mViewPlugin.setOnItemClickListener(mRecommendedClickListener);
		} else {
			mCouplesRecommendedAdapter.setData(mCacheZMObjectList);
			mCouplesRecommendedAdapter.notifyDataSetChanged();
		}
	}

	private OnItemClickListener mRecommendedClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ZMObject zmObj = mCouplesRecommendedAdapter.getItem(position);
			if (zmObj != null) {

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

	public void onDestroy() {
		if (mCacheZMObjectList != null) {
			mCacheZMObjectList.clear();
			mCacheZMObjectList = null;
		}
		super.onDestroy();
	};

}
