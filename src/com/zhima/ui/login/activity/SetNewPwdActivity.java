package com.zhima.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RequestSendResetPwdProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.ResetNewPasswordProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 忘记密码_设置新密码
 * @className:RegisterActivity
 * @Description: 设置新密码
 * @author yusonglin
 * @date 2012-7-24 下午 
 */
public class SetNewPwdActivity extends LoginBaseActivity implements OnClickListener {
	
	/** 密码 */
	private EditText mPasswordEdit;
	/** 下一步 */
	private Button mNextStepBt;
	
	private String authcode;
	private String phoneNumber;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_register_setup_password);
		
		
		initData();
		setTopBar();
		findView();
		setUpView();
	}

	private void initData() {
		//TODO
		Intent intent = getIntent();
		phoneNumber = intent.getStringExtra("phoneNumber");
		authcode = intent.getStringExtra("authcode");
		
	}

	private void setTopBar() {
		//TODO
		ViewInitTools.setTopBar(this, "设置新密码", View.GONE, null);
	}

	private void findView() {
		
		mPasswordEdit = (EditText) this.findViewById(R.id.edt_phoneRegister_password);
		mNextStepBt = (Button) this.findViewById(R.id.btn_phoneRegister_setUp_nextStep);
	
	}

	private void setUpView() {
		mPasswordEdit.setHint(R.string.please_input_newPwd);
		mNextStepBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_phoneRegister_setUp_nextStep:
			String password = mPasswordEdit.getText().toString().trim();
			
			//判断密码是否合格
//			if(!password.matches("[a-zA-Z0-9]{6,16}")){
			if(!password.matches("^[a-zA-Z0-9~!@#$%\\^&*_]{6,20}$")){
				Toast.makeText(this, R.string.please_input_rightPassword, 0).show();
				mPasswordEdit.setText("");
				return;
			}
			
			LoginService.getInstance(this).resetNewPasswordByMobile(authcode,phoneNumber,password, this);
			
//			HaloToast.show(this,getResources().getString(R.string.finish)+",进入个人中心");
			break;
		}
		
		
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等...");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			// 网络请求成功
			if (protocol.getProtocolType() == ProtocolType.RESET_NEW_PASSWORD_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					ResetNewPasswordProtocolHandler handler = (ResetNewPasswordProtocolHandler) protocol;
					if (handler.isResetSuccessful()) {
						HaloToast.show(this, "密码重置成功");
						dismissLoginActivity();
					} else {
						HaloToast.show(this, "密码重置失败");
					}
				} else {
					HaloToast.show(this, "密码重置失败");
				}
			}
		}else{
			HaloToast.show(this,R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
}
