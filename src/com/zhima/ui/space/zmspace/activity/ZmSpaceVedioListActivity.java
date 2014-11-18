package com.zhima.ui.space.zmspace.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetMultimediaListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceImageVideoListProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMOrganizationObject;
import com.zhima.data.model.ZMSpaceVideo;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMCouplesService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.adapter.MultimediaAdapter;
import com.zhima.ui.space.organization.activity.OrganizationMediaListActivity;
import com.zhima.ui.space.zmspace.adapter.ZmSpaceVedioAdapter;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 芝麻空间_影像列表
 * @ClassName: ZmSpaceVedioListActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-31 下午2:19:58
*/
public class ZmSpaceVedioListActivity extends BaseActivity {

	private ZMSpaceService mZMSpaceService;
	private ScanningcodeService mScanningcodeService;
	
	private PullToRefreshGridView mPullGridView;
	private GridView mGridView;
	
	private RefreshListData<ZMSpaceVideo> mMultimediaRefreshList;
	private ZmSpaceVedioAdapter mZmSpaceVedioAdapter;
	
	private ZMObject mZMObject;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_organization_media_list);
		
		init();
		findView();
		setTopbar();
		
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMSpaceService = ZMSpaceService.getInstance(this);
		
		//Intent 传递id
				Intent intent = getIntent();
				long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
				mZMObject = mScanningcodeService.getCacheZMObject(id);
				if (mZMObject != null) {
					getServiceData();
					setListener();
				} else {
					ErrorManager.showErrorMessage(getApplicationContext());
				}
	}

	private void setListener() {
		mPullGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMSpaceService.getImageVideoList(mZMObject, mPullGridView.hasPullFromTop(),
						ZmSpaceVedioListActivity.this);
			}
		});
	}
	
	private void setTopbar() {
		//TODO
		ViewInitTools.setTopBar(this, "影像列表", View.GONE, null);
	}

	private void findView() {
		mPullGridView = (PullToRefreshGridView) this.findViewById(R.id.refresh_grid);
		mGridView = mPullGridView.getRefreshableView();
	}

	
	private void getServiceData() {
		//获取知天使影像
//		mMultimediaRefreshList = mZMSpaceService.getCacheImagePhotoList(mZMObject);
//		if (mMultimediaRefreshList.isEmpty()) {
			mZMSpaceService.getImageVideoList(mZMObject, false, this);
//		} else {
//			setMultimediaView(mMultimediaRefreshList.getDataList());
//		}
	}
	
	private void setMultimediaView(final ArrayList<ZMSpaceVideo> arrayList) {
		if (mZmSpaceVedioAdapter == null) {
			mZmSpaceVedioAdapter = new ZmSpaceVedioAdapter(this, R.layout.space_album_item, arrayList);
			mGridView.setAdapter(mZmSpaceVedioAdapter);
			mGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (arrayList.size() > position) {
						ArrayList<String> urlArray = new ArrayList<String>();
						for (int i = 0; i < arrayList.size(); i++) {
							urlArray.add(arrayList.get(i).getContentUrl());
						}
						Intent acqierementIntent = new Intent(ZmSpaceVedioListActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
						acqierementIntent.putExtra(IdoPhotoActivity.TOPBAR_BACKGROUND, R.color.orange_organization_topbar);
						acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE,
								getText(R.string.multimedia_info));
						startActivity(acqierementIntent);
					}
				}
			});
		} else {
			mZmSpaceVedioAdapter.setData(arrayList);
			mZmSpaceVedioAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SPACE_IMAGE_VIDEO_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//  情侣知天使影音列表
					GetSpaceImageVideoListProtocol p = (GetSpaceImageVideoListProtocol) protocol;
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
		dismissWaitingDialog();
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
		startWaitingDialog("", "正在加载...");
	}
	
}
