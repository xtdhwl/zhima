package com.zhima.ui.usercenter.watchdog.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.User;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.RegionService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName VirtualInfoActivity
 * @Description 虚拟账户信息的显示
 * @author jiang
 * @date 2012-9-25 下午09:18:57
 */
public class VirtualInfoActivity extends BaseActivity{
  private AccountService  mAccountService;
  private User accout;
  private TextView textName,textAddress;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.virtual_infi_activity);
		setTopbar();
		init();
		
		accout = mAccountService.getMyself();
		setView(accout);
	}
	
	
	private void setView(User accout) {
		textName.setText(accout.getNickname());	
		String city = RegionService.getInstance(this).getRegionById(accout.getCityId());
		textAddress.setText(city);
	}


	private void init() {
		mAccountService = AccountService.getInstance(this);
		textName = (TextView)this.findViewById(R.id.txt_name);
		textAddress = (TextView)this.findViewById(R.id.txt_position);
	}
	
	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("虚拟身份");
	}

}
