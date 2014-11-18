package com.zhima.ui.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.BindExternalUserProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.MobileIsRegisteredProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.StringHelper;
import com.zhima.data.model.User;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 第三方登陆成功后绑定芝麻账户
 * @ClassName: From3BindZmActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-16 上午11:11:11
*/
public class From3BindZmActivity extends LoginBaseActivity implements OnClickListener {

//	/** 未注册用户 */
//	private TextView mUnregisteredText;
//	/** 已注册用户 */
//	private TextView mRegisteredText;
	
	//----未注册用户 View
	private LinearLayout mUnregisteredLayout;
	private EditText mPhoneNumberEdit;
	private CheckBox mAgreeClauseCb;
//	private TextView mUserClauseText;
	private TextView mPrivacyClauseText;
	private Button mNextStepBtn;
	
	//----已注册用户 View
	private LinearLayout mRegisteredLayout;
	private EditText mUserNameEdit;
	private EditText mPasswordEdit;
	private Button mLoginBtn;
	private String phoneNumber;
	
	private int mRegisterId = R.id.no_registed_user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_from3_bind_zm_activity);
		
		setTopbar();
		findView();
		setUpView();
	}

	private void setTopbar() {
		ViewInitTools.setTopBar(this, "绑定芝麻账户",View.VISIBLE,new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ZhimaPopupMenu zhimaPopupMenu = new ZhimaPopupMenu(From3BindZmActivity.this);
				zhimaPopupMenu.setSelection(mRegisterId);
				zhimaPopupMenu.setMenuItems(R.menu.register_type);
				zhimaPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public void onMenuItemClick(ZhimaMenuItem item, int position) {
						switch (item.getId()) {
						case R.id.no_registed_user:
							mRegisterId = R.id.no_registed_user;
							mUnregisteredLayout.setVisibility(View.VISIBLE);
							mRegisteredLayout.setVisibility(View.GONE);
							break;
						case R.id.registed_user:
							mRegisterId = R.id.registed_user;
							mUnregisteredLayout.setVisibility(View.GONE);
							mRegisteredLayout.setVisibility(View.VISIBLE);
							break;
						}
					}
				});
				zhimaPopupMenu.show(getTopbar());
			}
		});
	}

	private void findView() {
//		mUnregisteredText = (TextView) this.findViewById(R.id.txt_from3Bind_Zm_unregistered);
//		mRegisteredText = (TextView) this.findViewById(R.id.txt_from3Bind_Zm_registered);
		
		mUnregisteredLayout = (LinearLayout) this.findViewById(R.id.layout_from3Bind_unregistered);
		mPhoneNumberEdit = (EditText) this.findViewById(R.id.edt_from3Bind_honeRegister_phoneNum);
		mAgreeClauseCb = (CheckBox) this.findViewById(R.id.cb_from3Bind_phoneRegister_agree_clause);
//		mUserClauseText = (TextView) this.findViewById(R.id.txt_from3Bind_phoneRegister_use_clause);
		mPrivacyClauseText = (TextView) this.findViewById(R.id.txt_from3Bind_phoneRegister_privacy_clause);
		mNextStepBtn = (Button) this.findViewById(R.id.btn_from3Bind_nextStep);
		
		mRegisteredLayout = (LinearLayout) this.findViewById(R.id.layout_from3Bind_registered);
		mUserNameEdit = (EditText) this.findViewById(R.id.edt_from3Bind_Login_username);
		mPasswordEdit = (EditText) this.findViewById(R.id.edt_from3Bind_Login_password);
		mLoginBtn = (Button) this.findViewById(R.id.btn_from3Bind_Login);
	}

	private void setUpView() {
//		mUnregisteredText.setOnClickListener(this);
//		mRegisteredText.setOnClickListener(this);
		mNextStepBtn.setOnClickListener(this);
		mLoginBtn.setOnClickListener(this);
		
//		mUserClauseText.setOnClickListener(this);
		mPrivacyClauseText.setOnClickListener(this);
		
		mAgreeClauseCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mPhoneNumberEdit.setEnabled(true);
					mNextStepBtn.setBackgroundResource(R.color.login_button_bg);
				}else{
					mNextStepBtn.setEnabled(false);
					mNextStepBtn.setBackgroundResource(R.color.light_gray2);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.txt_from3Bind_Zm_unregistered://未注册用户
//			mUnregisteredLayout.setVisibility(View.VISIBLE);
//			mRegisteredLayout.setVisibility(View.GONE);
//			break;
//		case R.id.txt_from3Bind_Zm_registered://已注册用户
//			mUnregisteredLayout.setVisibility(View.GONE);
//			mRegisteredLayout.setVisibility(View.VISIBLE);
//			break;
		case R.id.btn_from3Bind_nextStep://未注册用户 手机注册 下一步
			if(!mAgreeClauseCb.isChecked()){
				return;
			}
			phoneNumber = mPhoneNumberEdit.getText().toString().trim();
			if(!StringHelper.isMobilePhone(phoneNumber)){
				HaloToast.show(this, R.string.please_input_rightPhoneNum);
				return;
			}
			LoginService.getInstance(this).mobileIsRegistered(phoneNumber, this);
			break;
		case R.id.btn_from3Bind_Login://已注册用户 登录
			String userName = mUserNameEdit.getText().toString().trim();
			String password = mPasswordEdit.getText().toString().trim();
			if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
				HaloToast.show(this,"用户名或密码不能为空");
				mUserNameEdit.setText("");
				mPasswordEdit.setText("");
				return;
			}
			LoginService.getInstance(this).bindExternalUser(userName, password, this);
			break;
//		case R.id.txt_from3Bind_phoneRegister_use_clause: //用户条款
//			
//			break;
		case R.id.txt_from3Bind_phoneRegister_privacy_clause://隐私条款
			
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
			int protocolType = protocol.getProtocolType();
			if(protocolType == ProtocolType.MOBILE_IS_REGISTERED_PROTOCOL){
				if(protocol.isHandleSuccess()){
					MobileIsRegisteredProtocolHandler handler = (MobileIsRegisteredProtocolHandler) protocol;
					if(handler.isRegistered()){
						HaloToast.show(From3BindZmActivity.this, R.string.mobile_isRegister);
					}else{
						LoginService.getInstance(From3BindZmActivity.this).requestSendSmsVerifyCode(phoneNumber, From3BindZmActivity.this);
					}
				}else{
					
				}
			}else if (protocolType == ProtocolType.REQUEST_SEND_VERIFYCODE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					Intent authcodeIntent = new Intent(this,PhoneAuthcodeActivity.class);
					authcodeIntent.putExtra("phoneNumber", phoneNumber);
					startActivity(authcodeIntent);
				} else {
					//
				}
			}else if (protocolType == ProtocolType.LOGIN_BIND_EXTERNAL_USER) {
				if (protocol.isHandleSuccess()) {
					BindExternalUserProtocolHandler handler = (BindExternalUserProtocolHandler) protocol;
					User userProfile = handler.getUserProfile();
				} else {
					
				}
			}
		}else{
			HaloToast.show(this,R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
}
