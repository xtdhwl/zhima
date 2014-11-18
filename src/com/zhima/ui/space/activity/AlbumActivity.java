package com.zhima.ui.space.activity;

import java.util.List;

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
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.ImageAlbumAdapter;

/**
* @ClassName: AlbumActivity
* @Description: 用户相册
* @author luqilong
* @date 2012-9-21 下午6:19:17
 */
public class AlbumActivity extends BaseActivity {

	private ImageAlbumAdapter mAlbumAdapter;
	private RefreshListData<ZMObjectImage> mRefreshPhotoList;
	private List<ZMObjectImage> mPhotoList;
	private PullToRefreshGridView mPullGridView;
	private GridView mGridView;
	private ScanningcodeService mScanningcodeService;
	private ZMObject mZMObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_album);
		findView();
		setTopbar();
		init();

		//Intent 传递id
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = mScanningcodeService.getCacheZMObject(id);
		if (mZMObject != null) {
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
		mPullGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mScanningcodeService.getZMObjectAlbumList(mZMObject, mPullGridView.hasPullFromTop(), AlbumActivity.this);
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent it = new Intent(AlbumActivity.this, PhotoActivity.class);
				it.putExtra(ACTIVITY_EXTRA, mZMObject.getId());
				it.putExtra(PhotoActivity.ACTIVITY_POSITION, position);
				startActivity(it);
			}
		});
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
	}

	private void getServiceDate() {
		// 获取商户相册
		mRefreshPhotoList = mScanningcodeService.getCacheZMObjectAlbumList(mZMObject.getId());
		if (mRefreshPhotoList.isEmpty()) {
			mScanningcodeService.getZMObjectAlbumList(mZMObject, false, this);
		} else {
			setUpView();
		}
	}

	private void setUpView() {
		mPhotoList = mRefreshPhotoList.getDataList();
		if (mAlbumAdapter == null) {
			mAlbumAdapter = new ImageAlbumAdapter(this, R.layout.space_album_item, mPhotoList);
			mGridView.setAdapter(mAlbumAdapter);
		} else {
			mAlbumAdapter.setData(mPhotoList);
			mAlbumAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
				// 获取协议结果
				if (protocol.isHandleSuccess()) {
					// TODO 设置相册
					GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
					mRefreshPhotoList = p.getDataList();
					setUpView();
					mPullGridView.setLastPage(mRefreshPhotoList.isLastPage());
				} else {

				}
				mPullGridView.onRefreshComplete(mPullGridView.hasPullFromTop());
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullGridView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.album);
	}

}
