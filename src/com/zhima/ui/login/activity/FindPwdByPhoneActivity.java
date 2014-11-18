package com.zhima.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.MobileIsRegisteredProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.StringHelper;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.ViewInitTools;

public class FindPwdByPhoneActivity extends LoginBaseActivity implements OnClickListener {

	private EditText mPhoneNumEdit;
	private LinearLayout mAgreeClauseLayout;
	private Button mNextStepBt;
	
	private String phoneNumber;
	private TextView mUseClauseText;
	private CheckBox mAgreeClauseCheck;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_phone_register_activity);
		
		setTopBar();
		findView();
		setUpView();
		
	}

	private void setTopBar() {
		ViewInitTools.setTopBar(this, "忘记密码",View.GONE,null);
	}

	private void findView() {
		mPhoneNumEdit = (EditText) this.findViewById(R.id.edt_phoneRegister_phoneNum);
		mAgreeClauseLayout = (LinearLayout) this.findViewById(R.id.layout_agree_clause);
		mAgreeClauseCheck = (CheckBox) this.findViewById(R.id.cb_phoneRegister_agree_clause);
		mNextStepBt = (Button) this.findViewById(R.id.btn_phoneRegister_nextStep);
		
		mUseClauseText = (TextView) this.findViewById(R.id.txt_phoneRegister_use_clause);
		mUseClauseText.setOnClickListener(this);
	
	}

	private void setUpView() {
		
		mAgreeClauseCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mPhoneNumEdit.setEnabled(true);
					mPhoneNumEdit.setClickable(true);
					mNextStepBt.setClickable(true);
				}else{
					mPhoneNumEdit.setEnabled(false);
					mPhoneNumEdit.setClickable(false);
					mNextStepBt.setClickable(false);
				}
			}
		});
		
		mNextStepBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_phoneRegister_nextStep:
			phoneNumber = mPhoneNumEdit.getText().toString().trim();
			if(!StringHelper.isMobilePhone(phoneNumber)){
				HaloToast.show(this, R.string.please_input_rightPhoneNum);
				return;
			}
			
			LoginService.getInstance(this).mobileIsRegistered(phoneNumber, this);
			break;
		case R.id.txt_phoneRegister_use_clause:
			Intent useIntent = new Intent(this,UseClauseActivity.class);
			startActivity(useIntent);
			break;
		}
	}
	
	
//	private IHttpRequestCallback callBack = new IHttpRequestCallback() {
//		
//		@Override
//		public void onHttpStart(ProtocolHandlerBase protocol) {
//			mLoadingLayout.setVisibility(View.VISIBLE);
//		}
//		
//		@Override
//		public void onHttpResult(ProtocolHandlerBase protocol) {
//			if (protocol.isHttpSuccess()) {
//				int protocolType = protocol.getProtocolType();
//				if (protocolType == ProtocolType.MOBILE_IS_REGISTERED_PROTOCOL) {
//
//					if (protocol.isHandleSuccess()) {
//						MobileIsRegisteredProtocolHandler p = (MobileIsRegisteredProtocolHandler) protocol;
//						if (p.isRegistered()) {
//							LoginService.getInstance(
//									FindPwdByPhoneActivity.this)
//									.requestSendResetSmsVerifyCode(phoneNumber,
//											FindPwdByPhoneActivity.this);
//						} else {
//							HaloToast.show(FindPwdByPhoneActivity.this,
//									R.string.mobile_isNotRegister);
//							mLoadingLayout.setVisibility(View.INVISIBLE);
//						}
//					} else {
//					}
//
//				} else if (protocolType == ProtocolType.REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL) {
//					if (protocol.isHandleSuccess()) {
//						Intent authcodeIntent = new Intent(this,PhoneAuthcodeActivity.class);
//						authcodeIntent.putExtra("phoneNumber", phoneNumber);
//						authcodeIntent.putExtra("isFindPwd", true);
//						startActivity(authcodeIntent);
//					} else {
//						HaloToast.show(this, R.string.send_message_failed);
//					}
//				}
//			}else{
//				HaloToast.show(FindPwdByPhoneActivity.this,R.string.network_request_failed);
//			}
//		}
//	};
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等...");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if(protocol.isHttpSuccess()){
			int protocolType = protocol.getProtocolType();
			if (protocolType == ProtocolType.MOBILE_IS_REGISTERED_PROTOCOL) {

				if (protocol.isHandleSuccess()) {
					MobileIsRegisteredProtocolHandler p = (MobileIsRegisteredProtocolHandler) protocol;
					if (p.isRegistered()) {
						LoginService.getInstance(this).requestSendResetSmsVerifyCode(phoneNumber,this);
					} else {
						HaloToast.show(this,R.string.mobile_isNotRegister);
					}
				} else {
				}

			} else if (protocolType == ProtocolType.REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					Intent authcodeIntent = new Intent(this,PhoneAuthcodeActivity.class);
					authcodeIntent.putExtra("phoneNumber", phoneNumber);
					authcodeIntent.putExtra("isFindPwd", true);
					startActivity(authcodeIntent);
				} else {
					HaloToast.show(this, R.string.send_message_failed);
				}
			}
		}else{
			HaloToast.show(this, R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
	
	@Override
	public void onDestroy() {
		//TODO
		super.onDestroy();
	}
}
