package com.zhima.ui.setting.activity;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.model.ZMProductObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName: CorrectFeedBackActivity
 * @Description: 纠错
 * @author luqilong
 * @date 2012-11-5 下午1:12:42
 */
public class CorrectionFeedBackActivity extends BaseActivity {
	private EditText mContentEdit;
	private TextView mTextCount;
	private ScanningcodeService mScanningcodeService;
	private ZMProductObject mZMObject;
	// 纠错输入最大数
	private static final int MAX_LENGTH = 140;

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
		setContentView(R.layout.settting_correction_feedback_activity);
		setTopBar();
		findView();
		init();
		setListenerView();

		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = (ZMProductObject) mScanningcodeService.getCacheZMObject(id);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 500);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		super.onHttpResult(protocol);
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.isHandleSuccess()) {
				HaloToast.show(getApplicationContext(),
						getText(R.string.send_success).toString() + "," + getText(R.string.praise_success).toString()
								+ "!");
				finish();
			} else {
				HaloToast.show(getApplicationContext(), R.string.send_message_failed);
			}
		} else {
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
	}

	// 发送纠错信息
	private View.OnClickListener sendClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String content = mContentEdit.getText().toString().trim();
			if (TextUtils.isEmpty(content)) {
				HaloToast.show(getApplicationContext(), R.string.content_not_empty);
			} else {
				startWaitingDialog(null, R.string.sending);
				mScanningcodeService.postCorrection(content, mZMObject, CorrectionFeedBackActivity.this);
			}
		}
	};

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
				mTextCount.setText(String.format(getString(R.string.correction_feedback_thanks), s.length(), MAX_LENGTH));
			}
		});
	}

	private void findView() {
		mTextCount = (TextView) this.findViewById(R.id.txt_prompt);
		mContentEdit = (EditText) this.findViewById(R.id.edt_content);
		// 设置输入最大数 与 提醒
		mContentEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
		mTextCount.setText(String.format(getString(R.string.correction_feedback_thanks), 0, MAX_LENGTH));
	}

	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View leftview = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) leftview.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) leftview.findViewById(R.id.txt_topbar_title);
		titleText.setText(getText(R.string.correction));
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImeHelper.hideIME(mContentEdit);
				finish();
			}
		});

		View rightView = View.inflate(this, R.layout.topbar_rightview, null);
		RelativeLayout sendBtLayout = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		ImageView sendImage = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		sendImage.setImageResource(R.drawable.send);
		sendBtLayout.setOnClickListener(sendClickListener);
		sendBtLayout.setVisibility(View.VISIBLE);

		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(leftview);
		topbar.setRightLayoutVisible(true);
		topbar.addRightLayoutView(rightView);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

}
