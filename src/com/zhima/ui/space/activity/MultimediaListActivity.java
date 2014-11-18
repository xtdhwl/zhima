package com.zhima.ui.space.activity;

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
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetMultimediaListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMIdolService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.MultimediaAdapter;

/**
 * @ClassName: MultimediaListActivity
 * @Description: 影像列表
 * @author luqilong
 * @date 2012-9-22 下午5:37:25
 */
public class MultimediaListActivity extends BaseActivity {
	public static final String ACTIVITY_POSITION = "position";

	private ZMIdolService mZMIdolService;
	private ScanningcodeService mScanningcodeService;

	private PullToRefreshGridView mPullGridView;
	private GridView mGridView;

	private RefreshListData<IdolAcqierement> mMultimediaRefreshList;
	private MultimediaAdapter mMultimediaAdapter;

	private ZMIdolObject mZMIdolObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_album);
		findView();
		setTopbar();
		init();

		// Intent 传递id
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMIdolObject = (ZMIdolObject) mScanningcodeService.getCacheZMObject(id);
		if (mZMIdolObject != null) {
			getServiceDate();
			setListener();
		} else {
			if (NetUtils.isNetworkAvailable(this)) {
				HaloToast.show(this, R.string.network_request_failed, 0);
			} else {
				HaloToast.show(this, R.string.gain_fail, 0);
			}
		}
	}

	private void setListener() {
		mPullGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMIdolService.getMultimediaList(mZMIdolObject, mPullGridView.hasPullFromTop(),
						MultimediaListActivity.this);
			}
		});
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMIdolService = ZMIdolService.getInstance(this);
	}

	private void getServiceDate() {
		// 获取知天使影像
		mMultimediaRefreshList = mZMIdolService.getCacheMultimediaList(mZMIdolObject);
		if (mMultimediaRefreshList.isEmpty()) {
			mZMIdolService.getMultimediaList(mZMIdolObject, false, this);
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
					ArrayList<String> urlArray = new ArrayList<String>();
					for (int i = 0; i < arrayList.size(); i++) {
						urlArray.add(arrayList.get(i).getContentUrl());
					}
					Intent acqierementIntent = new Intent(MultimediaListActivity.this, IdoPhotoActivity.class);
					acqierementIntent.putStringArrayListExtra(ACTIVITY_EXTRA, urlArray);
					acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
					acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE,
							getText(R.string.multimedia_info));
					startActivity(acqierementIntent);
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
			if (protocol.getProtocolType() == ProtocolType.GET_IDOL_MULTIMEDIA_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// TODO 获取知天使影音列表
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
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.multimedia_info);
	}
}
