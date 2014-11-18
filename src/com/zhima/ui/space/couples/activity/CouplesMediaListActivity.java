package com.zhima.ui.space.couples.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetMultimediaListProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMCouplesService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.adapter.MultimediaAdapter;


/**
 * @ClassName CouplesMediaListActivity
 * @Description 情侣知天使影像列表
 * @author jiang
 * @date 2012-12-6 下午06:56:10
 */
public class CouplesMediaListActivity extends BaseActivity {
	public static final String ACTIVITY_POSITION = "position";

	private ZMCouplesService mZMCouplesService;
	private ScanningcodeService mScanningcodeService;

	private PullToRefreshGridView mPullGridView;
	private GridView mGridView;

	private RefreshListData<IdolAcqierement> mMultimediaRefreshList;
	private MultimediaAdapter mMultimediaAdapter;

	private ZMCouplesObject mZMCouplesObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_couplesmedia_list);
		findView();
		setTopbar();
		init();

		//Intent 传递id
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMCouplesObject = (ZMCouplesObject) mScanningcodeService.getCacheZMObject(id);
		if (mZMCouplesObject != null) {
			getServiceDate();
			setListener();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	private void setListener() {
		mPullGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMCouplesService.getMultimediaList(mZMCouplesObject, mPullGridView.hasPullFromTop(),
						CouplesMediaListActivity.this);
			}
		});
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMCouplesService = ZMCouplesService.getInstance(this);
	}

	private void getServiceDate() {
		//获取知天使影像
		mMultimediaRefreshList = mZMCouplesService.getCacheMultimediaList(mZMCouplesObject);
		if (mMultimediaRefreshList.isEmpty()) {
			mZMCouplesService.getMultimediaList(mZMCouplesObject, false, this);
		} else {
			setMultimediaView(mMultimediaRefreshList.getDataList());
		}
	}

	private void setMultimediaView(final ArrayList<IdolAcqierement> arrayList) {
		if (mMultimediaAdapter == null) {
			mMultimediaAdapter = new MultimediaAdapter(this, R.layout.space_album_item, arrayList);
			mGridView.setAdapter(mMultimediaAdapter);
			mGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (arrayList.size() > position) {
						ArrayList<String> urlArray = new ArrayList<String>();
						for (int i = 0; i < arrayList.size(); i++) {
							urlArray.add(arrayList.get(i).getContentUrl());
						}
						Intent acqierementIntent = new Intent(CouplesMediaListActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
						acqierementIntent.putExtra(IdoPhotoActivity.TOPBAR_BACKGROUND, R.color.red_couples_topbar);
						acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE,
								getText(R.string.multimedia_info));
						startActivity(acqierementIntent);
					}
				}
			});
		} else {
			mMultimediaAdapter.setData(arrayList);
			mMultimediaAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_COUPLES_MULTIMEDIA_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//  情侣知天使影音列表
					GetMultimediaListProtocol p = (GetMultimediaListProtocol) protocol;
					mMultimediaRefreshList = p.getDataList();
					setMultimediaView(mMultimediaRefreshList.getDataList());
					mPullGridView.setLastPage(mMultimediaRefreshList.isLastPage());
				} else {

				}
			} else {

			}
			mPullGridView.onRefreshComplete(mPullGridView.hasPullFromTop());
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullGridView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
	}

	private void findView() {
		mPullGridView = (PullToRefreshGridView) this.findViewById(R.id.refresh_grid);
		mGridView = mPullGridView.getRefreshableView();
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.couples_media_list);
	}
}
