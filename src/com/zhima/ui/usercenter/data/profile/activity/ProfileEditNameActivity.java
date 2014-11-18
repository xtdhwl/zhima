package com.zhima.ui.usercenter.data.profile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.utils.ImeHelper;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName: ProfileEditorNameActivity
 * @Description: 编辑姓名
 * @author luqilong
 * @date 2013-1-18 下午5:04:03
 */
public class ProfileEditNameActivity extends BaseActivity {

	private EditText mContentView;

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImeHelper.showIME(mContentView);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter_profile_editor_name_activity);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setTopBar();
		findView();

		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		if (content != null) {
			mContentView.setText(content);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mContentView.requestFocus();
	}

	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ImeHelper.hideIME(mContentView);
			String content = mContentView.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				HaloToast.show(getApplicationContext(), R.string.content_not_empty);
			} else {
				//返回activity
				Intent data = new Intent();
				data.putExtra("content", content);
				setResult(RESULT_OK, data);
				finish();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 500);
	}

	private void findView() {
		mContentView = (EditText) findViewById(R.id.edit_content);
		// 设置输入最大数 与 提醒
//		mContentView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
//		mContentView.setText(String.format(getString(R.string.correction_feedback_thanks), 0, MAX_LENGTH));
//		mContentView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
	}

	@Override
	public void onDestroy() {
		ImeHelper.hideIME(mContentView);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		ImeHelper.hideIME(mContentView);
		super.onBackPressed();
	};

	private void setTopBar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ImeHelper.hideIME(mContentView);
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_save);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("修改姓名");
	}
}
