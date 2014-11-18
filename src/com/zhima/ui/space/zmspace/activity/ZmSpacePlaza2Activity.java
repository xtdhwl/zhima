package com.zhima.ui.space.zmspace.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.OrderBy;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryListProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.QueryZMObjectProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSelfRecommendedZMObjectListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.SpaceQueryResult;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMSpaceKind;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.SearchService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.plugin.space.reputationseal.adapter.RepDiaryPluginAdapter;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.diary.activity.DiaryInfoActivity;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.space.zmspace.adapter.ZmSpacePlazaAdapter;
import com.zhima.ui.space.zmspace.adapter.ZmSpaceQueryResultAdapter;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 广场
 * @ClassName: ZmSpacePlaza2Activity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-2-3 下午8:39:23
*/
public class ZmSpacePlaza2Activity extends BaseActivity implements OnClickListener {

	private TextView mNewestBtn;
	private TextView mHottestBtn;
	private TextView mRecommendBtn;
	
	private PullToRefreshListView mPullNewestRefList;
	private PullToRefreshListView mPullHottestRefList;
	private PullToRefreshListView mPullRecommendRefList;
	
	private ListView mNewestList;
	private ListView mHottestList;
	private ListView mRecommendList;
	
	private View mEmptyLayout;
	
	private ZmSpaceQueryResultAdapter mNewestAdapter;
	private ZmSpaceQueryResultAdapter mHottestAdapter;
	private ZmSpacePlazaAdapter mRecommendAdapter;
	
	private static final int NEW = 1;
	private static final int HOT = 2;
	private static final int RECOMMENT = 3;
	
	/** 当前检索排序方式  NEW.最新 HOT.最热 Recommend.推荐 */
	private int mCurrentSort = NEW;
	
	private ZMObject mZMObject;
	private ZMSpaceKind ZMSpaceKind;
	private int zmObjectType;
	private long spacekindId;
	private long cityId;
	
