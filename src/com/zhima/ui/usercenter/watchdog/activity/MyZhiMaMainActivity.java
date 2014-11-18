package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.PersonRecordType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetUserRecordListProtocol;
import com.zhima.data.model.UserRecordEntry;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.TitleFlowIndicator;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ViewFlow.ViewSwitchListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.tools.ViewInitTools;

/**
 * @ClassName:MyZhiMaMainActivity
 * @Description 我的芝麻主页面
 * @author jiangwei
 * @date 2012-9-22 13:02:47
 */
public class MyZhiMaMainActivity extends BaseActivity implements OnClickListener {

	/** 标题指示器 */
	private TitleFlowIndicator mIndicator;

	private ViewFlow mViewFlow;

	private int mCurrentPosition = 0;

	private MyZhimaViewflowAdapter mViewFlowAdapter;
	private View mChildrenView;
	private UserService mUserService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSidebar();
		findView();// 寻找控件

		getFirstRefreshData();
		setView();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * @Title: setSidebar
	 * @Description:设置侧边栏
	 * @param
	 * @return void
	 */
	public void setSidebar() {
		
		mChildrenView = View.inflate(this, R.layout.space_zmproduct_activity, null);

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "个人管家", View.GONE, null);
		
//		mChildrenView = View.inflate(this, R.layout.myzhima_main_activity, null);
//		mSideBarView = new SideBarView(this, this, "个人管家");
//		mSideBarView.setChildView(mChildrenView);
//		setContentView(mSideBarView.getContentView());
//
//		final View tranView = (View) mSideBarView.getContentView().findViewById(R.id.view_transparent);
//		tranView.setVisibility(View.GONE);
//		tranView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mSideBarView.scrollView();
//			}
//		});
//		tranView.setClickable(false);
//		mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {
//
//			@Override
//			public void onStateChange(boolean isMenuOut) {
//				if (isMenuOut) {
//					tranView.setVisibility(View.VISIBLE);
//					tranView.setClickable(true);
//				} else {
//					tranView.setVisibility(View.GONE);
//					tranView.setClickable(false);
//				}
//			}
//		});
	}

	/**
	 * @Title: findView
	 * @Description:初始控件
	 * @param
	 * @return void
	 */
	private void findView() {

		mViewFlow = (ViewFlow) mChildrenView.findViewById(R.id.vf_trueman_main_viewflow);
		mIndicator = (TitleFlowIndicator) mChildrenView.findViewById(R.id.tfi_trueman_main_indicator);

		mViewFlowAdapter = new MyZhimaViewflowAdapter(this);
		mUserService = UserService.getInstance(this);
	}

	/**
	 * @Title: getRefreshData
	 * @Description:进入我的芝麻后获取服务器数据 （不允许缓存 不刷新页面）
	 * @param
	 * @return void
	 */
	public void getFirstRefreshData() {
		mUserService.getUserRecordList(PersonRecordType.FAVORITE, getBeginTime(), getEndTime(), 0, true, firstCallBack);
		mUserService.getUserRecordList(PersonRecordType.SCANNING, getBeginTime(), getEndTime(), 0, true, firstCallBack);
		mUserService.getUserRecordList(PersonRecordType.IDOL, getBeginTime(), getEndTime(), 0, true, firstCallBack);
		mUserService.getMyCouponList(true, firstCallBack);
	}

	private long getBeginTime() {
		long begingTime;
		begingTime = 1349024460000L;
		return begingTime;
	}

	private long getEndTime() {
		long now = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		c.add(Calendar.DAY_OF_YEAR, 1);
		return c.getTimeInMillis();
	}

	/**
	 * @Title: setView
	 * @Description:布局控制
	 * @param
	 * @return void
	 */
	private void setView() {

		// 标题栏
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_ren);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(this);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		mIndicator.setTitleProvider(mViewFlowAdapter);
		mViewFlow.setAdapter(mViewFlowAdapter, mCurrentPosition);
		mViewFlow.setFlowIndicator(mIndicator);

		mViewFlow.setOnViewSwitchListener(new ViewSwitchListener() {

			@Override
			public void onSwitched(View view, int position) {
				mCurrentPosition = position;
				mViewFlowAdapter.getServerData(position);
			}
		});

	}

	/** 刷新“我的芝麻” 数据 网络请求的回调 */
	private IHttpRequestCallback firstCallBack = new IHttpRequestCallback() {

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub
			if (protocol.isHttpSuccess()) {
				if (protocol.getProtocolType() != ProtocolType.GET_MYCOUPON_LIST_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						GetUserRecordListProtocol p = (GetUserRecordListProtocol) protocol;
						if (!p.getDataList().isEmpty()) {
							ArrayList<UserRecordEntry> newDataList = p.getDataList().getDataList();
							if (newDataList.size() > 0) {
								FavoriteView.lastTimestamp = newDataList.get(newDataList.size() - 1).getCreateTime();
							}
						}

					}
				}
			}
		}

	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.layout_topbar_rightButton1:
//			final ZhimaDatePickerDialog tt = new ZhimaDatePickerDialog(this, findViewById(R.id.layout_topbar_rightButton1));
//			tt.setOnBtClickListener(new OnBtClickListener() {
//				
//				@Override
//				public void onRightBtClick() {
//					// TODO Auto-generated method stub
//					tt.dismiss();
//				}
//				
//				@Override
//				public void onLeftBtClick() {
//					// TODO Auto-generated method stub
//					tt.dismiss();
//				}
//			});
//			tt.show();
			Intent intent = new Intent(this, WatchdogMainActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mViewFlowAdapter != null) {
				// mViewFlowAdapter.initViewData();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		MyZhimaViewflowAdapter.ISFIRST_CARD = true;
		startWaitingDialog("", R.string.loading);
		mViewFlowAdapter.getServerData(1);
		mViewFlowAdapter.notifyDataSetChanged();
		dismissWaitingDialog();
	}

}
