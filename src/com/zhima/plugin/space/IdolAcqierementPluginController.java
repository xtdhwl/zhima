package com.zhima.plugin.space;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetAcqierementListProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetChannelListByKindProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ZMIdolService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.space.activity.AcqierementListActivity;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.adapter.AcqierementAdapter;

/**
 * @ClassName: IdolAcqierementPluginController
 * @Description: 知天使频道
 * @author luqilong
 * @date 2013-1-7 上午11:16:49
 */
public class IdolAcqierementPluginController extends BaseViewPluginController {
	//最多显示几个
	private static final int ACQIEREMENT_COUNT = 3;

	private static final String TAG = IdolAcqierementPluginController.class.getSimpleName();

	private IdolAcqierementViewPlugin mViewPlugin;
	private ZMIdolObject mZMIdolObject;

	private RefreshListData<IdolAcqierement> mAcqierementRefreshList;
	private ArrayList<IdolAcqierement> mDataList;

	private AcqierementAdapter mAcqierementAdapter;
//	private MyDataSetObserver mObserver;

	private ZMIdolService mZMIdolService;

	public IdolAcqierementPluginController(BaseActivity activity, IdolAcqierementViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;
		mZMIdolService = ZMIdolService.getInstance(activity);
//		mObserver = new MyDataSetObserver();
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMIdolObject = (ZMIdolObject) zmObject;

		// 知天使频道列表
		mAcqierementRefreshList = mZMIdolService.getCacheAcqierementList(mZMIdolObject);
		if (mAcqierementRefreshList.isEmpty()) {
			//XXX zmobject
			mZMIdolService.getChannelListByKind(mZMIdolObject, false, this);
		} else {
			mDataList = mAcqierementRefreshList.getDataList();
			setAcqierementView();
		}

		mViewPlugin.setArrowClick(acqierementMoreClick);
	}

	private void setAcqierementView() {

		if (mAcqierementAdapter == null) {
			mAcqierementAdapter = new AcqierementAdapter(mParentActivity, R.layout.space_acqierement_item, mDataList);
			// 设置显示个数
			mAcqierementAdapter.setItemtCount(ACQIEREMENT_COUNT);
			mViewPlugin.setAdapter(mAcqierementAdapter);
			mViewPlugin.setOnItemClickListener(acqierementItemClick);

		} else {
			mAcqierementAdapter.setData(mDataList);
			mAcqierementAdapter.notifyDataSetChanged();
		}

//		mAcqierementAdapter.registerDataSetObserver(mObserver);
//		setAcqierementMoreImg();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);

		if (protocol.isHttpSuccess()) {
			//if (protocol.getProtocolType() == ProtocolType.GET_IDOL_CHANNEL_LIST_PROTOCOL) {
			if (protocol.isHandleSuccess()) {
				// 得到知天使频道列表
				GetChannelListByKindProtocol p = (GetChannelListByKindProtocol) protocol;
				mAcqierementRefreshList = p.getDataList();
				mDataList = mAcqierementRefreshList.getDataList();
				setAcqierementView();
			} else {

			}
			//}
		} else {
			// 网络访问错误
			//			if (protocol.getProtocolType() == ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL) {
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			//			}
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		mHandler.sendEmptyMessage(IntentMassages.START_WAITING_DIALOG);
	}

	/** 进入频道列表 */
	private View.OnClickListener acqierementMoreClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				Intent acqierementListIntent = new Intent(mParentActivity, AcqierementListActivity.class);
				acqierementListIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMIdolObject.getId());
				acqierementListIntent.putExtra(AcqierementListActivity.TOPBAR_TITLE,
						mParentActivity.getText(R.string.acqierement_info));
				mParentActivity.startActivity(acqierementListIntent);
			} else {
				mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			}
		}
	};

	//点击频道列表
	private OnItemClickListener acqierementItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// 知天使频道详情
			mDataList = mAcqierementRefreshList.getDataList();
			mDataList = mAcqierementRefreshList.getDataList();
			ArrayList<String> urlArray = new ArrayList<String>();
			for (int i = 0; i < mDataList.size(); i++) {
				urlArray.add(mDataList.get(i).getContentUrl());
			}
			Intent acqierementIntent = new Intent(mParentActivity, IdoPhotoActivity.class);
			acqierementIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, urlArray);
			acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
			acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE,
					mParentActivity.getText(R.string.acqierement_info));
			mParentActivity.startActivity(acqierementIntent);
		}

	};

//	//监听数据是否显示箭头
//	private class MyDataSetObserver extends DataSetObserver {
//		@Override
//		public void onChanged() {
//			super.onChanged();
//			setAcqierementMoreImg();
//		}
//
//		@Override
//		public void onInvalidated() {
//			super.onInvalidated();
//			setAcqierementMoreImg();
//		}
//	}

//	//显示箭头 
//	private void setAcqierementMoreImg() {
//		Logger.getInstance(TAG).debug("mAcqierementAdapter onChanged");
//		if (mAcqierementRefreshList.getDataList().size() > ACQIEREMENT_COUNT) {
//			mViewPlugin.setShowArrow(true);
//		} else {
//			mViewPlugin.setShowArrow(false);
//		}
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		if (mAcqierementAdapter != null) {
//			mAcqierementAdapter.unregisterDataSetObserver(mObserver);
//		}
	}
}
