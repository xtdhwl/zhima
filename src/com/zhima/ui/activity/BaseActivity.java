/* 
 * @Title: BaseActivity.java
 * Created by liubingsr on 2012-5-14 上午10:49:28 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.ImeHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.base.utils.UmengStatUtil;
import com.zhima.base.utils.UniqueIdGenerator;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.GetUserInfoHttpRequestCallback;
import com.zhima.data.service.RequestService;
import com.zhima.data.service.UserService;
import com.zhima.plugin.ViewPluginActivityHandler;
import com.zhima.ui.common.view.CommonViewUtils;
import com.zhima.ui.common.view.CustomLoadDialog;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.LocalImageLoader;

/**
 * @ClassName: BaseActivity
 * @Description: Activity基类
 * @author liubingsr
 * @date 2012-5-14 上午10:49:28
 * 
 */
public class BaseActivity extends Activity implements IHttpRequestCallback {
	private static final String TAG = "BaseActivity";
	public final static String ACTIVITY_EXTRA = "activity_extra";
	public final static String ACTIVITY_EXTRA2 = "activity_extra2";
	public final static String ACTIVITY_BUNDLE = "bundle";
	public final static String UID_EXTRA = "uid";
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;

	
	// 登录
	public final static int RESULT_NEED_LOGIN = RESULT_FIRST_USER;
	public final static int RESULT_NEED_LOGIN_SUCCESS = RESULT_NEED_LOGIN + 1;
	public final static int RESULT_NEED_LOGIN_ERROR = RESULT_NEED_LOGIN_SUCCESS + 1;
	public final static int RESULT_NEED_LOGIN_FAILED = RESULT_NEED_LOGIN_ERROR + 1;

	// 数据刷新
	public final static int RESULT_NEED_REFRESH = RESULT_NEED_LOGIN_FAILED + 1;
	public static final int RESULT_NEED_REFRESH_SUCCESS = RESULT_NEED_REFRESH + 1;
	public final static int RESULT_NEED_REFRESH_ERROR = RESULT_NEED_REFRESH + 1;
	public final static int RESULT_NEED_REFRESH_FAILED = RESULT_NEED_REFRESH_ERROR + 1;

	protected RequestService mRequestService;
	private CustomLoadDialog mWaitDlg = null;

	protected boolean mEnableResumeLoadImage = true;

	protected long mActivityId = 0;
	protected long mEndActivityId = 0;
	private View mMainView;

//	private boolean isActivityBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		ImeHelper.setNoTitleAndInput(this);
		mRequestService = RequestService.getInstance(this);
		//------------------
		createActivityId();
		((ZhimaApplication) this.getApplication()).mActivitys.put(mActivityId, this);
		LocalImageLoader.getInstance(this).setCurrentBelongId(mActivityId);
		HttpImageLoader.getInstance(this).setCurrentBelongId(mActivityId);
		
		getMyself();
	}

	public void setPluginHandler(ViewPluginActivityHandler handler) {
		mViewPluginHandler = handler;
	}

	@Override
	public void onResume() {
		super.onResume();
		UmengStatUtil.onResume(this);
		mRequestService.bindCallback(this);
		if (mEnableResumeLoadImage) {
			LocalImageLoader.getInstance(this).resumeBelongId(mActivityId);
			HttpImageLoader.getInstance(this).resumeBelongId(mActivityId);
		}

	
	}

	@Override
	public void onPause() {
		UmengStatUtil.onPause(this);

		if (mWaitDlg != null) {
			mWaitDlg.dismiss();
			mWaitDlg = null;
		}
		if (mEnableResumeLoadImage) {
			LocalImageLoader.getInstance(this).recyclePauseByBelongId(mActivityId);
			HttpImageLoader.getInstance(this).recyclePauseByBelongId(mActivityId);
		}
		System.gc();

		//		if(isActivityBack){
		//			
		//			HaloToast.show(this, "是返回");
		//			overridePendingTransition(R.anim.activity_back_in,R.anim.activity_back_out);
		//		}else{
		//			HaloToast.show(this, "不是返回");
		//			overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
		//		}

		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);

