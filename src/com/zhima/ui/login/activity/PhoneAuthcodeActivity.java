package com.zhima.ui.login.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 手机注册_发送验证码
 * @ClassName: PhoneAuthcodeActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-8-13 下午4:21:39
*/
public class PhoneAuthcodeActivity extends LoginBaseActivity implements OnClickListener{

	/** 验证码 */
	private EditText mAuthcodeEdit;
	/** 再次发送 */
	private Button mSendAgainBtn;
	/** 下一步 */
	private Button mNextStepBt;
	
	private boolean isFindPwd;
	private String phoneNumber;
	private String authcode;
	
	private CountDownTimer timer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_phonereg_authcode_activity);
		
		setTopBar();
		findView();
		setUpView();
		initTimer();
	}

	private void initTimer() {
		timer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				mSendAgainBtn.setText("再次发送("+(millisUntilFinished/1000)+"s)");
				mSendAgainBtn.setEnabled(false);
				mSendAgainBtn.setTextColor(Color.GRAY);
			}

			@Override
			public void onFinish() {
				mSendAgainBtn.setText("再次发送");
				mSendAgainBtn.setEnabled(true);
				mSendAgainBtn.setTextColor(Color.BLACK);
			}
		}.start();
	}

	private void setTopBar() {
		ViewInitTools.setTopBar(this, "验证码",View.GONE,null);
	}

	private void findView() {
		mAuthcodeEdit = (EditText) this.findViewById(R.id.edt_phoneResgiter_authcode);
		mNextStepBt = (Button) this.findViewById(R.id.btn_phoneResgiter_authcode_nextStep);
		
		mSendAgainBtn = (Button) this.findViewById(R.id.login_phoneReg_authcode_sendAgain);
	}

	private void setUpView() {
		Intent intent = getIntent();
		phoneNumber = intent.getStringExtra("phoneNumber");
		isFindPwd = intent.getBooleanExtra("isFindPwd", false);
		mNextStepBt.setOnClickListener(this);
		mSendAgainBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_phoneResgiter_authcode_nextStep:
			authcode = mAuthcodeEdit.getText().toString().trim();
			if(TextUtils.isEmpty(authcode)){
				HaloToast.show(this,R.string.authCode_isWrong);
				return;
			}
			
			if(isFindPwd){
				LoginService.getInstance(this).validateResetVerifyCode(phoneNumber, authcode, this);
			}else{
				LoginService.getInstance(this).validateRegisterVerifyCode(phoneNumber, authcode, this);
//				LoginService.getInstance(this).registerByMobile(phoneNumber, password, verifyCode, callBack)
			}
			break;
		case R.id.login_phoneReg_authcode_sendAgain://再次发送
			if(isFindPwd){
				LoginService.getInstance(this).requestSendResetSmsVerifyCode(phoneNumber, this);
			}else{
				LoginService.getInstance(this).requestSendSmsVerifyCode(phoneNumber, this);
			}
			timer.start();
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
			if (protocol.getProtocolType() == ProtocolType.REQUEST_VALIDATE_VERIFYCODE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					if (isFindPwd) {
						Intent intent = new Intent(this,
								SetNewPwdActivity.class);
						intent.putExtra("phoneNumber", phoneNumber);
						intent.putExtra("authcode", authcode);
						startActivity(intent);
					} else {
						Intent intent = new Intent(this,
								PhoneSetupActivity.class);
						intent.putExtra("phoneNumber", phoneNumber);
						intent.putExtra("authcode", authcode);
						startActivity(intent);
					}
				} else {
					//
					HaloToast.show(this, R.string.authCode_isWrong);
				}
			} else if (protocol.getProtocolType() == ProtocolType.REQUEST_SEND_VERIFYCODE_PROTOCOL
					|| protocol.getProtocolType() == ProtocolType.REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), "发送验证码成功");
				} else {
					HaloToast.show(getApplicationContext(), "发送验证码失败");
				}
			}
		} else {
			HaloToast.show(this, R.string.network_request_failed, 1);
		}
		dismissWaitingDialog();
	}
	
	@Override
	public void onDestroy() {
		//TODO
		super.onDestroy();
		if(timer!=null){
			timer.cancel();
		}
	}
}
