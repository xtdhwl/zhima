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
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.CommerceProtocolHandler.GetMyCouponOfCommerceListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.adapter.CouponAdatper;

/**
* @ClassName: CouponListActivity
* @Description: 用户优惠券列表
* @author luqilong
* @date 2012-10-9 上午10:12:59
 */
public class CouponListActivity extends BaseActivity {

	private PullToRefreshGridView mPullCouponiView;
	private GridView mPromotionView;
	private CommerceService mCommerceService;
	private CommerceObject mCommerceObject;
	private RefreshListData<UserCoupon> mPromotionRefreshList;
	private CouponAdatper mCouponInfoAdapter;

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
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCommerceObject != null) {
			getServerData();
			setListener();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			mCouponInfoAdapter.notifyDataSetChanged();
		}
	}

	private void setListener() {
		mPullCouponiView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mCommerceService.getMyCouponOfCommerceList(mCommerceObject, mPullCouponiView.hasPullFromTop(),
						CouponListActivity.this);
			}
		});
		mPromotionView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent promotionIntent = new Intent(CouponListActivity.this, CouponInfoActivity.class);
				promotionIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
				promotionIntent.putExtra(CouponInfoActivity.PROMOTION_POSITION, position);
				startActivityForResult(promotionIntent, 0);
			}
		});

	}

	private void getServerData() {
		//获取商家优惠券 
		mPromotionRefreshList = mCommerceService.getCacheMyCouponOfCommerceList(mCommerceObject);
		if (mPromotionRefreshList.isEmpty()) {
			mCommerceService.getMyCouponOfCommerceList(mCommerceObject, true, CouponListActivity.this);
		} else {
			setCouponView(mPromotionRefreshList.getDataList());
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		//TODO 网络访问之前 
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			// 获取商家活动列表
			if (protocol.getProtocolType() == ProtocolType.GET_MYCOUPON_OF_COMMERCE_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//获取我的优惠券成功,
					GetMyCouponOfCommerceListProtocol p = (GetMyCouponOfCommerceListProtocol) protocol;
					mPromotionRefreshList = p.getDataList();
					ArrayList<UserCoupon> userCouponList = mPromotionRefreshList.getDataList();
					setCouponView(userCouponList);
					mPullCouponiView.setLastPage(mPromotionRefreshList.isLastPage());
					if (userCouponList.size() < 1) {
						HaloToast.show(this, R.string.coupon_enpty);
					}
				} else {
					//获取我的优惠券失败
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
				mPullCouponiView.onRefreshComplete(mPullCouponiView.hasPullFromTop());
			}
		} else {
			// 网络访问失败
			HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			mPullCouponiView.setEmptyView();
		}
	}

	private void setCouponView(ArrayList<UserCoupon> arrayList) {
		if (mCouponInfoAdapter == null) {
			mCouponInfoAdapter = new CouponAdatper(this, R.layout.space_business_promotion_item, arrayList);
			mPromotionView.setAdapter(mCouponInfoAdapter);
		} else {
			mCouponInfoAdapter.setData(arrayList);
			mCouponInfoAdapter.notifyDataSetChanged();
		}
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("我的优惠券");
	}

	private void findId() {
		mPullCouponiView = (PullToRefreshGridView) findViewById(R.id.refresh_grid);
		mPromotionView = mPullCouponiView.getRefreshableView();
	}

}
