package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.NoticeAdapter;

/**
 * @ClassName:NoticeActivity
 * @Description:公告Activity
 * @author liqilong
 * @date 2012-8-6 上午9:33:30
 * 
 */
public class NoticeActivity extends BaseActivity {
	// 传递.公告默认显示那种公告.

	private PullToRefreshListView mOfficePullView;
	private ListView mOfficeView;

	private NoticeAdapter mOfficeNoticeAdapter;
	private RefreshListData<Notice> mOfficeNoticeList;

	private ZMSpaceService mZMSpaceService;

	private String mZMCode = "";
	private ZMObject mZMObject;

	private long mRegionId;
	private int mObjectType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_notice_activity);
		findView();
		setTopbar();
		init();

		Intent intent = getIntent();
		mZMCode = intent.getStringExtra(ACTIVITY_EXTRA);
		mZMObject = ScanningcodeService.getInstance(this).getCacheZMObject(mZMCode);
		if (mZMObject != null) {
			mRegionId = mZMObject.getCityId();
			mObjectType = mZMObject.getZMObjectType();
			getServiceData();
			setListener();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
			finish();
		}
	}

	private void setListener() {
		mOfficePullView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMSpaceService.getOfficialNoticeList(mRegionId, mObjectType, mOfficeNoticeList, NoticeActivity.this);
			}
		});

	}

	private void init() {
//		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMSpaceService = ZMSpaceService.getInstance(this);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_OFFICIAL_NOTICE_LIST_PROTOCOL) {
				// 获取官方公告
				if (protocol.isHandleSuccess()) {
					// 访问成功                          
					GetOfficialNoticeListProtocol p = (GetOfficialNoticeListProtocol) protocol;
					mOfficeNoticeList = p.getDataList();
					setOfficeView(mOfficeNoticeList.getDataList());
					mOfficePullView.setLastPage(mOfficeNoticeList.isLastPage());
				} else {

				}
				mOfficePullView.onRefreshComplete(mOfficePullView.hasPullFromTop());
			}
		} else {
			// TODO 网络访问失败
			mOfficePullView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}

	private void getServiceData() {
		mOfficeNoticeList = new RefreshListData<Notice>();
		startWaitingDialog(null, R.string.loading);
		mZMSpaceService.getOfficialNoticeList(mRegionId, mObjectType, mOfficeNoticeList, this);
	}

	private void setOfficeView(final ArrayList<Notice> notices) {
		if (mOfficeNoticeAdapter == null) {
			mOfficeNoticeAdapter = new NoticeAdapter(this, R.layout.space_notice_item, notices);
			mOfficeView.setAdapter(mOfficeNoticeAdapter);
			mOfficeView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Notice notice = mOfficeNoticeAdapter.getItem(position);
					openNoticeInfoActivity(notice.getId(), null);
				}
			});
			mOfficeNoticeAdapter.setOnClickerListener(photoClick);
		} else {
			mOfficeNoticeAdapter.setData(notices);
			mOfficeNoticeAdapter.notifyDataSetChanged();
		}
	}

	private void openNoticeInfoActivity(long id, String code) {
		Intent noticeIt = new Intent(NoticeActivity.this, NoticeInfoActivity.class);
		noticeIt.putExtra(ACTIVITY_EXTRA, id);
		if (code != null) {
			noticeIt.putExtra(NoticeInfoActivity.ACTIVITY_EXTRA_ZMCODE, code);
		}
		startActivity(noticeIt);
	}

	private OnClickListener photoClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.img_photo:
				String url = (String) v.getTag();
				if (url != null) {
					Intent it = new Intent(NoticeActivity.this, PhotoActivity.class);
					it.putExtra(PreviewActivity.ACTIVITY_URL, url);
					startActivity(it);
				}
				break;

			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void findView() {
		mOfficePullView = (PullToRefreshListView) findViewById(R.id.refresh_lstv_office);
		mOfficeView = mOfficePullView.getRefreshableView();
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.notice);
	}
}
