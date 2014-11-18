/* 
* @Title: IPlugin.java
* Created by liubingsr on 2012-12-29 下午4:48:56 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.plugin;

import android.content.Intent;

/**
 * @ClassName: IPlugin
 * @Description: Plugin接口
 * @author liubingsr
 * @date 2012-12-29 下午4:48:56
 *
 */
public interface IPlugin {

	public void onStart();
	public void onResume();
	public void onPause();
	public void onStop();
	public void onRestart();
	public void onDestroy();
	public void onActivityResult(int requestCode, int resultCode, Intent data);
//	//------------------------------
//	//add
//	public void setData();
//	public void setTitle(String title);
//	public void setStyle(int style);
//	public void onSkinChange();
	
//	public void onStart();
//	public void onRestart();
}
