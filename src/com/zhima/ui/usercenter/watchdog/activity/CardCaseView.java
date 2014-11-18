package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetMyCouponListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName CardCaseView
 * @Description 卡片夹
 * @author jiang
 * @date 2012-9-23 下午06:16:37
 */
public class CardCaseView {
	private Context mContext;
	private View mContentView;
	private RefreshListData<UserCoupon> userCouponList;
	private UserService mUserService;
	private LinearLayout mLoadingbr;
	private GridView mCardCaseView;
	private MyCouponAdapter myCouponAdapter;
	/** 是否最后一页 */
	private boolean isLastPage = false;
	/** 是否结束请求服务器 */
	private boolean isRequestFinish = false;

	public CardCaseView(Context context) {
		this.mContext = context;
		mContentView = View.inflate(mContext, R.layout.myzhima_cardcase, null);
		(mContentView.findViewById(R.id.ztop_bar_layout)).setVisibility(View.GONE);

		initView();
	}

	/**
	 * @Title: getView
	 * @Description:获取当前的view
	 * @param
	 * @return View
	 */
	public View getView() {
		return mContentView;
	}

	/**
	 * @Title: initView
	 * @Description:初始化
	 * @param
	 * @return void
	 */
	private void initView() {
		mUserService = UserService.getInstance(mContext);
		mLoadingbr = (LinearLayout) mContentView.findViewById(R.id.basic_view_loading);
		mCardCaseView = (GridView) mContentView.findViewById(R.id.refresh_grid);
		mCardCaseView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						if (isLastPage) {
							if (!isRequestFinish) {
								HaloToast.show(mContext, R.string.no_more_data);
							}
							isRequestFinish = true;
						} else {
							mLoadingbr.setVisibility(View.VISIBLE);
							mUserService.getMyCouponList(false, callBack);
						}
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		mCardCaseView.setOnItemClickListener(itemClick);
	}

	/**
	 * @Title: requestServiceData
	 * @Description:请求获取数据
	 * @param
	 * @return void
	 */
	public void requestServiceData() {
		userCouponList = mUserService.getMyCacheCouponList();
		if (userCouponList.isEmpty()) {
			showContentEmpty(false, false);
			mLoadingbr.setVisibility(View.VISIBLE);
			mUserService.getMyCouponList(true, callBack);
		} else {
			mLoadingbr.setVisibility(View.GONE);
			setView(userCouponList.getDataList());
		}
	}

	/**
	 * @Title: setView
	 * @Description:设置view的显示
	 * @param list
	 *            优惠券数据list
	 * @return void
	 */
	private void setView(ArrayList<UserCoupon> list) {
		myCouponAdapter = new MyCouponAdapter(mContext, R.layout.space_business_promotion_item, list);
		mCardCaseView.setAdapter(myCouponAdapter);
		MyZhimaViewflowAdapter.ISFIRST_CARD = false;
	}

	/** item点击的监听 */
	private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int positon, long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(mContext, MyCouponInfoActivity.class);
			intent.putExtra(BaseActivity.ACTIVITY_EXTRA, positon);
			((MyZhiMaMainActivity) mContext).startActivityForResult(intent, 1);
		}
	};

	/** 服务器请求回调 */
	private IHttpRequestCallback callBack = new IHttpRequestCallback() {

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub
			if (protocol.isHttpSuccess()) {
				if (protocol.getProtocolType() == ProtocolType.GET_MYCOUPON_LIST_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						GetMyCouponListProtocol p = (GetMyCouponListProtocol) protocol;
						userCouponList = p.getDataList();
						setView(userCouponList.getDataList());
						if (userCouponList.isLastPage()) {
							isLastPage = true;
						}
						if (userCouponList.getDataList().size() > 0) {
							mCardCaseView.setSelection(userCouponList.getDataList().size() - 1);
						}
					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
					}
					if (userCouponList.isEmpty()) {
						showContentEmpty(true, false);
					} else {
						showContentEmpty(false, false);
					}
				}
			} else {
				if (userCouponList.isEmpty()) {
					showContentEmpty(true, true);
				} else {
					showContentEmpty(false, false);
				}
			}
			mLoadingbr.setVisibility(View.GONE);
		}

	};

	/**
	 * @Title: showContentEmpty
	 * @Description:如果数据为空的时候，显示提示
	 * @param bl
	 *            是否显示提示
	 * @return void
	 */
	public void showContentEmpty(boolean bl, boolean noNet) {
		TextView txt = (TextView) mContentView.findViewById(R.id.txt_empty);
		txt.setVisibility(bl ? View.VISIBLE : View.GONE);
		if (noNet) {
			txt.setText(R.string.myzhima_request_failed);
		} else {
			txt.setText(R.string.no_favorite_card);
		}

		mCardCaseView.setVisibility(bl ? View.GONE : View.VISIBLE);
	}

}
