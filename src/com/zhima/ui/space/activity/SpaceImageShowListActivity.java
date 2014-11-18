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
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.SpacePhotoListAdapter;

/**
 * @ClassName SpaceImageShowListActivity
 * @Description 公共空间图片列表
 * @author jiang
 * @date 2012-9-20 下午12:42:26
 */
public class SpaceImageShowListActivity extends BaseActivity {
	private GridView mProductView;
	private PullToRefreshGridView mPullProductView;
	private SpacePhotoListAdapter mProductAdapter;
	private ZMObject mZMObject;

	private ScanningcodeService mScanningcodeService;
	private RefreshListData<ZMObjectImage> mProductListData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_product_activity);
		findView();
		setTopbar();

		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = (ZMObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId);
		init();
		setListener();
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		requsetData();
	}

	private void requsetData() {
		// 获取图片列表
		mProductListData = mScanningcodeService.getCacheZMObjectAlbumList(mZMObject.getId());
		if (mProductListData.isEmpty()) {
			mScanningcodeService.getZMObjectAlbumList(mZMObject, true, this);
		} else {
			setProductView(mProductListData.getDataList());
		}
	}

	private void setListener() {
		// TODO 点击商品
		mProductView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(SpaceImageShowListActivity.this, SpaceImageShowInfoActivity.class);
				intent.putExtra(ProductInfoActivity.PRODUCT_POSITION, position);
				intent.putExtra(ACTIVITY_EXTRA, mZMObject.getId());
				startActivity(intent);
			}
		});
		
		mPullProductView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
					// 刷新
					mScanningcodeService.getZMObjectAlbumList(mZMObject, mPullProductView.hasPullFromTop(), SpaceImageShowListActivity.this);
					// 加载新数据
			}
		});
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
				// 获取图片展示列表
				if (protocol.isHandleSuccess()) {
					GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
					mProductListData = p.getDataList();
					setProductView(mProductListData.getDataList());
					mPullProductView.setLastPage(mProductListData.isLastPage());
				} else {

				}
				mPullProductView.onRefreshComplete(mPullProductView.hasPullFromTop());
			}
		} else {
			mPullProductView.onRefreshComplete(mPullProductView.hasPullFromTop());
		}
	}

	// 设置图片展示
	private void setProductView(ArrayList<ZMObjectImage> list) {
		if (mProductAdapter == null) {
			mProductAdapter = new SpacePhotoListAdapter(this, R.layout.space_image_show, list);
			mProductView.setAdapter(mProductAdapter);
		} else {
			mProductAdapter.setData(list);
			mProductAdapter.notifyDataSetChanged();
		}
	}

	private void findView() {
		mPullProductView = (PullToRefreshGridView) findViewById(R.id.refresh_grid);
		mProductView = mPullProductView.getRefreshableView();
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.image_list);
	}

}
