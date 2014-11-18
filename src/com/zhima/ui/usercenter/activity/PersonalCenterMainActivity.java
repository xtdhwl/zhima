package com.zhima.ui.usercenter.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.usercenter.watchdog.activity.WatchdogMainActivity;

public class PersonalCenterMainActivity extends TabActivity implements OnTabChangeListener {

	// private AnimationTabHost mTabHost;
	private TabHost mTabHost;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_center_maintab_activity);

		initView();
	}

	private void initView() {
		mTabHost = getTabHost();

		// TabSpec tabTodaybuy = mTabHost.newTabSpec("1");
		// Intent todaybuyIntent = new Intent(this, SettingMainActivity.class);
		// tabTodaybuy.setContent(todaybuyIntent);
		// tabTodaybuy.setIndicator(getTabView(R.string.perssonal_center_trends));

		TabSpec neatlybuy = mTabHost.newTabSpec("1");
		Intent neatlyIntent = new Intent(this, PersonalDataActivity.class);
		neatlyIntent.putExtra(PersonalDataActivity.USER_ID, getIntent().getLongExtra(PersonalDataActivity.USER_ID, -1));
		neatlyIntent.putExtra(PersonalDataActivity.IS_MYSELF,getIntent().getBooleanExtra(PersonalDataActivity.IS_MYSELF, false));
		neatlyIntent.putExtra(PersonalDataActivity.IS_MYFRIEND,getIntent().getBooleanExtra(PersonalDataActivity.IS_MYFRIEND, false));
		neatlybuy.setContent(neatlyIntent);
		neatlybuy.setIndicator(getTabView(R.string.perssonal_center_space));

		TabSpec tabFindBuy = mTabHost.newTabSpec("2");
		Intent findbuyIntent = new Intent(this, WatchdogMainActivity.class);
		findbuyIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, WatchdogMainActivity.TYPE_INBOX);
		tabFindBuy.setContent(findbuyIntent);
		tabFindBuy.setIndicator(getTabView(R.string.perssonal_center_steward));

		// mTabHost.addTab(tabTodaybuy);
		mTabHost.addTab(neatlybuy);
		mTabHost.addTab(tabFindBuy);

		// mTabHost.setOpenAnimation(true);
	}

	@Override
	protected void onPause() {
		// TODO
		super.onPause();
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}

	public View getTabView(int res) {
		textView = (TextView) View.inflate(this, R.layout.user_center_main_button_item, null);
		textView.setGravity(Gravity.CENTER);
		textView.setText(res);
		return textView;
	}

	@Override
	public void onTabChanged(String tabId) {

	}

}