	private RefreshListData<SpaceQueryResult> mNewestRefreshList;
	private RefreshListData<SpaceQueryResult> mHottestRefreshList;
	private RefreshListData<ZMObject> mRecommendRefreshList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.space_zmspace_plaza2_activity);
		
		initData();
		setTopBar();
		findView();
		setUpView();
		setListener();
	}

	private void getSpaceData(boolean refreshed) {
		SearchService.getInstance(this).queryZMObject(zmObjectType, spacekindId, OrderBy.NEW, refreshed, this);
	}
	
	private void getSpaceByHot(boolean refreshed){
		SearchService.getInstance(this).getPlazaSpaceByHot(zmObjectType, refreshed, this);
	}
	
	private void getSpaceByRecommend(boolean refreshed){
		ZMSpaceService.getInstance(this).getSquareRecommendedSpaceList(mZMObject, refreshed, this);
	}
	
	private void setListener() {
		//TODO
		mPullNewestRefList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getSpaceData(mPullNewestRefList.hasPullFromTop());
			}
		});
		mPullHottestRefList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				getSpaceByHot(mPullHottestRefList.hasPullFromTop());
			}
		});
		mPullRecommendRefList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				getSpaceByRecommend(mPullRecommendRefList.hasPullFromTop());
			}
		});
	}

	private void initData() {
		Intent intent = getIntent();
		long mZmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = ScanningcodeService.getInstance(this).getCacheZMObject(mZmObjectId);
		
		if(mZMObject!=null){
			ZMSpaceKind = mZMObject.getSpaceKind();
			zmObjectType = mZMObject.getZMObjectType();
			if(ZMSpaceKind!=null){
				spacekindId = ZMSpaceKind.getId();
			}
			cityId = mZMObject.getCityId();
		}
	}

	private void setTopBar() {
		ViewInitTools.setTopBar(this, "广场", View.GONE, null);
	}

	private void findView() {
		//TODO
		mNewestBtn = (TextView) this.findViewById(R.id.btn_zmspace_plaza_newest);
		mHottestBtn = (TextView) this.findViewById(R.id.btn_zmspace_plaza_hottest);
		mRecommendBtn = (TextView) this.findViewById(R.id.btn_zmspace_plaza_recommend);
		
		mPullNewestRefList = (PullToRefreshListView) this.findViewById(R.id.refresh_zmspace_plaza_newestList);
		mPullHottestRefList = (PullToRefreshListView) this.findViewById(R.id.refresh_zmspace_plaza_hottestList);
		mPullRecommendRefList = (PullToRefreshListView) this.findViewById(R.id.refresh_zmspace_plaza_recommendList);
		
		mNewestList = mPullNewestRefList.getRefreshableView();
		mHottestList = mPullHottestRefList.getRefreshableView();
		mRecommendList = mPullRecommendRefList.getRefreshableView();
		
		mEmptyLayout = this.findViewById(R.id.layout_empty);
	}

	private void setUpView() {
		//TODO
		mNewestBtn.setOnClickListener(this);
		mHottestBtn.setOnClickListener(this);
		mRecommendBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_zmspace_plaza_newest:
			if(mCurrentSort == NEW){
				return;
			}
			mCurrentSort = NEW;
			mPullNewestRefList.setVisibility(View.VISIBLE);
			mPullHottestRefList.setVisibility(View.GONE);
			mPullRecommendRefList.setVisibility(View.GONE);
			mNewestList.setVisibility(View.VISIBLE);
			mHottestList.setVisibility(View.GONE);
			mRecommendList.setVisibility(View.GONE);
			getSpaceData(true);
			break;
		case R.id.btn_zmspace_plaza_hottest:
			if(mCurrentSort == HOT){
				return;
			}
			mCurrentSort = HOT;
			mPullNewestRefList.setVisibility(View.GONE);
			mPullHottestRefList.setVisibility(View.VISIBLE);
			mPullRecommendRefList.setVisibility(View.GONE);
			mNewestList.setVisibility(View.GONE);
			mHottestList.setVisibility(View.VISIBLE);
			mRecommendList.setVisibility(View.GONE);
			getSpaceByHot(true);
			break;
		case R.id.btn_zmspace_plaza_recommend:

			if(mCurrentSort == RECOMMENT){
				return;
			}
			mCurrentSort = RECOMMENT;
			mPullNewestRefList.setVisibility(View.GONE);
			mPullHottestRefList.setVisibility(View.GONE);
			mPullRecommendRefList.setVisibility(View.VISIBLE);
			mNewestList.setVisibility(View.GONE);
			mHottestList.setVisibility(View.GONE);
			mRecommendList.setVisibility(View.VISIBLE);
			getSpaceByRecommend(true);
			break;
		}
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.QUERY_SPACE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					QueryZMObjectProtocol p = (QueryZMObjectProtocol) protocol;
					mNewestRefreshList = p.getDataList();
					setNewestView(mNewestRefreshList.getDataList());
					mPullNewestRefList.setLastPage(mNewestRefreshList.isLastPage());
				} else {

				}
				mPullNewestRefList.onRefreshComplete(mPullNewestRefList.hasPullFromTop());
			}else if (protocol.getProtocolType() == ProtocolType.QUERY_ZMSPACE_PLAZASPACE_HOT_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					QueryZMObjectProtocol p = (QueryZMObjectProtocol) protocol;
					mHottestRefreshList = p.getDataList();
					setHottestView(mHottestRefreshList.getDataList());
					mPullHottestRefList.setLastPage(mHottestRefreshList.isLastPage());
				} else {

				}
				mPullHottestRefList.onRefreshComplete(mPullHottestRefList.hasPullFromTop());
			}else if (protocol.getProtocolType() == ProtocolType.GET_SQUARE_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetSelfRecommendedZMObjectListProtocol p = (GetSelfRecommendedZMObjectListProtocol) protocol;
					mRecommendRefreshList = p.getDataList();
					setRecommendView(mRecommendRefreshList.getDataList());
					mPullRecommendRefList.setLastPage(mRecommendRefreshList.isLastPage());
				} else {
					
				}
				mPullRecommendRefList.onRefreshComplete(mPullRecommendRefList.hasPullFromTop());
			}
		} else {
			//TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullNewestRefList.setEmptyView();
			mPullHottestRefList.setEmptyView();
			mPullRecommendRefList.setEmptyView();
		}
		dismissWaitingDialog();
	}
	

	private void setRecommendView(ArrayList<ZMObject> dataList) {
		//TODO
		if(mRecommendAdapter == null){
			mRecommendAdapter = new ZmSpacePlazaAdapter(this, R.layout.space_zmspace_plaza_item, dataList);
			mRecommendList.setAdapter(mRecommendAdapter);
		} else {
			mRecommendAdapter.setData(dataList);
			mRecommendAdapter.notifyDataSetChanged();
		}
	}

	private void setHottestView(ArrayList<SpaceQueryResult> dataList) {
		//TODO
		if(mHottestAdapter == null){
			mHottestAdapter = new ZmSpaceQueryResultAdapter(this, R.layout.space_zmspace_plaza_item, dataList);
			mHottestList.setAdapter(mHottestAdapter);
//			mPullNewestRefList.setVisibility(View.GONE);
//			mPullHottestRefList.setVisibility(View.VISIBLE);
//			mPullRecommendRefList.setVisibility(View.GONE);
			
		} else {
			mHottestAdapter.setData(dataList);
			mHottestAdapter.notifyDataSetChanged();
		}
	}

	private void setNewestView(ArrayList<SpaceQueryResult> dataList) {
		//TODO
		if(mNewestAdapter == null){
			mNewestAdapter = new ZmSpaceQueryResultAdapter(this, R.layout.space_zmspace_plaza_item, dataList);
			mNewestList.setAdapter(mNewestAdapter);
		} else {
			mNewestAdapter.setData(dataList);
			mNewestAdapter.notifyDataSetChanged();
			
		}
	}
}