//		isActivityBack = false;

	
		
		super.onPause();

	}

	private void close() {
		//		Logger.getInstance(this.getClass().getSimpleName()).debug("close()");
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		((ZhimaApplication) getApplication()).mActivitys.remove(mActivityId);
		mRequestService.deleteByCallback(this);
		LocalImageLoader.getInstance(this).recycle(mActivityId);
		HttpImageLoader.getInstance(this).recycle(mActivityId);
		//		recycleImage();
		//		mMainView = null;
		System.gc();
		close();
		super.onDestroy();
	}
	
	protected void getMyself() {
		if (SettingHelper.getBoolean(this, Field.IS_NEED_GET_MYSELF, false) && AccountService.getInstance(this).isLogin()) {
			UserService.getInstance(ZhimaApplication.getContext()).getMyself(new GetUserInfoHttpRequestCallback());
		}
	}
	/**
	 * @Title: recycleImage
	 * @Description: TODO(描述这个方法的作用) void
	 */
	private void recycleImage() {
		ArrayList<View> viewList = new ArrayList<View>();
		if (mMainView != null) {
			viewList.add(mMainView);
			for (int index = 0; index < viewList.size(); ++index) {
				View v = viewList.get(index);
				removeBitmap(v.getBackground());
				if (v instanceof ViewGroup) {
					ViewGroup group = (ViewGroup) v;
					for (int j = 0; j < group.getChildCount(); ++j) {
						viewList.add(group.getChildAt(j));
					}
				}
				if (v instanceof ImageView) {
					ImageView image = (ImageView) v;
					removeBitmap(image.getDrawable());
					image.setImageDrawable(null);
					image.setImageBitmap(null);
				}
				v.setBackgroundDrawable(null);
			}
		}
		viewList.clear();
	}

	/**
	 * @Title: removeBitmap
	 * @Description: TODO(描述这个方法的作用)
	 * @param background void
	 */
	private void removeBitmap(Drawable background) {
		// TODO Auto-generated method stub
	}

	public boolean onSuperBackPressed() {
		return false;
	}

	@Override
	public void onBackPressed() {
		boolean result = false;
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			isActivityBack = true;
//		} else {
//			isActivityBack = false;
//		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public long createActivityId() {
		mActivityId = UniqueIdGenerator.getInstance().genericId();
		return mActivityId;
	}

	public long getActivityId() {
		return mActivityId;
	}

	/**
	 * @Title: getTopbar
	 * @Description: 获取顶端的topbar
	 * @return ZhimaTopbar
	 */
	public ZhimaTopbar getTopbar() {
		return (ZhimaTopbar) findViewById(R.id.ztop_bar_layout);
	}

	public void startWaitingDialog(String title, int res) {
		startWaitingDialog(title, getString(res));
	}

	public void startWaitingDialog(String title, String text) {
		if (isDialogShowing()) {
			return;
		}
		mWaitDlg = CommonViewUtils.getWaitDlg(this, title, text);
		mWaitDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {

			}
		});
		mWaitDlg.show();
	}

	public void startWaitingDialog(int title, int text, final IHttpRequestCallback callback) {
		if (title == 0)
			startWaitingDialog("", getString(text), callback);
		else
			startWaitingDialog(getString(title), getString(text), callback);
	}

	public void startWaitingDialog(int title, String text, final IHttpRequestCallback callback) {
		if (title == 0) {
			startWaitingDialog("", text, callback);
		} else {
			startWaitingDialog(getString(title), text, callback);
		}
	}

	public void startWaitingDialog(String title, int res, final IHttpRequestCallback callback) {
		startWaitingDialog(title, getString(res), callback);
	}

	public void startWaitingDialog(int title, int text) {
		if (title == 0) {
			startWaitingDialog("", getString(text));
		} else {
			startWaitingDialog(getString(title), getString(text));
		}
	}

	/**
	 * @Title: startWaitingDialog
	 * @Description: 显示等待Dialog
	 * @param title 标题
	 * @param text 显示文字
	 * @param callback
	 * @return void
	 */
	public void startWaitingDialog(String title, String text, final IHttpRequestCallback callback) {
		mWaitDlg = CommonViewUtils.getWaitDlg(this, title, text);
		mWaitDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				mRequestService.deleteByCallback(callback);
			}
		});
		mWaitDlg.show();
	}

	/**
	 * @Title: dismissWaitingDialog
	 * @Description: 关闭等待Dialog
	 * @return void
	 */
	public void dismissWaitingDialog() {
		if (mWaitDlg != null && mWaitDlg.isShowing()) {
			mWaitDlg.dismiss();
		}
	}

	public boolean isDialogShowing() {
		return mWaitDlg != null && mWaitDlg.isShowing();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}

	//----------------------------------
	//add 
	private ViewPluginActivityHandler mViewPluginHandler = null;

	public ViewPluginActivityHandler getActivityHandler() {
		return mViewPluginHandler;
	}

}
