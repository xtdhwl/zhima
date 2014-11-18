package com.zhima.ui.space.activity;

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
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommerceProductProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommerceProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.ProductAdapter;

/**
 * @ClassName:ProductActivity
 * @Description:商品列表
 * @author liqilong
 * @date 2012-8-6 下午5:48:29
 * 
 */
public class ProductListActivity extends BaseActivity {
	protected static final String TAG = ProductListActivity.class.getSimpleName();
	/** 显示商品列表 */
	private GridView mProductView;
	private PullToRefreshGridView mPullProductView;
	private ProductAdapter mProductAdapter;
	private CommerceObject mCommerceObject;
	private CommerceService mCommerceService;
	/** 商品列表 */
	private RefreshListData<CommerceProduct> mProductListData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_product_activity);
		findId();
		setTopbar();
		init();

		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mCommerceObject = (CommerceObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId);

		if (mCommerceObject != null) {
			getServiceData();
			setListener();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	private void setListener() {
		mProductView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ProductListActivity.this, ProductInfoActivity.class);
				intent.putExtra(ProductInfoActivity.PRODUCT_POSITION, position);
				intent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
				startActivity(intent);
			}
		});
		mPullProductView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (mPullProductView.hasPullFromTop()) {
					// 刷新
					mCommerceService.getCommerceProductList(mCommerceObject, true, ProductListActivity.this);
				} else {
					// 加载新数据
					mCommerceService.getCommerceProductList(mCommerceObject, false, ProductListActivity.this);
				}
			}
		});
	}

	private void init() {
		mCommerceService = CommerceService.getInstance(this);
	}

	private void getServiceData() {
		// 获取商品列表
		mProductListData = mCommerceService.getCacheCommerceProductList(mCommerceObject);
		if (mProductListData.isEmpty()) {
			mCommerceService.getCommerceProductList(mCommerceObject, true, ProductListActivity.this);
		} else {
			setProductView();
		}
	}

	// 设置商品
	private void setProductView() {
		if (mProductAdapter == null) {
			mProductAdapter = new ProductAdapter(this, R.layout.space_image_show,
					mProductListData.getDataList());
			mProductView.setAdapter(mProductAdapter);
		} else {
			mProductAdapter.setData(mProductListData.getDataList());
			mProductAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_COMMERCE_PRODUCT_LIST_PROTOCOL) {
				// 获取协议结果
				if (protocol.isHandleSuccess()) {
					// 访问成功
					GetCommerceProductProtocol p = (GetCommerceProductProtocol) protocol;
					mProductListData = p.getDataList();
					setProductView();
					mPullProductView.setLastPage(mProductListData.isLastPage());
				} else {
				}
			}
			mPullProductView.onRefreshComplete(mPullProductView.hasPullFromTop());
		} else {
			// TODO 网络访问失败
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
			mPullProductView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO
	}

	private void findId() {
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
				// TODO Auto-generated method stub
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.product_view);
	}

}
