package com.zhima.ui.setting.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.service.AppLaunchService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName: AboutZhimaActivity
 * @Description: 关于芝麻
 * @author yusonglin
 * @date 2012-10-6 下午4:01:48
*/
public class AboutZhimaActivity extends BaseActivity {

	private TextView mVersionText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_about_zhima_activity);
		
		initView();
		setTopBar();
	}
	
	
	private void initView() {
		mVersionText = (TextView) this.findViewById(R.id.txt_setting_about_zhima_version);
		mVersionText.setText("当前版本:"+AppLaunchService.getInstance(this).getVersionName());
		//		mAboutText.setText(StringHelper.toDBC(mAboutText.getText().toString()));
	}


	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("关于芝麻");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}
}
