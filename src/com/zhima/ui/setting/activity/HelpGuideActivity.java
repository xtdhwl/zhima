package com.zhima.ui.setting.activity;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.service.AppLaunchService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.CirclePageIndicator;
import com.zhima.ui.common.view.ZmVpAdapter;
import com.zhima.ui.main.activity.MainActivity;

/**
 * 帮助指南
 * 
 * @ClassName: HelpGuideActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-10-6 下午4:01:59
 */
public class HelpGuideActivity extends BaseActivity {

	//	private ViewFlow mViewFlow;
//		private CircleFlowIndicator mCircleFlowIndicator;
	private Integer[] mImageIds = { R.drawable.help1, R.drawable.help2, R.drawable.help3, R.drawable.help4,
			R.drawable.help5 };

	private boolean isFirstInstall;
//	private SettingHelpAdapter mSettingHelpAdapter;

	private ViewPager mViewPager;
	private CirclePageIndicator mCircleFlowIndicator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_help_activity);

		Intent intent = getIntent();
		isFirstInstall = intent.getBooleanExtra("isFirstInstall", false);
		findView();
		setUpView();
	}

	private void findView() {
		//		mViewFlow = (ViewFlow) this.findViewById(R.id.vf_setting_help_viewflow);
		//		mCircleFlowIndicator = (CircleFlowIndicator)this.findViewById(R.id.cfi_setting_help_viewflowindic);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mCircleFlowIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

	}

	private void setUpView() {
		//		mViewFlow.setFlowIndicator(mCircleFlowIndicator);
		//		mSettingHelpAdapter = new SettingHelpAdapter(this, this,isFirstInstall);

//		GuideViewPageAdapter adapter = new GuideViewPageAdapter(this, Arrays.asList(mImageIds));
		
		ZmVpAdapter<Integer> vpAdapter = new ZmVpAdapter<Integer>(this, Arrays.asList(mImageIds), 1, 5) {

			@Override
			public Object getView(ViewGroup container, Integer data,
					int position) {

				View view = View.inflate(mContext, R.layout.setting_help_item, null);

				ImageView imageView = (ImageView) view.findViewById(R.id.img_setting_helpItem);
				Button button = (Button) view.findViewById(R.id.btn_setting_helpItem);

				imageView.setImageResource(data);

				//是否最后一个
				if (position == (mArray.size() - 1)) {
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (isFirstInstall) {
								Intent intent = new Intent(mContext, MainActivity.class);
								mContext.startActivity(intent);
								SettingHelper.setString(mContext, Field.VERSION, AppLaunchService.getInstance(mContext)
										.getVersionName());
							}
							finish();
						}
					});
					button.setVisibility(View.VISIBLE);
				} else {
					button.setVisibility(View.GONE);
				}

				container.addView(view, 0);
				return view;
			}
		};
		
		mViewPager.setAdapter(vpAdapter);
		mCircleFlowIndicator.setViewPager(mViewPager);
	}

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
	}

}
