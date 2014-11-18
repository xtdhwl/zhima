package com.zhima.plugin.controller;

/**
 * @ClassName: IPluginController
 * @Description: IPlugin控制器接口
 * @author liubingsr
 * @date 2012-12-29 下午5:03:53
 * 
 */
public interface IPluginController {	
	
	public void onStart();
	public void onResume();
	public void onPause();
	public void onStop();
	public void onRestart();
	public void onDestroy();
}