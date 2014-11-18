package com.zhima.ui.login.activity;

import android.os.Bundle;
import android.view.View;

import com.zhima.R;
import com.zhima.ui.tools.ViewInitTools;

public class UseClauseActivity extends LoginBaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_use_clause_activity);
		
		setTopBar();
	}

	private void setTopBar() {
		ViewInitTools.setTopBar(this, "使用条款",View.GONE,null);
	}
	
}
