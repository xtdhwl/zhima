package com.zhima.plugin.controller;

import java.util.ArrayList;

import android.content.Intent;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.ZMObject;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.ViewPluginActivityHandler;
import com.zhima.ui.activity.BaseActivity;

/**
 * @ClassName: BaseViewPluginController
 * @Description: 可视的PluginController基类
 * @author liubingsr
 * @date 2012-12-29 下午5:03:53
 * 
 */
public abstract class BaseViewPluginController implements IPluginController, IHttpRequestCallback {

	protected BaseActivity mParentActivity;
	protected BaseViewPlugin mViewPlugin = null;
	protected ViewPluginActivityHandler mHandler;
	protected ArrayList<ViewPluginCallback> mCallBackList = new ArrayList<ViewPluginCallback>();

	public BaseViewPluginController(BaseActivity activity, BaseViewPlugin viewPlugin) {
		mViewPlugin = viewPlugin;
		this.mParentActivity = activity;
		mHandler = activity.getActivityHandler();
	}

	public interface ViewPluginCallback {

	}

	public void startActivity(Intent intent) {
		mParentActivity.startActivity(intent);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		mParentActivity.startActivityForResult(intent, requestCode);
	}

	public void dismissWaitingDialog() {
		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);
	}

	public void startWaitingDialog() {
		mHandler.sendEmptyMessage(IntentMassages.START_DIALOG);
	}
	

	/**
	 * @Title: onSkinChange
	 * @Description: 皮肤发生改变
	 * @return void
	 */
	public void onSkinChange() {
		//TODO
	}

	public void addCallback(ViewPluginCallback c) {
		mCallBackList.add(c);
	}

	public void removeCallback(ViewPluginCallback c) {
		mCallBackList.remove(c);
	}

	//------------------
	//add
	/**
	 * @Title: loadData
	 * @Description: 获取数据
	 * @param zmObject zmObject
	 * @param refreshed 是否刷新数据.true为刷新，false为不刷新
	 */
	public abstract void loadData(ZMObject zmObject, boolean refreshed);

	//-------------------------------------------------------------------
	//周期方法
	@Override
	public void onStart() {
		mViewPlugin.onStart();
	}

	@Override
	public void onResume() {
		mViewPlugin.onResume();
	}

	@Override
	public void onPause() {
		mViewPlugin.onPause();
	}

	@Override
	public void onStop() {
		mViewPlugin.onStop();
		for (ViewPluginCallback c : mCallBackList) {
			removeCallback(c);
		}
	}

	@Override
	public void onRestart() {
		mViewPlugin.onRestart();
	}

	@Override
	public void onDestroy() {
		mViewPlugin.onDestroy();
	}

	//-------------------------------------------------------------------
	//回调方法
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mViewPlugin.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub

	}

	//--------------------------------------------------------------------
	//事件方法
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		return mViewPlugin.onKeyDown(keyCode, event);
	//	}
	//
	//	public boolean onBackPressed() {
	//		return mViewPlugin.onBackPressed();
	//	}
	//
	//	public boolean onTouchEvent(MotionEvent event) {
	//		return mViewPlugin.onTouchEvent(event);
	//	}
	//	public void onConfigurationChanged(Configuration newConfig) {
	//		//TODO
	//	}
	//	public boolean onControllerEvent(String id, Object event) {
	//		return false;
	//	}
	//
	//	public void handleConfigChanged(Configuration config) {
	//		//TODO 
	//	}
}