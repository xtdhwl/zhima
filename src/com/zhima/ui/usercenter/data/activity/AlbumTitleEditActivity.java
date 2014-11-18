package com.zhima.ui.usercenter.data.activity;

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
 * @ClassName: AlbumEditActivity
 * @Description: 编辑相册标题栏
 * @author luqilong
 * @date 2013-1-21 上午10:08:30
 */
public class AlbumTitleEditActivity extends BaseActivity {
	// 签名输入最大数
	private static final int MAX_LENGTH = 12;

	private EditText mTitleEditView;
	private TextView mPromptView;

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImeHelper.showIME(mTitleEditView);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_album_title_edit_activity);
		setTopBar();
		findView();

		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		if (content != null) {
			mTitleEditView.setText(content);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 500);
	}

	private void findView() {
		// TODO Auto-generated method stub
		mTitleEditView = (EditText) findViewById(R.id.edit_content);
		mPromptView = (TextView) findViewById(R.id.txt_prompt);

		// 设置输入最大数 与 提醒
		mTitleEditView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
		mPromptView.setText(String.format("还可以输入%s个字", MAX_LENGTH));
		mTitleEditView.addTextChangedListener(textWatcher);
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			// 字符输入个数提示
			mPromptView.setText(String.format("还可以输入%s个字", MAX_LENGTH - s.length()));
		}
	};

	private OnClickListener saveTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ImeHelper.hideIME(mTitleEditView);
			String content = mTitleEditView.getText().toString().trim();
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
		ImeHelper.hideIME(mTitleEditView);
		super.onBackPressed();
	};

	@Override
	public void onDestroy() {
		ImeHelper.hideIME(mTitleEditView);
		super.onDestroy();

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
				ImeHelper.hideIME(mTitleEditView);
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_save);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("编辑标题");
	}

}
