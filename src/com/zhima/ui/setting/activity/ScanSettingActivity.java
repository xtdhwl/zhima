package com.zhima.ui.setting.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * 扫码设置页面
 * @ClassName: ScanSettingActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-25 上午10:31:32
*/
public class ScanSettingActivity extends BaseActivity implements OnClickListener {

	/** 提示音 */
	private RelativeLayout mWarningToneLayout;
	private TextView mWarningToneText;
	private CheckBox mWarningToneCb;
	
	/** 震动 */
	private RelativeLayout mShakeLayout;
	private TextView mShakeText;
	private CheckBox mShakeCb;
	
	/** 提示音 */
	private boolean isWarningTone = false;
	/** 震动 */
	private boolean isShake = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_scan_activity);
		
		getMemoryData();
		
		setTopBar();
		findView();
		setUpView();
	}

	/**
	 * 获取已经存储的数据
	 */
	private void getMemoryData() {
		isWarningTone = SettingHelper.getBoolean(this.getBaseContext(),Field.SETTING_SCAN_WARNINGTONE, true);
		isShake = SettingHelper.getBoolean(this.getBaseContext(), Field.SETTING_SCAN_SHAKE, true);
	}
	
	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("扫码设置");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}
	
	/**
	 * 寻找控件
	 */
	private void findView() {
		mWarningToneLayout = (RelativeLayout) this.findViewById(R.id.layout_scan_setting_warningTone);
		mWarningToneText = (TextView) this.findViewById(R.id.txt_scan_setting_warningTone);
		mWarningToneCb = (CheckBox) this.findViewById(R.id.cb_scan_setting_warningTone);
		
		mShakeLayout = (RelativeLayout) this.findViewById(R.id.layout_scan_setting_shake);
		mShakeText = (TextView) this.findViewById(R.id.txt_scan_setting_shake);
		mShakeCb = (CheckBox) this.findViewById(R.id.cb_scan_setting_shake);
	}

	/**
	 * 设置控件
	 */
	private void setUpView() {
		mWarningToneLayout.setOnClickListener(this);
		mShakeLayout.setOnClickListener(this);
		
		mWarningToneCb.setChecked(isWarningTone);
		mShakeCb.setChecked(isShake);
		
		mWarningToneCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingHelper.setBoolean(ScanSettingActivity.this, Field.SETTING_SCAN_WARNINGTONE, isChecked);
			}
		});
		
		mShakeCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SettingHelper.setBoolean(ScanSettingActivity.this, Field.SETTING_SCAN_SHAKE, isChecked);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_scan_setting_warningTone:
			mWarningToneCb.setChecked(!mWarningToneCb.isChecked());
			break;
		case R.id.layout_scan_setting_shake:
			mShakeCb.setChecked(!mShakeCb.isChecked());
			break;
		}
	}
}
