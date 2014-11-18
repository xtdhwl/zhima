package com.zhima.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.MobileIsRegisteredProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.StringHelper;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 手机注册
 * @className:EmailRegisterActivity
 * @Description: 注册(手机)
 * @author yusonglin
 * @date 2012-7-23 
 */
public class PhoneRegisterActivity extends LoginBaseActivity implements OnClickListener {
	
	/** 手机号输入 */
	private EditText mPhoneNumEdit;
	/** 同意条款 */
	private CheckBox mAgreeClauseCheck;
	/** 用户条款 */
	private TextView mUseClauseText;
	/** 隐私条款 */
//	private TextView mPrivacyClauseText;
	/** 验证 */
	private Button mNextStepBt;
	
	
	private String phoneNumber;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_phone_register_activity);
		
		setTopBar();
		//寻找设置控件
		findView();
		setUpView();
		
	}
	
	private void setTopBar() {
		ViewInitTools.setTopBar(this, "注册",View.GONE,null);
	}

	/**
	 * 寻找控件
	 */
	private void findView() {
		mPhoneNumEdit = (EditText) this.findViewById(R.id.edt_phoneRegister_phoneNum);
		mAgreeClauseCheck = (CheckBox) this.findViewById(R.id.cb_phoneRegister_agree_clause);
		mUseClauseText = (TextView) this.findViewById(R.id.txt_phoneRegister_use_clause);
//		mPrivacyClauseText = (TextView) this.findViewById(R.id.txt_phoneRegister_privacy_clause);
		mNextStepBt = (Button) this.findViewById(R.id.btn_phoneRegister_nextStep);
		
	}
	/**
	 * 设置控件
	 */
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
		
		mUseClauseText.setOnClickListener(this);
//		mPrivacyClauseText.setOnClickListener(this);
		
		mNextStepBt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_phoneRegister_nextStep:
			if(!mAgreeClauseCheck.isChecked()){
				return;
			}
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
//		case R.id.txt_phoneRegister_privacy_clause:
//			Intent privacyIntent = new Intent(this,PrivacyClauseActivity.class);
//			startActivity(privacyIntent);
//			break;
		}
	}
	
	
//	private IHttpRequestCallback callBack = new IHttpRequestCallback() {
//		
//		@Override
//		public void onHttpStart(ProtocolHandlerBase protocol) {
//			startWaitingDialog("", "请稍等...");
//		}
//		
//		@Override
//		public void onHttpResult(ProtocolHandlerBase protocol) {
//			if(protocol.isHttpSuccess()){
//				if(protocol.isHandleSuccess()){
//					MobileIsRegisteredProtocolHandler handler = (MobileIsRegisteredProtocolHandler) protocol;
//					if(handler.isRegistered()){
//						HaloToast.show(PhoneRegisterActivity.this, R.string.mobile_isRegister);
//					}else{
//						LoginService.getInstance(PhoneRegisterActivity.this).requestSendSmsVerifyCode(phoneNumber, PhoneRegisterActivity.this);
//					}
//				}else{
//				}
//			}else{
//				HaloToast.show(PhoneRegisterActivity.this,R.string.network_request_failed);
//			}
//			dismissWaitingDialog();
//		}
//	};
	
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等...");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			int protocolType = protocol.getProtocolType();
			if(protocolType == ProtocolType.MOBILE_IS_REGISTERED_PROTOCOL){
				if(protocol.isHandleSuccess()){
					MobileIsRegisteredProtocolHandler handler = (MobileIsRegisteredProtocolHandler) protocol;
					if(handler.isRegistered()){
						HaloToast.show(PhoneRegisterActivity.this, R.string.mobile_isRegister);
					}else{
						LoginService.getInstance(PhoneRegisterActivity.this).requestSendSmsVerifyCode(phoneNumber, PhoneRegisterActivity.this);
					}
				}else{
					
				}
			}else if (protocolType == ProtocolType.REQUEST_SEND_VERIFYCODE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//
					Intent authcodeIntent = new Intent(this,PhoneAuthcodeActivity.class);
					authcodeIntent.putExtra("phoneNumber", phoneNumber);
					startActivity(authcodeIntent);
				} else {
					//
				}
			}
		}else{
			HaloToast.show(this,R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
}
