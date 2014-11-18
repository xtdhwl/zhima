package com.zhima.plugin.space.couples.controller;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSelfRecommendedZMObjectListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.adapter.CouplesSpaceAdapter;
import com.zhima.plugin.space.couples.CouplesSpaceViewPlugin;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: CouplesSapcePluginController
 * @Description: 空间列表
 * @author luqilong
 * @date 2013-1-7 上午11:16:49
 */
public class CouplesSapcePluginController extends BaseViewPluginController {
	//最多显示几个
	private static final int ACQIEREMENT_COUNT = 3;

	private static final String TAG = CouplesSapcePluginController.class.getSimpleName();

	private ZMObject mZMObject;
	private CouplesSpaceViewPlugin mViewPlugin;

	private ZMSpaceService mZMSpaceService;

	private RefreshListData<ZMObject> mCacheSelfRecommendedSpaceList;
	private ArrayList<ZMObject> mDataSpaceList;

	private CouplesSpaceAdapter mCouplesSpaceAdapter;

	public CouplesSapcePluginController(BaseActivity activity, CouplesSpaceViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;
		mZMSpaceService = ZMSpaceService.getInstance(activity);
		mDataSpaceList = new ArrayList<ZMObject>();
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		//没有数据默认隐藏
		mViewPlugin.getPluginView().setVisibility(View.GONE);

		mZMObject = zmObject;
		// 情侣知天使日志列表
		mCacheSelfRecommendedSpaceList = mZMSpaceService.getCacheSelfRecommendedSpaceList(zmObject);
		if (mCacheSelfRecommendedSpaceList.isEmpty()) {
			mZMSpaceService.getSelfRecommendedSpaceList(mZMObject, true, this);
		} else {
			setSpaceView();
		}
	}

	private void setSpaceView() {
		mDataSpaceList = mCacheSelfRecommendedSpaceList.getDataList();
		if (mDataSpaceList.size() > 0) {
			mViewPlugin.getPluginView().setVisibility(View.VISIBLE);
		}
		if (mCouplesSpaceAdapter == null) {
			mCouplesSpaceAdapter = new CouplesSpaceAdapter(mParentActivity, R.layout.plugin_couples_space_item,
					mDataSpaceList);
			mViewPlugin.setOnItemSelectedListener(onItemSelectedListener);
			mViewPlugin.setAdatper(mCouplesSpaceAdapter);
			mViewPlugin.setOnItemClickListner(mItemClick);
			mViewPlugin.setItemSelect(mDataSpaceList.size() / 2);
			mViewPlugin.setItemSpace(18);
		} else {
			mCouplesSpaceAdapter.notifyDataSetChanged();
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
			//TODO 网络访问错误
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		startWaitingDialog();
	}

	private OnItemClickListener mItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ZMObject zmObj = mCouplesSpaceAdapter.getItem(position);
			if (zmObj != null) {
				//启动
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

	/**
	 * 当前选择的item
	 */
	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//			int count = parent.getChildCount();
//			for (int i = 0; i < count; i++) {
//				View childAt = parent.getChildAt(i);
//				TextView iv = (TextView) childAt.findViewById(R.id.text_space_name);
//				iv.setTextColor(mParentActivity.getResources().getColor(R.color.dust_grey));
//			}
//			View selectedView = parent.getSelectedView();
//			TextView iv = (TextView) selectedView.findViewById(R.id.text_space_name);
//			iv.setTextColor(mParentActivity.getResources().getColor(R.color.black_color));
			Logger.getInstance(TAG).debug("onItemSelected");
			mCouplesSpaceAdapter.setSelectItem(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

}
