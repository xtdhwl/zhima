package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.CommerceProtocolHandler.GainCouponProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommercePromotion;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.space.adapter.PromotionInfoAdapter;

/**
 * @ClassName:PromotionInfoActivity
 * @Description:活动详细信息
 * @author liqilong
 * @date 2012-8-13 下午3:59:16
 * 
 */
public class PromotionInfoActivity extends BaseActivity {
	protected static final String TAG = "PromotionInfoActivity";
	protected static final String PROMOTION_POSITION = "position";

	private ViewFlow mPromotionView;
	private int position;
	private CommerceObject mCommerceObject;
	private RefreshListData<CommercePromotion> mPromotionRefreshList;
	private PromotionInfoAdapter mPromotionInfoAdapter;
	private CommerceService mCommerceService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_promotion_info_activity);
		findId();
		setTopbar();
		init();

		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		position = intent.getIntExtra(PROMOTION_POSITION, 0);
		mCommerceObject = (CommerceObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId);

		if (mCommerceObject != null) {
			mPromotionRefreshList = mCommerceService.getCacheCommercePromotionList(mCommerceObject);
			setUpView(mPromotionRefreshList.getDataList());
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
			finish();
		}
	}

	private void init() {
		mCommerceService = CommerceService.getInstance(this);
	}

	private class MyHttpProtocol implements IHttpRequestCallback {
		CommercePromotion mCommercePromotion;
		View view;

		public MyHttpProtocol(CommercePromotion promotion, View view) {
			mCommercePromotion = promotion;
			this.view = view;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			if (protocol.isHttpSuccess()) {
				// 收藏卡片夹
				if (protocol.getProtocolType() == ProtocolType.GAIN_COUPON_PROTOCOL) {
					// 获取优惠券
					if (protocol.isHandleSuccess()) {
						HaloToast.show(getApplicationContext(), R.string.save_card_success);
						GainCouponProtocolHandler p = (GainCouponProtocolHandler) protocol;
						int count = p.getRemainCount();
						mCommercePromotion.setRemainCount(count);
						TextView tv = (TextView) view.findViewById(R.id.txt_remain);
						if(count < 0){
							tv.setText(getApplication().getString(R.string.remain) + ":" + "不限");
						}else{
							tv.setText(getApplication().getString(R.string.remain) + ":" + count);
						}
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				}
			} else {
				// TODO 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	private void setUpView(ArrayList<CommercePromotion> arrayList) {
		if (mPromotionInfoAdapter == null) {
			mPromotionInfoAdapter = new PromotionInfoAdapter(this, R.layout.space_promotion_info_item, arrayList);
			mPromotionInfoAdapter.setOnClickerListener(previewClick);
			mPromotionView.setAdapter(mPromotionInfoAdapter, position);
		} else {
			mPromotionInfoAdapter.setData(arrayList);
			mPromotionInfoAdapter.notifyDataSetChanged();
		}
	}

	//预览大图
	private View.OnClickListener previewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommercePromotion data = (CommercePromotion) v.getTag();
			Intent it = new Intent(PromotionInfoActivity.this, PreviewActivity.class);
			it.putExtra(PreviewActivity.ACTIVITY_URL, data.getImageUrl());
			startActivity(it);
		}
	};

	private View.OnClickListener queueTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//领取活动
			if (mCommerceObject != null && mPromotionInfoAdapter != null) {
				int position = mPromotionView.getSelectedItemPosition();
				if (mPromotionInfoAdapter.getCount() > position) {
					CommercePromotion promotion = mPromotionInfoAdapter.getItem(position);
					View view = mPromotionView.getSelectedView();
					mCommerceService.gainCoupon(promotion.getActionId(), new MyHttpProtocol(promotion, view));
				} else {
					HaloToast.show(getApplicationContext(), R.string.gain_fail, 0);
				}
			} else {
				HaloToast.show(getApplicationContext(), R.string.gain_fail, 0);
			}
		}
	};

	private void findId() {
		mPromotionView = (ViewFlow) findViewById(R.id.flow_promotion);
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_queue);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(queueTopbarClick);

		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.promotion_particular);

	}

}
