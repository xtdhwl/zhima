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
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetAcqierementListProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetChannelListByKindProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMIdolService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.AcqierementAdapter;

/**
 * @ClassName:IdolAcqierementActivity
 * @Description:知天使才艺频道
 * @author liqilong
 * @date 2012-8-15 下午4:15:36
 * 
 */
public class AcqierementListActivity extends BaseActivity {
	protected static final String TAG = null;
	private PullToRefreshListView mAcqierementPullView;
	private ScanningcodeService mScanningcodeService;

	//	private IdolAcqierement mAcqierement;
	private ZMIdolObject mZMIdolObject;
	private ZMIdolService mZMIdolService;
	private RefreshListData<IdolAcqierement> mAcqierementRefreshList;
	private AcqierementAdapter mAcqierementAdapter;
	private ListView mAcqierementListView;
	public static final String TOPBAR_TITLE = "topbar_title";
	private String title = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_acqierementlist_activity);

		findView();
		init();

		//Intent 传递id
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		title = intent.getStringExtra(TOPBAR_TITLE);
		
		setTopbar();
		mZMIdolObject = (ZMIdolObject) mScanningcodeService.getCacheZMObject(id);
		if (mZMIdolObject != null) {
			getServiceDate();
			setListener();
		} else {
			if (NetUtils.isNetworkAvailable(this)) {
				HaloToast.show(this, R.string.gain_fail, 0);
			} else {
				HaloToast.show(this, R.string.network_request_failed, 0);
			}
		}
	}

	private void setListener() {
		mAcqierementPullView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMIdolService.getChannelListByKind(mZMIdolObject, mAcqierementPullView.hasPullFromTop(),
						AcqierementListActivity.this);
			}
		});
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_IDOL_CHANNEL_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到知天使频道列表
					GetChannelListByKindProtocol p = (GetChannelListByKindProtocol) protocol;
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
		mAcqierementRefreshList = mZMIdolService.getCacheAcqierementList(mZMIdolObject);
		if (mAcqierementRefreshList.isEmpty()) {
			mZMIdolService.getChannelListByKind(mZMIdolObject, true, this);
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
						Intent acqierementIntent = new Intent(AcqierementListActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
						acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE,
								getText(R.string.acqierement_info));
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
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
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
		mZMIdolService = ZMIdolService.getInstance(this);
	}
}
