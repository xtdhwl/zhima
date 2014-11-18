package com.zhima.ui.space.zmspace.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetVisitedUserListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.WeddingComment;
import com.zhima.data.model.ZMCardObject;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectVisitedEntry;
import com.zhima.data.model.ZMOrganizationObject;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.UserService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.diary.activity.DiaryReplyActivity;
import com.zhima.ui.space.zmspace.adapter.PassedRecordAdapter;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 看看谁来过
 * @ClassName: PassedRecordActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-24 下午8:08:05
 */
public class PassedRecordActivity extends BaseActivity {

	public static final String ISADD_VISITED_COMMENT = "isAddVisitedComment";
	public static final String ZMOBJECT_ID = "zmObject_id";
	public static final String ZMCODE = "zmcode";

	private PassedRecordAdapter mRecordAdapter;

	private PullToRefreshGridView mPullGridView;
	private GridView mGridView;

	private ScanningcodeService mScanningcodeService = ScanningcodeService
			.getInstance(this);
	private long zmObjectId;
	private ArrayList<ZMObjectVisitedEntry> visitedList;

	private ZMObject mZMObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_zmspace_whois_gone_activity);

		findView();
		initData();
		setTopBar();
		// getVisitedData();
	}

	private void initData() {
		// TODO
		mScanningcodeService = ScanningcodeService.getInstance(this);

		Intent intent = getIntent();
		zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);

		setListener();
		mZMObject = mScanningcodeService.getCacheZMObject(zmObjectId);
		if (mZMObject != null) {
			getServiceData();
		} else {
			if (NetUtils.isNetworkAvailable(this)) {
				HaloToast.show(this, R.string.gain_fail, 0);
			} else {
				HaloToast.show(this, R.string.network_request_failed, 0);
			}
		}

		// visitedList = new ArrayList<ZMObjectVisitedEntry>();
		//
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		// visitedList.add(new ZMObjectVisitedEntry());
		//
		// setUpView();
	}

	private void setTopBar() {
		// TODO
		ViewInitTools.setTopBar(this, "看看谁来过", View.GONE, null);

		boolean isAllowComment = true;
		
		if(mZMObject.getZMObjectType() == ZMObjectKind.ORGANIZATION_OBJECT){
			isAllowComment = ((ZMOrganizationObject)mZMObject).isAllowComment();
		}else if(mZMObject.getZMObjectType() == ZMObjectKind.VCARD_OBJECT){
			isAllowComment = ((ZMCardObject)mZMObject).isAllowComment();
		}else if(mZMObject.getZMObjectType() == ZMObjectKind.WEDDING_OBJECT){
			isAllowComment = ((ZMCouplesObject)mZMObject).isAllowComment();
		}
		
		if(isAllowComment && AccountService.getInstance(this).isLogin()){
			ZhimaTopbar topbar = getTopbar();
			View topbarRightView = View.inflate(this, R.layout.topbar_rightview,
					null);
			RelativeLayout rightLayout1 = (RelativeLayout) topbarRightView
					.findViewById(R.id.layout_topbar_rightButton1);
			ImageView rightBt1 = (ImageView) topbarRightView
					.findViewById(R.id.img_topbar_rightButton1);
			
			rightBt1.setImageResource(R.drawable.topbar_email);
			rightLayout1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO
					Intent intent = new Intent(PassedRecordActivity.this,
							DiaryReplyActivity.class);
					intent.putExtra(ISADD_VISITED_COMMENT, true);
					intent.putExtra(ZMOBJECT_ID, mZMObject.getId());
					intent.putExtra("title",mZMObject.getName()+"留言");
					startActivity(intent);
					
					// HaloToast.show(getApplicationContext(), "看看谁来过_留言");
				}
			});
			
			rightLayout1.setVisibility(View.VISIBLE);
			topbar.setRightLayoutVisible(true);
			topbar.addRightLayoutView(topbarRightView);
		}
	}

	private void getVisitedData() {
		// TODO
		ZMSpaceService.getInstance(this).getVisitedUserList(mZMObject, true,
				this);
	}

	private void getServiceData() {
		// 获取商户相册
//		visitedList = ZMSpaceService.getInstance(this)
//				.getCacheVisitedUserList(mZMObject).getDataList();
//		
		ZMSpaceService.getInstance(this).getVisitedUserList(mZMObject,true, this);
	}

	private void setListener() {
		mPullGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
//				HaloToast.show(getApplicationContext(), "刷新数据");
					ZMSpaceService.getInstance(PassedRecordActivity.this).getVisitedUserList(mZMObject,
							mPullGridView.hasPullFromTop(),
							PassedRecordActivity.this);
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 传USERID
				Intent it = new Intent(PassedRecordActivity.this,
						ZmSpaceVisitedCommentActivity.class);
				it.putExtra(ACTIVITY_EXTRA, visitedList.get(position).getId());
				it.putExtra(ACTIVITY_EXTRA2, visitedList.get(position).getVisitor().getUserId());
				startActivity(it);
				// HaloToast.show(getApplicationContext(), "看看此人留言");
			}
		});
	}

	private void findView() {
		mPullGridView = (PullToRefreshGridView) this
				.findViewById(R.id.refresh_grid);
		mGridView = mPullGridView.getRefreshableView();
	}

	private void setUpView() {
		// TODO
		if (mRecordAdapter == null) {
			mRecordAdapter = new PassedRecordAdapter(this,
					R.layout.space_zmspace_passed_record_item, visitedList);
			mGridView.setAdapter(mRecordAdapter);
		} else {
			mRecordAdapter.setData(visitedList);
			mRecordAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO
		super.onHttpStart(protocol);
		startWaitingDialog("", "请稍等...");
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		// TODO
		super.onHttpResult(protocol);
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SPACE_VISITED_USER_LIST_PROTOCOL) {
				
				if(protocol.isHandleSuccess()){
					GetVisitedUserListProtocol p = (GetVisitedUserListProtocol) protocol;
					RefreshListData<ZMObjectVisitedEntry> dataList = p
							.getDataList();
					visitedList = dataList.getDataList();
					
					setUpView();
					mPullGridView.setLastPage(dataList.isLastPage());
					
				}else{
					
				}
				
			} else {

			}
			mPullGridView.onRefreshComplete(mPullGridView.hasPullFromTop());
		} else {
			HaloToast.show(this, R.string.network_request_failed);
			mPullGridView.setEmptyView();
		}

		dismissWaitingDialog();
	}

}
