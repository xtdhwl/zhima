package com.zhima.ui.space.zmspace.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetAcqierementListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.DiaryService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.UserService;
import com.zhima.data.service.ZMIdolService;
import com.zhima.plugin.space.reputationseal.adapter.RepDiaryPluginAdapter;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.diary.activity.DiaryEditActivity;
import com.zhima.ui.diary.activity.DiaryInfoActivity;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.space.activity.AcqierementListActivity;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.adapter.AcqierementAdapter;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.tools.ViewInitTools;
import com.zhima.ui.usercenter.activity.PersonalDataActivity;

/**
 * 芝麻空间——日志列表
 * @ClassName: ReputationSealActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-30 上午11:53:57
 */
public class ZmSpaceDiaryListActivity extends BaseActivity {

	private PullToRefreshListView mAcqierementPullView;
	private ScanningcodeService mScanningcodeService;

	private ZMObject mZMObject;
	private DiaryService mDiaryService;
	private RefreshListData<ZMDiary> mAcqierementRefreshList;
	private RepDiaryPluginAdapter mDiaryAdapter;
	private ListView mAcqierementListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_zmspace_reputation_diarylist_activity);

		init();
		setTopbar();
		findView();
		setUpView();
	}

	private void setUpView() {
		//TODO
		if (mZMObject != null) {
			getServiceData();
			setListener();
		} else {
			if (NetUtils.isNetworkAvailable(this)) {
				HaloToast.show(this, R.string.gain_fail, 0);
			} else {
				HaloToast.show(this, R.string.network_request_failed, 0);
			}
		}
	}

	private void setTopbar() {
		//TODO
		ViewInitTools.setTopBar(this, "日志列表", View.GONE, null);
		
		
		if(mZMObject!=null){
			User user = mZMObject.getUser();
			if(user!=null && UserService.getInstance(this).isMySelf(user.getUserId())){
				ZhimaTopbar topbar = getTopbar();
				View topbarRightView = View.inflate(this, R.layout.topbar_rightview, null);
				RelativeLayout rightLayout1 = (RelativeLayout) topbarRightView.findViewById(R.id.layout_topbar_rightButton1);
				ImageView rightBt1 = (ImageView) topbarRightView.findViewById(R.id.img_topbar_rightButton1);
				
				rightBt1.setImageResource(R.drawable.user_center_add_friend);
				
				rightLayout1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ZmSpaceDiaryListActivity.this,DiaryEditActivity.class);
						intent.putExtra(DiaryConsts.space_id, mZMObject.getRemoteId());
						startActivity(intent);
					}
				});
				
				rightLayout1.setVisibility(View.VISIBLE);
				topbar.setRightLayoutVisible(true);
				topbar.addRightLayoutView(topbarRightView);
			}
		}
	}

	private void findView() {
		mAcqierementPullView = (PullToRefreshListView) findViewById(R.id.refresh_lstv);
		mAcqierementListView = mAcqierementPullView.getRefreshableView();
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mDiaryService = DiaryService.getInstance(this);

		// Intent 传递id
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);

		mZMObject = mScanningcodeService.getCacheZMObject(id);
	}

	private void getServiceData() {
		//知天使频道列表
		mAcqierementRefreshList = mDiaryService.getCacheDiaryList(mZMObject.getRemoteId(), true);
		if (mAcqierementRefreshList.isEmpty()) {
			mDiaryService.getZMSpaceDiaryList(mZMObject, true, this);
		} else {
			setAcqierementView(mAcqierementRefreshList.getDataList());
		}
	}

	/** 设置频道详情 */
	private void setAcqierementView(final ArrayList<ZMDiary> arrayList) {
		if (mDiaryAdapter == null) {
			mDiaryAdapter = new RepDiaryPluginAdapter(this, R.layout.personal_center_data_diary_item, arrayList);
			mAcqierementListView.setAdapter(mDiaryAdapter);
			mAcqierementListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//频道详情
					if (arrayList.size() > position) {
						ZMDiary diary = arrayList.get(position);
						Intent intent = new Intent(ZmSpaceDiaryListActivity.this, DiaryInfoActivity.class);
						intent.putExtra(DiaryConsts.user_Id, diary.getAuthor()!=null?diary.getAuthor().getUserId():-1);
						intent.putExtra(DiaryConsts.space_id, mZMObject.getRemoteId());
						intent.putExtra(DiaryConsts.position, position);
						startActivity(intent);
					}
				}
			});
		} else {
			mDiaryAdapter.setData(arrayList);
			mDiaryAdapter.notifyDataSetChanged();
		}
	}

	private void setListener() {
		mAcqierementPullView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mDiaryService.getZMSpaceDiaryList(mZMObject, mAcqierementPullView.hasPullFromTop(),
						ZmSpaceDiaryListActivity.this);
			}
		});
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_DIARY_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到知天使频道列表
					GetDiaryListProtocol p = (GetDiaryListProtocol) protocol;
					mAcqierementRefreshList = p.getDataList();
					setAcqierementView(mAcqierementRefreshList.getDataList());
					mAcqierementPullView.setLastPage(mAcqierementRefreshList.isLastPage());
				} else {

				}
				mAcqierementPullView.onRefreshComplete(mAcqierementPullView.hasPullFromTop());
			}
		} else {
			//TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mAcqierementPullView.setEmptyView();
		}
		dismissWaitingDialog();
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
		startWaitingDialog("", "正在加载..");
	}

}
