package com.zhima.ui.sidebar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.base.logger.Logger;
import com.zhima.base.utils.ImeHelper;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.contact.activity.ContactActivity;
import com.zhima.ui.retrieval.activity.RetrievalMainActivity;
import com.zhima.ui.scancode.activity.ScanningActivity;
import com.zhima.ui.setting.activity.SettingMainActivity;
import com.zhima.ui.sidebar.MenuHorizontalScrollView.OnSidebarScrollistener;
import com.zhima.ui.sidebar.callback.SizeCallBackForMenu;
import com.zhima.ui.usercenter.watchdog.activity.MyZhiMaMainActivity;

/**
 * @ClassName: SideBarView
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-26 下午9:11:13
 */
public class SideBarView implements OnClickListener {

	protected static final String TAG = "SideBarView";
	private Context mContext;
	private int mContentViewHeight;
	private Activity mActivity;
	private String mPageTitle;
	private View mContentView;
	private MenuHorizontalScrollView mScrollView;
	private LinearLayout mSideBarView;
	private View mChildView;
	private LinearLayout mSidebarlayout;

	/** 扫码 */
	private Button mScaningBt;
	/** 我的芝麻 */
	private Button mMyZhimaBt;
	/** 检索 */
	private Button mRetrievalBt;
	/** 设置 */
	private Button mSettingBt;
	/** 通讯录 */
	private Button mContactBt;

	private OnStateChangeListener mOnStateChangeListener;
	private View leftTopbarView;

	private OnLeftBtClickListener mOnLeftBtClickListener;
	protected boolean isMenuOut;

	private OnInputStateChangeListener mOnInputStateChangeListener;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			Logger.getInstance(TAG).debug("handleMessage isMenuOut:" + isMenuOut);
			if (isMenuOut) {
				mSidebarlayout.setVisibility(View.VISIBLE);
			} else {
				mSidebarlayout.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * @param context
	 * @param pageTitle 页面标题 如果不显示传空字符串
	 */
	public SideBarView(Activity activity, Context context, String pageTitle) {
//		Logger.getInstance(TAG).debug("SideBarView");
		this.mActivity = activity;
		this.mContext = context;
		this.mPageTitle = pageTitle;
		mContentView = View.inflate(mContext, R.layout.sidebar_menulist, null);

		mScrollView = (MenuHorizontalScrollView) mContentView.findViewById(R.id.menu_scrollView);

		mSidebarlayout = (LinearLayout) mContentView.findViewById(R.id.layout_sidebar);

		mSideBarView = (LinearLayout) mContentView.findViewById(R.id.layout_sidebar_view);

		findView();
		setUpView();
	}

	private void findView() {
		mScaningBt = (Button) mContentView.findViewById(R.id.btn_sidebar_scanning);
		mMyZhimaBt = (Button) mContentView.findViewById(R.id.btn_sidebar_myzhima);
		mRetrievalBt = (Button) mContentView.findViewById(R.id.btn_sidebar_retrieval);
		mSettingBt = (Button) mContentView.findViewById(R.id.btn_sidebar_setting);
		mContactBt = (Button) mContentView.findViewById(R.id.btn_sidebar_contact);
	}

	private void setUpView() {
		mScaningBt.setOnClickListener(this);
		mMyZhimaBt.setOnClickListener(this);
		mRetrievalBt.setOnClickListener(this);
		mSettingBt.setOnClickListener(this);
		mContactBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_sidebar_scanning:
			if (mContext instanceof ScanningActivity) {
				mScrollView.clickMenuBtn();
				return;
			}

			Intent scanIntent = new Intent(mContext, ScanningActivity.class);
			scanIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(scanIntent);
			break;
		case R.id.btn_sidebar_myzhima:
			if (mContext instanceof MyZhiMaMainActivity) {
				mScrollView.clickMenuBtn();
				return;
			}

			Intent myZhiMaIntent = new Intent(mContext, MyZhiMaMainActivity.class);
			myZhiMaIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(myZhiMaIntent);
			break;
		case R.id.btn_sidebar_retrieval:
			if (mContext instanceof RetrievalMainActivity) {
				mScrollView.clickMenuBtn();
				return;
			}
			//在检索空间中开启定位，所有这里不需要添加
			Intent retrievalIntent = new Intent(mContext, RetrievalMainActivity.class);
			retrievalIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(retrievalIntent);
			break;
		case R.id.btn_sidebar_setting:
			if (mContext instanceof SettingMainActivity) {
				mScrollView.clickMenuBtn();
				return;
			}
			Intent settingIntent = new Intent(mContext, SettingMainActivity.class);
			settingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(settingIntent);
			break;
		case R.id.btn_sidebar_contact:
			if (mContext instanceof ContactActivity) {
				mScrollView.clickMenuBtn();
				return;
			}

			Intent contactIntent = new Intent(mContext, ContactActivity.class);
			contactIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(contactIntent);
			break;
		}
		((ZhimaApplication) mActivity.getApplication()).popAllActivity();
	}

