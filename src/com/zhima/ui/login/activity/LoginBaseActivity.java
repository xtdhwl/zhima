package com.zhima.ui.login.activity;

import android.os.Bundle;

import com.zhima.app.ZhimaApplication;
import com.zhima.ui.activity.BaseActivity;

public class LoginBaseActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//TODO
		super.onCreate(savedInstanceState);
		
		((ZhimaApplication)getApplication()).mLoginActivitys.put(mActivityId, this);
		((ZhimaApplication)getApplication()).mActivitys.remove(mActivityId);
	}
	
	@Override
	public void onDestroy() {
		//TODO
		super.onDestroy();
		((ZhimaApplication)getApplication()).mLoginActivitys.remove(mActivityId);
	}
	
	protected void dismissLoginActivity() {
		//TODO
		((ZhimaApplication)getApplication()).dismissLoginActivity();
	}
	
}
