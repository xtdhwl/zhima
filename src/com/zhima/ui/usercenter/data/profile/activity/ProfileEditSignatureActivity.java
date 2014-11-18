package com.zhima.ui.usercenter.data.profile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * @Description: 编辑签名
 * @author luqilong
 * @date 2013-1-18 下午5:04:03
 */
public class ProfileEditSignatureActivity extends BaseActivity {

	// 签名输入最大数
	private static final int MAX_LENGTH = 140;

	private EditText mContentEdit;
	private TextView mTextCount;

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImeHelper.showIME(mContentEdit);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter_profile_editor_signature_activity);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setTopBar();
		findView();
		setListenerView();

		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		if (content != null) {
			mContentEdit.setText(content);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 500);
	}

	private void setListenerView() {
		mContentEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// 字符输入个数提示
				mTextCount.setText(String.format("还可以输入%s个字", MAX_LENGTH - s.length()));
			}
		});
	}

	private OnClickListener saveTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ImeHelper.hideIME(mContentEdit);
			String content = mContentEdit.getText().toString().trim();
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
	public void onBackPressed() {
		ImeHelper.hideIME(mContentEdit);
		super.onBackPressed();
	};

	@Override
	public void onDestroy() {
		ImeHelper.hideIME(mContentEdit);
		super.onDestroy();
		
	};
	
	private void findView() {
		mTextCount = (TextView) findViewById(R.id.txt_prompt);
		mContentEdit = (EditText) findViewById(R.id.edit_content);
		// 设置输入最大数 与 提醒
		mContentEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
		mTextCount.setText(String.format("还可以输入%s个字", MAX_LENGTH));
	}

	private void setTopBar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ImeHelper.hideIME(mContentEdit);
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_save);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("修改签名");
	}

}