	public interface OnLeftBtClickListener {
		boolean onLeftBtClick();
	}

	public void setOnLeftBtClickListener(OnLeftBtClickListener onLeftBtClickListener) {
		this.mOnLeftBtClickListener = onLeftBtClickListener;
	}

	public void setChildView(View view) {
//		Logger.getInstance(TAG).debug("setChildView(View view)");
		this.mChildView = view;

		ZhimaTopbar topbar = (ZhimaTopbar) mChildView.findViewById(R.id.ztop_bar_layout);

		leftTopbarView = View.inflate(mContext, R.layout.topbar_leftview, null);
		RelativeLayout menuBtLayout = (RelativeLayout) leftTopbarView.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) leftTopbarView.findViewById(R.id.txt_topbar_title);
		titleText.setText(mPageTitle);
		menuBtLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Logger.getInstance(TAG).debug("menuBtLayout setOnClickListener");
				if (mOnLeftBtClickListener != null) {
					if (mOnLeftBtClickListener.onLeftBtClick())
						return;
				}
				ImeHelper.hideIME(leftTopbarView);
				mSidebarlayout.setVisibility(View.VISIBLE);
				mScrollView.clickMenuBtn();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(leftTopbarView);

		View leftView = new View(mContext);
//      leftView.setBackgroundColor(Color.TRANSPARENT);
		View[] children = new View[] { leftView, mChildView };
		mScrollView.setMenuBtn(menuBtLayout);
		mScrollView.initViews(children, new SizeCallBackForMenu(menuBtLayout), mChildView);

		mScrollView.setOnSidebarScrollistener(new OnSidebarScrollistener() {

			@Override
			public void onSidebarScrol(int menuWidth) {
				Logger.getInstance(TAG).debug("mScrollView setOnSidebarScrollistener");
				mSideBarView.setLayoutParams(new LayoutParams(menuWidth, LayoutParams.FILL_PARENT));
			}
		});
	}

	public void scrollView() {
		Logger.getInstance(TAG).debug("scrollView");
		mScrollView.clickMenuBtn();
	}

	public View getLeftView() {
		Logger.getInstance(TAG).debug("getLeftView");
		if (leftTopbarView != null) {
			return leftTopbarView;
		}
		return View.inflate(mContext, R.layout.topbar_leftview, null);
	}

	public View getContentView() {
		return mContentView;
	}

	public boolean onKeyBack() {
		Logger.getInstance(TAG).debug("onKeyBack");
		if (isMenuOut) {
			Logger.getInstance(TAG).debug("onKeyBack()");
			mScrollView.clickMenuBtn();
			return true;
		}
		return false;
	}

	public interface OnInputStateChangeListener {
		void onInputStateChange();
	}

	public void setOnInputStateChangeListener(OnInputStateChangeListener onInputStateChangeListener) {
		Logger.getInstance(TAG).debug("setOnInputStateChangeListener");
		this.mOnInputStateChangeListener = onInputStateChangeListener;
	}

	/**
	 * 设置侧滑状态改变监听器
	 * @param onStateChangeListener
	 */
	public void setOnStateChangeListener(final OnStateChangeListener onStateChangeListener) {
		this.mOnStateChangeListener = onStateChangeListener;
		mScrollView.setOnStateChangeListener(new MenuHorizontalScrollView.OnStateChangeListener() {
			@Override
			public void onStateChange(boolean isMenuOut) {
				SideBarView.this.isMenuOut = isMenuOut;
				Logger.getInstance(TAG).debug(
						"mScrollView.setOnStateChangeListener onStateChange() isMenuOut:" + isMenuOut);
				mOnStateChangeListener.onStateChange(isMenuOut);

//					new Thread(){
//						public void run() {
//							try {
//								sleep(500);
//								mHandler.sendEmptyMessage(0);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						};
//					}.start();

				mHandler.sendEmptyMessageDelayed(0, 500);
			}
		});
	}

	public interface OnStateChangeListener {
		void onStateChange(boolean isMenuOut);
	}
}
