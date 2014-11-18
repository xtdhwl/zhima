package com.zhima.ui.usercenter.watchdog.activity;

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
import com.zhima.data.model.UserCoupon;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName MyCouponInfoActivity
 * @Description 卡片夹详情（左右切换形式展现）
 * @author jiang
 * @date 2013-1-21 上午10:51:28
 */
public class MyCouponInfoActivity extends BaseActivity {
	protected static final String TAG = "MyCouponInfoActivity";

	private ViewFlow mUserCouponView;
	private int position;
	// private RefreshListData<UserCoupon> mUserCouponRefreshList;
	private ArrayList<UserCoupon> mUserCouponList;
	private MyCouponInfoAdapter mUserCouponInfoAdapter;
	private UserService mUserService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_promotion_info_activity);
		setTopbar();

		Intent intent = getIntent();
		position = intent.getIntExtra(ACTIVITY_EXTRA, 0);

		ArrayList<UserCouponParcelable> parcelableList = intent.getParcelableArrayListExtra(ACTIVITY_EXTRA2);

		mUserCouponList = new ArrayList<UserCoupon>();
		for (UserCouponParcelable couponPar : parcelableList) {
			UserCoupon userCoupon = new UserCoupon();
			userCoupon.setName(couponPar.name);
			userCoupon.setDescription(couponPar.description);
			userCoupon.setImageUrl(couponPar.imageUrl);
			userCoupon.setActionId(couponPar.mId);
			userCoupon.setBeginTime(couponPar.beginTime);
			userCoupon.setDeadlineTime(couponPar.deadlineTime);
			mUserCouponList.add(userCoupon);
		}
		init();
	}

	/**
	 * @Title: init
	 * @Description:初始化
	 * @param
	 * @return void
	 */
	private void init() {
		mUserService = UserService.getInstance(this);
		mUserCouponView = (ViewFlow) findViewById(R.id.flow_promotion);
		// mUserCouponRefreshList = mUserService.getMyCacheCouponList();
		if (!mUserCouponList.isEmpty()) {
			setView(mUserCouponList);
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
			finish();
		}
	}

	private void setView(ArrayList<UserCoupon> list) {
		mUserCouponInfoAdapter = new MyCouponInfoAdapter(this, R.layout.space_promotion_info_item, list);
		mUserCouponView.setAdapter(mUserCouponInfoAdapter, position);
	}

	/** 删除选项的监听 */
	private View.OnClickListener deleteTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MessageDialog dialog = new MessageDialog(MyCouponInfoActivity.this, mUserCouponView);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.show();
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onLeftBtClick() {
				}

				@Override
				public void onRightBtClick() {
					if (mUserCouponList != null) {
						int position = mUserCouponView.getSelectedItemPosition();
						if (mUserCouponList.size() > position) {
							UserCoupon userCoupon = mUserCouponList.get(position);
							startWaitingDialog("", R.string.loading);
							mUserService.deleteMyCoupon(userCoupon.getActionId(), new MyHttpProtocol(userCoupon,
									position));
						} else {
							ErrorManager.showErrorMessage(getApplicationContext());
						}
					}
				}
			});

		}
	};

	/** 删除网络请求的回调 */
	/**
	 * @ClassName: MyHttpProtocol
	 * @Description: TODO
	 * @author luqilong
	 * @date 2012-10-13 上午11:24:29
	 */
	private class MyHttpProtocol implements IHttpRequestCallback {
		UserCoupon mUserCoupon;

		public MyHttpProtocol(UserCoupon promotion, int position) {
			mUserCoupon = promotion;
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
					if (protocol.isHandleSuccess()) {
						setResult(1);
						if (mUserCouponList.size() > 0) {
							mUserCouponInfoAdapter.setData(mUserCouponList);
							mUserCouponInfoAdapter.notifyDataSetChanged();
							mUserCouponView.setSelection(mUserCouponView.getSelectedItemPosition());
						} else {
							finish();
						}
						HaloToast.show(getApplicationContext(), "删除成功");
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				}
			} else {
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	/**
	 * @Title: setTopbar
	 * @Description:设置topbar
	 * @param
	 * @return void
	 */
	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.rubbish);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(deleteTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.my_coupon);

	}

}
