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
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.space.adapter.CouponInfoAdapter;

/**
 * @ClassName: CouponInfoActivity
 * @Description: 查看用户优惠券
 * @author luqilong
 * @date 2012-9-21 下午11:43:52
 */
public class CouponInfoActivity extends BaseActivity {

	protected static final String TAG = "CouponInfoActivity";
	protected static final String PROMOTION_POSITION = "position";

	private ViewFlow mPromotionView;
	private int position;
	private CommerceObject mCommerceObject;
	private RefreshListData<UserCoupon> mPromotionRefreshList;
	private ArrayList<UserCoupon> mPromotionList;
	private CouponInfoAdapter mCouponInfoAdapter;
	private CommerceService mCommerceService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_promotion_info_activity);
		findView();
		setTopbar();
		init();

		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		position = intent.getIntExtra(PROMOTION_POSITION, 0);
		mCommerceObject = (CommerceObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId);

		if (mCommerceObject != null) {
			setView();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	private void init() {
		mCommerceService = CommerceService.getInstance(this);
	}

	private class MyHttpProtocol implements IHttpRequestCallback {
		//		UserCoupon mCoupon;

		public MyHttpProtocol(UserCoupon coupon) {
			//			mCoupon = coupon;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 删除优惠券
				if (protocol.getProtocolType() == ProtocolType.DELETE_MYCOUPON_PROTOCOL) {
					// 删除优惠券成功
					if (protocol.isHandleSuccess()) {
						setResult(1);
						mPromotionList = mPromotionRefreshList.getDataList();
						if (mPromotionList.size() > 0) {
							mCouponInfoAdapter.setData(mPromotionList);
							mCouponInfoAdapter.notifyDataSetChanged();
							mPromotionView.setSelection(mPromotionView.getSelectedItemPosition());
						} else {
							//返回 1 说明删除过需要优惠券列表需要更新
							finish();
						}
						HaloToast.show(getApplicationContext(), R.string.delete_success);
					} else {
						// 删除优惠券失败
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	private void setView() {
		mPromotionRefreshList = mCommerceService.getCacheMyCouponOfCommerceList(mCommerceObject);
		//获取优惠券.如果不是空设置View
		if (!mPromotionRefreshList.isEmpty()) {
			mPromotionList = mPromotionRefreshList.getDataList();
			mCouponInfoAdapter = new CouponInfoAdapter(this, R.layout.space_promotion_info_item, mPromotionList);
			mPromotionView.setAdapter(mCouponInfoAdapter, position);
		}
	}

	/** 删除优惠券 */
	private View.OnClickListener deleteCouponClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MessageDialog dialog = new MessageDialog(CouponInfoActivity.this, mPromotionView);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.show();
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					if (mPromotionList != null) {
						int position = mPromotionView.getSelectedItemPosition();
						if (mPromotionList.size() > position) {
							UserCoupon coupon = mPromotionList.get(position);
							startWaitingDialog("", R.string.loading);
							CommerceService.getInstance(CouponInfoActivity.this).deleteMyCoupon(coupon.getActionId(),
									new MyHttpProtocol(coupon));
						} else {
							HaloToast.show(getApplicationContext(), R.string.delete_failed, 0);
						}
					}
				}

				@Override
				public void onLeftBtClick() {
				}
			});
		}
	};

	private void findView() {
		mPromotionView = (ViewFlow) findViewById(R.id.flow_promotion);
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.addRightLayoutView(ll_right);

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.rubbish);

		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(deleteCouponClick);

		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.my_coupon);

	}
}
