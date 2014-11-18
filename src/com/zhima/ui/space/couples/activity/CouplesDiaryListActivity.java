package com.zhima.ui.space.couples.activity;

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
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetJournalListProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMCouplesService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.adapter.AcqierementAdapter;


/**
 * @ClassName CouplesDiaryListActivity
 * @Description 情侣知天使日志列表
 * @author jiang
 * @date 2012-12-6 下午06:55:29
 */
public class CouplesDiaryListActivity extends BaseActivity {
	protected static final String TAG = null;
	private PullToRefreshListView mAcqierementPullView;
	private ScanningcodeService mScanningcodeService;

	//	private IdolAcqierement mAcqierement;
	private ZMCouplesObject mZMCouplesObject;
	private ZMCouplesService mZMCouplesService;
	private RefreshListData<IdolAcqierement> mAcqierementRefreshList;
	private AcqierementAdapter mAcqierementAdapter;
	private ListView mAcqierementListView;
	public static final String TOPBAR_TITLE = "topbar_title";
	private String title = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_couplesdiarylist_activity);

		findView();
		init();

		//Intent 传递id
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		title = intent.getStringExtra(TOPBAR_TITLE);

		setTopbar();
		mZMCouplesObject = (ZMCouplesObject) mScanningcodeService.getCacheZMObject(id);
		if (mZMCouplesObject != null) {
			getServiceDate();
			setListener();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	private void setListener() {
		mAcqierementPullView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMCouplesService.getCouplesJournalList(mZMCouplesObject, mAcqierementPullView.hasPullFromTop(),
						CouplesDiaryListActivity.this);
			}
		});
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_COUPLES_JOURNAL_CHANNEL_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到知天使频道列表
					GetJournalListProtocol p = (GetJournalListProtocol) protocol;
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
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}

	private void getServiceDate() {
		//知天使频道列表
		mAcqierementRefreshList = mZMCouplesService.getCacheCouplesJournalList(mZMCouplesObject);
		if (mAcqierementRefreshList.isEmpty()) {
			mZMCouplesService.getCouplesJournalList(mZMCouplesObject, true, this);
		} else {
			setAcqierementView(mAcqierementRefreshList.getDataList());
		}
	}

	/**设置频道详情*/
	private void setAcqierementView(final ArrayList<IdolAcqierement> arrayList) {
		if (mAcqierementAdapter == null) {
			mAcqierementAdapter = new AcqierementAdapter(this, R.layout.space_acqierement_item, arrayList);
			mAcqierementListView.setAdapter(mAcqierementAdapter);
			mAcqierementListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//频道详情
					if (arrayList.size() > position) {
						ArrayList<String> urlArray = new ArrayList<String>();
						for (int i = 0; i < arrayList.size(); i++) {
							urlArray.add(arrayList.get(i).getContentUrl());
						}
						Intent acqierementIntent = new Intent(CouplesDiaryListActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
						acqierementIntent.putExtra(IdoPhotoActivity.TOPBAR_BACKGROUND, R.color.red_couples_topbar);
						acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE,
								getText(R.string.diary_info));
						startActivity(acqierementIntent);
					}
				}
			});
		} else {
			mAcqierementAdapter.setData(arrayList);
			mAcqierementAdapter.notifyDataSetChanged();
		}
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		mTopbar.setBackgroundResource(R.color.red_couples_topbar);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(title);

	}

	private void findView() {
		mAcqierementPullView = (PullToRefreshListView) findViewById(R.id.refresh_lstv);
		mAcqierementListView = mAcqierementPullView.getRefreshableView();
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMCouplesService = ZMCouplesService.getInstance(this);
	}
}
