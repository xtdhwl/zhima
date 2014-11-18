package com.zhima.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.RegisterProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 手机注册_设置密码
 * @className:PhoneSetupActivity
 * @Description: 设置密码
 * @author yusonglin
 * @date 2012-7-23 
 */
public class PhoneSetupActivity extends LoginBaseActivity implements OnClickListener {
	
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
		
		setTopBar();
		findView();
		setUpView();
		initData();
		
	}
	private void setTopBar() {
		ViewInitTools.setTopBar(this, "设置密码",View.GONE,null);
	}
	private void initData() {
		Intent intent = getIntent();
		phoneNumber = intent.getStringExtra("phoneNumber");
		authcode = intent.getStringExtra("authcode");
	}
	/**
	 * 寻找控件
	 */
	private void findView() {
		
		mPasswordEdit = (EditText) this.findViewById(R.id.edt_phoneRegister_password);
		mNextStepBt = (Button) this.findViewById(R.id.btn_phoneRegister_setUp_nextStep);
	
	}
	/**
	 * 设置控件
	 */
	private void setUpView() {
		mNextStepBt.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_phoneRegister_setUp_nextStep:
			String password = mPasswordEdit.getText().toString().trim();
			//判断密码是否合格
//			String r = "^(?=.*\\d.*)(?=.*[a-zA-Z].*)(?=.*[-`~!@#$%^&*()_+\\|\\\\=,./?><\\{\\}\\[\\]].*).*$";
//			if(!password.matches("{6,20}")){
//			if(!password.matches("[\\p{ASCII}]+{6,20}")){
//			if(!password.matches("[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,20}")){
//			if(!password.matches("/^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,22}$/")){
//			if(!password.matches("[A-Za-z0-9]{6,20}")){
			if(!password.matches("^[a-zA-Z0-9~!@#$%\\^&*_]{6,20}$")){
				mPasswordEdit.setText("");
				HaloToast.show(this, R.string.please_input_rightPassword);
				return;
			}
			
			LoginService.getInstance(this).registerByMobile(phoneNumber, password,authcode, this);
			
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
			if (protocol.getProtocolType() == ProtocolType.REGISTER_PROTOCOL) {
				if (protocol.isHandleSuccess()) {

					RegisterProtocolHandler p = (RegisterProtocolHandler) protocol;
					Intent intent = new Intent(this,PersonalDataSetupActivity.class);
					intent.putExtra(ACTIVITY_EXTRA, p.getZMUserId());
					startActivity(intent);
				} else {
					//
					HaloToast.show(this, "手机注册失败", 1);
				}
			}
		}else{
			HaloToast.show(this,R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
}
