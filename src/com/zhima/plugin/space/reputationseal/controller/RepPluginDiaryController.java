package com.zhima.plugin.space.reputationseal.controller;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.DiaryService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.reputationseal.ReputationAcqierementViewPlugin;
import com.zhima.plugin.space.reputationseal.adapter.RepDiaryPluginAdapter;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.diary.activity.DiaryInfoActivity;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.space.couples.activity.CouplesDiaryListActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpaceDiaryListActivity;

/**
 * 誉玺空间——日志列表控制器
 * 
 * @ClassName: RepPluginDiaryController
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-30 下午4:12:15
 */
public class RepPluginDiaryController extends BaseViewPluginController {

	// 最多显示几个 
	private static final int ACQIEREMENT_COUNT = 3;

	private static final String TAG = RepPluginDiaryController.class.getSimpleName();

	private ReputationAcqierementViewPlugin mRepDiaryPlugin;
	private ZMObject mZMOrganizationObject;

	private RefreshListData<ZMDiary> mDiarytRefreshList;
	private ArrayList<ZMDiary> mDataList;

	private RepDiaryPluginAdapter mDiaryAdapter;

	private DiaryService mDiaryService;

	public RepPluginDiaryController(BaseActivity activity, ReputationAcqierementViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mRepDiaryPlugin = viewPlugin;
		mDiaryService = DiaryService.getInstance(mParentActivity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		// TODO
		mZMOrganizationObject = zmObject;

		mDiarytRefreshList = mDiaryService.getCacheDiaryList(zmObject.getRemoteId(), true);
		if (mDiarytRefreshList.isEmpty()) {
			mDiaryService.getZMSpaceDiaryList(zmObject, refreshed, this);
		} else {
			setAcqierementView();
		}
		mRepDiaryPlugin.setArrowClick(acqierementMoreClick);
	}

	private void setAcqierementView() {
		mDataList = mDiarytRefreshList.getDataList();
		if (mDiaryAdapter == null) {
			mDiaryAdapter = new RepDiaryPluginAdapter(mParentActivity, R.layout.personal_center_data_diary_item,
					mDataList);
			// 设置显示个数
			mDiaryAdapter.setItemtCount(ACQIEREMENT_COUNT);
			mRepDiaryPlugin.setAdapter(mDiaryAdapter);
			mRepDiaryPlugin.setOnItemClickListener(acqierementItemClick);

		} else {
			mDiaryAdapter.setData(mDataList);
			mDiaryAdapter.notifyDataSetChanged();
		}
	}

	private OnItemClickListener acqierementItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ZMDiary diary = mDataList.get(position);
			Intent intent = new Intent(mParentActivity, DiaryInfoActivity.class);
			intent.putExtra(DiaryConsts.user_Id, diary.getAuthor()!=null?diary.getAuthor().getUserId():ZMConsts.INVALID_ID);
			intent.putExtra(DiaryConsts.space_id, mZMOrganizationObject.getRemoteId());
			intent.putExtra("id", mZMOrganizationObject.getId());
			intent.putExtra(DiaryConsts.position, position);
			mParentActivity.startActivity(intent);
		}
	};

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_DIARY_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到情侣知天使日志列表
					GetDiaryListProtocol p = (GetDiaryListProtocol) protocol;
					mDiarytRefreshList = p.getDataList();
					setAcqierementView();
				} else {

				}
			}
		} else {
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		startWaitingDialog();
	}

	/** 进入频道列表 */
	private View.OnClickListener acqierementMoreClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO 跳进日志列表页面
			if (mZMOrganizationObject != null) {
				Intent acqierementListIntent = new Intent(mParentActivity, ZmSpaceDiaryListActivity.class);
				acqierementListIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMOrganizationObject.getId());
				startActivity(acqierementListIntent);
			} else {
				ErrorManager.showErrorMessage(mParentActivity);
			}
		}
	};
}
