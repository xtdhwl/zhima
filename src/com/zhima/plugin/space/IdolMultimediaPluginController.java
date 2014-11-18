package com.zhima.plugin.space;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetMultimediaListProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ZMIdolService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.SpaceVPAdapter;
import com.zhima.ui.common.view.SpaceViewPager;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.activity.MultimediaListActivity;
import com.zhima.ui.tools.HttpImageLoader;

public class IdolMultimediaPluginController extends BaseViewPluginController {

	protected static final String TAG = IdolMultimediaPluginController.class.getSimpleName();
	private int preview_size = SystemConfig.VIEW_PLUGIN_PREVIEW_COUNT;
	private int item_count = 10000;

	private IdolMultimediaViewPlugin mViewPlugin;
	private ZMIdolObject mZMIdolObject;

	private RefreshListData<IdolAcqierement> mMultimediaRefreshList;
	private ArrayList<IdolAcqierement> dataList;

	private ZMIdolService mZMIdolService;

	public IdolMultimediaPluginController(BaseActivity activity, IdolMultimediaViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;
		mZMIdolService = ZMIdolService.getInstance(mParentActivity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMIdolObject = (ZMIdolObject) zmObject;

		// 获取知天使影像
		mMultimediaRefreshList = mZMIdolService.getCacheMultimediaList(mZMIdolObject);
		if (mMultimediaRefreshList.isEmpty()) {
			mZMIdolService.getMultimediaList(mZMIdolObject, refreshed, this);
		} else {
			setMultimediaView(mMultimediaRefreshList.getDataList());
		}

		mViewPlugin.setArrowClickListener(multimediaClick);
	}

	/** 设置影像View */
	private void setMultimediaView(final ArrayList<IdolAcqierement> arrayList) {

		SpaceVPAdapter<IdolAcqierement> adapter = new SpaceVPAdapter<IdolAcqierement>(mParentActivity, arrayList,
				preview_size, item_count) {
			@Override
			public Object getView(ViewGroup container, IdolAcqierement data, int position) {
				View view = View.inflate(mParentActivity, R.layout.space_idol_img_item, null);
				Logger.getInstance(TAG).debug("getView" + position);
				ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
				HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), photoImage,
						mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
				container.addView(view, 0);
				return view;
			}
		};

		mViewPlugin.setAdapter(adapter);
		mViewPlugin.setOnItemClickListener(onItemClickListener);
	};

	//点击条目
	private SpaceViewPager.OnItemClickListener onItemClickListener = new SpaceViewPager.OnItemClickListener() {

		@Override
		public void onItemClick(ViewGroup parent, View view, int position) {
			dataList = mMultimediaRefreshList.getDataList();
			ArrayList<String> tt = new ArrayList<String>();
			for (int i = 0; i < dataList.size(); i++) {
				tt.add(dataList.get(i).getContentUrl());
			}
			Intent acqierementIntent = new Intent(mParentActivity, IdoPhotoActivity.class);
			acqierementIntent.putStringArrayListExtra(BaseActivity.ACTIVITY_EXTRA, tt);
			acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
			mParentActivity.startActivity(acqierementIntent);
		}
	};

	//点击更多
	private OnClickListener multimediaClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent multimediaListIntent = new Intent(mParentActivity, MultimediaListActivity.class);
			multimediaListIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMIdolObject.getId());
			mParentActivity.startActivity(multimediaListIntent);
		}

	};

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_IDOL_MULTIMEDIA_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 获取知天使影音列表
					GetMultimediaListProtocol p = (GetMultimediaListProtocol) protocol;
					mMultimediaRefreshList = p.getDataList();
					setMultimediaView(mMultimediaRefreshList.getDataList());
				} else {

				}
			}
		} else {
			// 网络访问错误
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		mHandler.sendEmptyMessage(IntentMassages.START_WAITING_DIALOG);
	}
}
