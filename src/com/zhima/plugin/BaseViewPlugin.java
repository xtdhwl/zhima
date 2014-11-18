package com.zhima.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.View;

import com.zhima.plugin.controller.BaseViewPluginController;

/**
 * @ClassName: BaseViewPlugin
 * @Description: 可视化Plugin基类
 * @author liubingsr
 * @date 2012-12-29 下午5:03:53
 * 
 */
public abstract class BaseViewPlugin implements IPlugin {
	protected BaseViewPluginController mViewPluginController = null;
	protected View mPluginView = null;
	protected Context mContext = null;
	protected TypedArray mTypedArray;

	public BaseViewPlugin(Context context/* , BaseViewPluginController controller */) {
		mContext = context;
		//		mViewPluginController = controller;
	}

	public void init(BaseViewPluginController controller) {
		mViewPluginController = controller;
	}

	public View getPluginView() {
		return mPluginView;
	}

	public void setBaseSytle(TypedArray typedArray) {
		mTypedArray = typedArray;
		setStyle(mTypedArray);
	}

	public TypedArray getSytle() {
		return mTypedArray;
	}

	//设置模块的样式
	public abstract void setStyle(TypedArray typedArray);

	public BaseViewPluginController getPluginController() {
		return mViewPluginController;
	}

	//---------------------------------------------------
	//周期方法回调
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public void onRestart() {

	}

	@Override
	public void onDestroy() {

	}

	//-----------------------------
	//	/**
	//	 * @Title: onSkinChange
	//	 * @Description: 皮肤发生改变
	//	 * @return void
	//	 */
	//	public void onSkinChange() {
	//
	//	}
	//	public boolean onTouchEvent(MotionEvent event) {
	//		return false;
	//	}
	//
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		return false;
	//	}
	//
	//	public boolean onBackPressed() {
	//		return false;
	//	}

}