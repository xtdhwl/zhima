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
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommercePromotionListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommercePromotion;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.PromotionCardAdapter;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;

/**
 * @ClassName:PromotionListActivity
 * @Description:TODO 商家活动列表信息
 * @author liqilong
 * @date 2012-7-23 上午11:46:46
 * 
 */
public class PromotionListActivity extends BaseActivity {
	private PullToRefreshGridView mPullPromotionView;
	private GridView mPromotionView;
	private CommerceObject mCommerceObject;

	private PromotionCardAdapter mPromotionAdapter;
	private RefreshListData<CommercePromotion> mPromotionList;

	private CommerceService mCommerceService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_product_activity);
		findId();
		mCommerceService = CommerceService.getInstance(this);
		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		setTopbar();
		mCommerceObject = (CommerceObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId);
		if (mCommerceObject != null) {
			getServerData();
			setListener();
		} else {
			if(NetUtils.isNetworkAvailable(this)){
				HaloToast.show(getApplicationContext(), R.string.gain_fail, 0);
			}else{
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
			finish();
		}
	}


	private void getServerData() {
		mPromotionList = CommerceService.getInstance(this).getCacheCommercePromotionList(mCommerceObject);
		if(mPromotionList.isEmpty()){
			mCommerceService.getCommercePromotionList(mCommerceObject, false, PromotionListActivity.this);
		}else{
			setPromotionView();
		}
	}


	private void setListener() {
		// 点击商品活动
		mPromotionView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent promotionIntent = new Intent(PromotionListActivity.this, PromotionInfoActivity.class);
				promotionIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
				promotionIntent.putExtra(PromotionInfoActivity.PROMOTION_POSITION, position);
				startActivity(promotionIntent);
			}
		});
		mPullPromotionView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (mPullPromotionView.hasPullFromTop()) {
					// 刷新
					mCommerceService.getCommercePromotionList(mCommerceObject, true, PromotionListActivity.this);
				} else {
					// 加载新数据
					mCommerceService.getCommercePromotionList(mCommerceObject, false, PromotionListActivity.this);
				}
			}
		});
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_COMMERCE_PROMOTION_LIST_PROTOCOL) {
				// 获取商户活动列表协议
				if (protocol.isHandleSuccess()) {
					// 访问成功
					GetCommercePromotionListProtocol p = (GetCommercePromotionListProtocol) protocol;
					mPromotionList = p.getDataList();
					setPromotionView();
					mPullPromotionView.setLastPage(mPromotionList.isLastPage());
				} else {
				}
				mPullPromotionView.onRefreshComplete(mPullPromotionView.hasPullFromTop());
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
			mPullPromotionView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 
	}
	
	
	private void setPromotionView() {
		if (mPromotionAdapter == null) {
			mPromotionAdapter = new PromotionCardAdapter(this, R.layout.space_business_promotion_item,
					mPromotionList.getDataList(), true);
			mPromotionView.setAdapter(mPromotionAdapter);
		} else {
			mPromotionAdapter.setData(mPromotionList.getDataList());
			mPromotionAdapter.notifyDataSetChanged();
		}
	}

	

	private void findId() {
		mPullPromotionView = (PullToRefreshGridView) findViewById(R.id.refresh_grid);
		mPromotionView = mPullPromotionView.getRefreshableView();
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.business_promotion_view);

	}
}
