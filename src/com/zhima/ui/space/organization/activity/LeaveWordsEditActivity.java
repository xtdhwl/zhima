package com.zhima.ui.space.organization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMCouplesService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName BlessEditActivity
 * @Description 祝福语编辑页面
 * @author jiang
 * @date 2012-12-6 下午06:53:56
 */
public class LeaveWordsEditActivity extends BaseActivity {

	private ScanningcodeService mScanningcodeService;
	private ZMObject mZMCouplesObject;
	private ZMCouplesService mZMCouplesService;
	private EditText mNameEdit;
	private EditText mContentEdit;
	private TextView mContentLabel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.space_organization_leavewords_edit_activity);

		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMCouplesService = ZMCouplesService.getInstance(this);
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);

		setTopbar();
		findView();

		mZMCouplesObject = mScanningcodeService.getCacheZMObject(id);
		if (mZMCouplesObject != null) {

		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.ADD_COUPLES_COMMENT_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					HaloToast.show(this, R.string.bless_send_success);
					finish();
				} else {
					HaloToast.show(this, protocol.getProtocolErrorMessage());
				}
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
		}
	}

	private void getServiceData() {
		// TODO Auto-generated method stub
		String name = mNameEdit.getText().toString();
		String content = mContentEdit.getText().toString();
		if (name.trim().equals("")) {
			HaloToast.show(this, R.string.bless_forbid_name_empty);
		} else if (content.trim().equals("")) {
			HaloToast.show(this, R.string.bless_forbid_content_empty);
		} else if(content.length() > 140){
			HaloToast.show(this, R.string.bless_forbid_content_top);
		} else if(name.length() > 10){
			HaloToast.show(this, R.string.bless_forbid_name_top);
		} else {
			startWaitingDialog("", "正在发送...");
			mZMCouplesService.addWeddingComment(mZMCouplesObject.getRemoteId(), mZMCouplesObject.getZMObjectType(),name, content, this);
		}
	}

	private void setTopbar() {
		// TODO Auto-generated method stub
		ZhimaTopbar mTopbar = getTopbar();
		mTopbar.setBackgroundResource(R.color.orange_organization_topbar);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.bless_send);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getServiceData();
			}

		});

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.leave_words_write);
	}

	private void findView() {
		mContentLabel = (TextView) this.findViewById(R.id.text_content_label);
		mNameEdit = (EditText) this.findViewById(R.id.edit_nick_name);
		mContentEdit = (EditText) this.findViewById(R.id.edit_bless_content);
		mContentEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mContentLabel.setText("留言" + "（" + mContentEdit.getText().toString().length() + "/140字）");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
}
