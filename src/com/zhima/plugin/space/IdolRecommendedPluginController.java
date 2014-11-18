package com.zhima.plugin.space;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetRecommendedZMObjectListProtocol;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.SpaceVPAdapter;
import com.zhima.ui.common.view.SpaceViewPager.OnItemClickListener;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: IdolRecommendedPluginController
 * @Description: 知天使周边推荐
 * @author luqilong
 * @date 2013-1-6 下午7:35:50
 */
public class IdolRecommendedPluginController extends BaseViewPluginController {
	/** 其他知天使个数 */
	//	private static final int OTHER_IDOL_COUNT = 3;
	private int preview_size = SystemConfig.VIEW_PLUGIN_PREVIEW_COUNT;
	private int item_count = 20;

	private IdolRecommendedViewPlugin mViewPlugin;
	//	private ZMIdolObject mZMIdolObject;

	private ZMLocationManager mLocationManager;
	private ScanningcodeService mScanningcodeService;

	private RefreshListData<ZMObject> mOtherIdolRefreshList;

	private ArrayList<ZMObject> mCacheZMObjectList;

	public IdolRecommendedPluginController(BaseActivity activity, IdolRecommendedViewPlugin parent) {
		super(activity, parent);
		mViewPlugin = parent;
		mLocationManager = ZMLocationManager.getInstance();
		mScanningcodeService = ScanningcodeService.getInstance(activity);
		mCacheZMObjectList = new ArrayList<ZMObject>();
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		//		mZMIdolObject = (ZMIdolObject) zmObject;

		// 获取其它知天使
		GeoCoordinate geo = mLocationManager.getLastKnownGeoCoordinate();
		mScanningcodeService.getRecommendedZMIdolObjectList(item_count, zmObject.getZMObjectType(),
				zmObject.getRemoteId(), geo, true, this);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {

		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);

		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 其它知天使周边推荐列表
					GetRecommendedZMObjectListProtocol p = (GetRecommendedZMObjectListProtocol) protocol;
					mOtherIdolRefreshList = p.getDataList();
					addCacheZMObjectList(mOtherIdolRefreshList.getDataList());
					setOtherIdolView();
				} else {

				}
			}
		} else {
			// 网络访问错误
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}
	}

	private void addCacheZMObjectList(ArrayList<ZMObject> dataList2) {
		for (ZMObject obj : dataList2) {
			if (mCacheZMObjectList.size() >= item_count) {
				break;
			}
			mCacheZMObjectList.add(obj);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		mHandler.sendEmptyMessage(IntentMassages.START_WAITING_DIALOG);
	}

	private void setOtherIdolView() {
		SpaceVPAdapter<ZMObject> adapter = new SpaceVPAdapter<ZMObject>(mParentActivity, mCacheZMObjectList,
				preview_size, item_count) {
			@Override
			public Object getView(ViewGroup container, ZMObject data, int position) {
				View view = View.inflate(mParentActivity, R.layout.space_idol_recommend_item, null);
				ImageView mProductImage = (ImageView) view.findViewById(R.id.img_photo);
				TextView nNameText = (TextView) view.findViewById(R.id.txt_name);

				HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), mProductImage,
						mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
				nNameText.setText(data.getName());

				container.addView(view, 0);
				return view;
			}
		};
		mViewPlugin.setAdapter(adapter);
		mViewPlugin.setOnItemClickListener(onItemClickListener);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(ViewGroup parent, View view, int position) {
			Bundle bundleExtra = mParentActivity.getIntent().getBundleExtra(BaseActivity.ACTIVITY_BUNDLE);
			boolean isFinish = false;
			if (bundleExtra != null) {
				isFinish = bundleExtra.getBoolean("flag");
			}

			Bundle flagBundle = new Bundle();
			flagBundle.putBoolean("flag", true);
			ActivitySwitcher
					.openSpaceActivity(mParentActivity, mCacheZMObjectList.get(position), flagBundle, isFinish);
		}
	};

	public void onDestroy() {
		super.onDestroy();
		mCacheZMObjectList.clear();
		mCacheZMObjectList = null;
	};

}
