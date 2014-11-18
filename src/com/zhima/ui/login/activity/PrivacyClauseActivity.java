package com.zhima.ui.login.activity;

import android.os.Bundle;
import android.view.View;

import com.zhima.R;
import com.zhima.ui.tools.ViewInitTools;

public class PrivacyClauseActivity extends LoginBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_privacy_clause_activity);
		
		setTopBar();
	}

	private void setTopBar() {
		ViewInitTools.setTopBar(this, "隐私条款",View.GONE,null);
	}
}
