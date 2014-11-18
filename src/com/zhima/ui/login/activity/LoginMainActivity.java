package com.zhima.ui.login.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.utils.QHttpClient;
import com.zhima.R;
import com.zhima.base.config.FilePathConfig;
import com.zhima.base.consts.ZMConsts.LoginType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.LoginFrom3ProtocolHandler;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.LoginProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.LogonUser;
import com.zhima.data.service.LoginService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.QQShareActivity;
import com.zhima.ui.share.QQWeiboLoginActivity;
import com.zhima.ui.share.SinaWeiboShare;
import com.zhima.ui.share.SinaWeiboShare.SinaLoginListener;
import com.zhima.ui.tools.LoginSkipHelper;

/**
 * 登陆账户
 * @className:LoginActivity
 * @Description 登录
 * @author yusonglin
 * @Date 2012-07-23 上午
 */
public class LoginMainActivity extends LoginBaseActivity implements OnClickListener {
	
	private static final String TAG = "LoginMainActivity";
	
	private static final int QQ_LOGIN = 1;
//	private static final int SINA_LOGIN = SinaWeiboShare.SINA_LOGIN;
	
	/** 用户名输入框 */
	private EditText mUsernameEdit;
	/** 密码输入 */
	private EditText mPasswordEdit;
	/** 登录 */
	private Button mLoginButton;
	/** 注册  */
	private Button mRegisterButton;
	/** 忘记密码 */
	private TextView mFindPwdButton;
	
	/** 新浪微博登陆 */
	private LinearLayout mSinaLoginLayout;
	/** qq微博登陆 */
	private LinearLayout mQQLoginLayout;
	
	private String mAccount;
	
	private int mLoginType;
	private OAuthV1 oAuth;
	
//	private String mSinaReturnUrl;
	private String mQqReturnUrl;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (!NetUtils.isNetworkAvailable(LoginMainActivity.this)) {
					HaloToast.show(LoginMainActivity.this, R.string.network_request_failed);
					return;
				}
				dismissWaitingDialog();
				Intent intent = new Intent(LoginMainActivity.this, QQWeiboLoginActivity.class);
				intent.putExtra("oauth", oAuth);
				startActivityForResult(intent, 1);
				break;
			case 1:
				excuteQQLogin();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		setTopBar();
		findView();
		setUpView();
	}

	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("登录");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}
	
	/**
	 * 寻找控件
	 */
	private void findView() {
		
		mUsernameEdit = (EditText) this.findViewById(R.id.edt_login_username);
		mPasswordEdit = (EditText) this.findViewById(R.id.edt_login_password);
		mLoginButton = (Button) this.findViewById(R.id.btn_login);
		mRegisterButton = (Button) this.findViewById(R.id.btn_login_register);
		mFindPwdButton = (TextView) this.findViewById(R.id.btn_login_forget_password);
		
		mSinaLoginLayout = (LinearLayout) this.findViewById(R.id.layout_login_sina);
		mQQLoginLayout = (LinearLayout) this.findViewById(R.id.layout_login_qq);
	}
	
	/**
	 * 设置控件
	 */
	private void setUpView() {
		
		mLoginButton.setOnClickListener(this);
		mRegisterButton.setOnClickListener(this);
		mFindPwdButton.setOnClickListener(this);
		
		mSinaLoginLayout.setOnClickListener(this);
		mQQLoginLayout.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login://登录
			String userName = mUsernameEdit.getText().toString().trim();
			String password = mPasswordEdit.getText().toString().trim();
			if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
				HaloToast.show(this,"用户名或密码不能为空");
				mUsernameEdit.setText("");
				mPasswordEdit.setText("");
				return;
			}
			LoginService.getInstance(LoginMainActivity.this).login(userName, password, LoginType.MANUAL, this);
			break;
		case R.id.btn_login_register://注册
			Intent phoneRegIntent = new Intent(LoginMainActivity.this,PhoneRegisterActivity.class);
			startActivity(phoneRegIntent);
			
//			SelectListDialog registerDialog = new SelectListDialog(this,mRegisterButton);
//			registerDialog.setTitle("注册方式");
//			registerDialog.setoptionNames(new String[]{"手机注册","邮箱注册"});
//			registerDialog.setOnBtItemClickListener(new OnBtItemClicklistener() {
//				
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position,
//						long id) {
//					switch(position){
//					case 0:
//						break;
//					case 1:
////						Intent emailRegIntent = new Intent(LoginMainActivity.this,EmailRegisterActivity.class);
////						startActivity(emailRegIntent);
//						break;
//					}
//				}
//			});
//			registerDialog.show();
			
			break;
		case R.id.btn_login_forget_password://忘记密码
			
			Intent phoneFindIntent = new Intent(LoginMainActivity.this,FindPwdByPhoneActivity.class);
			startActivity(phoneFindIntent);
			
//			SelectListDialog findPwdDialog = new SelectListDialog(this,mFindPwdButton);
//			findPwdDialog.setTitle("找回方式");
//			findPwdDialog.setoptionNames(new String[]{"手机账号找回","邮箱账号找回"});
//			findPwdDialog.setOnBtItemClickListener(new OnBtItemClicklistener() {
//				
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position,
//						long id) {
//					switch(position){
//					case 0:
//						break;
//					case 1:
////						Intent emailRegIntent = new Intent(LoginMainActivity.this,FindPwdByEmailActivity.class);
////						startActivity(emailRegIntent);
//						break;
//					}
//				}
//			});
//			findPwdDialog.show();
			break;
		case R.id.layout_login_sina:
//			Intent intent = new Intent(this,From3BindZmActivity.class);
//			startActivity(intent);
			
			SinaWeiboShare sinaWeiboShare = SinaWeiboShare.getInstance(this, this);
			sinaWeiboShare.setSinaLoginListener(new SinaLoginListener() {
				
				@Override
				public void login(boolean isSuccess,String uid,String returnUrl) {
					if(isSuccess){
						loginFrom3("sina", uid, returnUrl);
					} else {
						HaloToast.show(LoginMainActivity.this, "新浪微博登陆失败");
					}
				}
			});
			sinaWeiboShare.login();
			break;
		case R.id.layout_login_qq:
//			Intent intent1 = new Intent(this,PersonalDataSetupActivity.class);
//			startActivity(intent1);
			
			
			startWaitingDialog("", "请稍等...");
			new Thread(){
				public void run() {
					oAuth = new OAuthV1("null");
					oAuth.setOauthConsumerKey(QQShareActivity.oauthConsumeKey);
					oAuth.setOauthConsumerSecret(QQShareActivity.oauthConsumerSecret);
					// 关闭OAuthV1Client中的默认开启的QHttpClient。
					OAuthV1Client.getQHttpClient().shutdownConnection();
					// 为OAuthV1Client配置自己定义QHttpClient。
					OAuthV1Client.setQHttpClient(new QHttpClient());
					try {
						oAuth = OAuthV1Client.requestToken(oAuth);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				};
			}.start();
			break;
		}
	}
	
	private void loginFrom3(String from,String uid,String returnUrl){
		LoginService.getInstance(LoginMainActivity.this).loginFrom3(from, uid, returnUrl, LoginMainActivity.this);
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "正在登录...");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			int protocolType = protocol.getProtocolType();
			if(protocolType == ProtocolType.LOGIN_PROTOCOL){
				// 网络请求成功
				if (protocol.isHandleSuccess()) {
					LoginProtocolHandler protocolhandler = (LoginProtocolHandler)protocol;
					if (protocolhandler.getLogonUser() != null) {
						LogonUser logonUser = protocolhandler.getLogonUser();
						Logger.getInstance(TAG).debug("本次登录用户的profile:" + protocolhandler.getLogonUser().toString());
						if (LoginSkipHelper.getLoginSuccessActivity() != null) {
							// 跳转到指定页面						
//							ActivitySwitcher.switchActivity(this, LoginSkipHelper.getLoginSuccessActivity());
							HaloToast.show(getApplicationContext(), R.string.login_success);
							dismissLoginActivity();//销毁login全部activity
						} else {
							// TODO 在此添加跳转到页面的代码
						}
						finish();
					}
				} else {
					HaloToast.show(LoginMainActivity.this,R.string.usernameAndPwd_isWrong);
				}
			}else if(protocolType == ProtocolType.LOGIN_FROM3_PROTOCOL){
				if (protocol.isHandleSuccess()) {
					LoginFrom3ProtocolHandler handler = (LoginFrom3ProtocolHandler)protocol;
					LogonUser userProfile = handler.getUserProfile();
					boolean isBoundUser = userProfile.isBoundUser();
					if(isBoundUser){
						HaloToast.show(LoginMainActivity.this, "登录芝麻账户成功："+userProfile.getZMUserId()+"---"+userProfile.getUserId());
						dismissLoginActivity();//销毁login全部activity
					}else{
						Intent intent = new Intent(this,From3BindZmActivity.class);
						startActivity(intent);
					}
				} else {
					HaloToast.show(getApplicationContext(), R.string.load_failed);
				}
			}
		} else {
			HaloToast.show(LoginMainActivity.this,R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
	
	/*
	 * 通过读取OAuthV1AuthorizeWebView返回的Intent，获取用户授权后的验证码
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == QQ_LOGIN) {
			if (resultCode == QQWeiboLoginActivity.RESULT_CODE) {
				mQqReturnUrl = data.getStringExtra("returnUrl");
				
				startWaitingDialog("", "请稍等");
				// 从返回的Intent中获取验证码
				oAuth = (OAuthV1) data.getExtras().getSerializable("oauth");
				new Thread() {
					public void run() {
						try {
							oAuth = OAuthV1Client.accessToken(oAuth);
							QQShareActivity.oAuth = oAuth;
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.start();
//				if (bind_from_space == 1) {
//					finish();
//				}
			}
		}/*else if(requestCode == SINA_LOGIN && resultCode == WeiboLoginActivity.WeiboLoginCode){
			mSinaReturnUrl = data.getStringExtra("returnUrl");
		}*/
	}
	 
	private void excuteQQLogin() {
		dismissWaitingDialog();
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		String fileName = FileHelper.getRootDir() + FilePathConfig.OAUTH_FILE;
		File file = new File(fileName);
		try {
			fos = new FileOutputStream(file, false);
			out = new ObjectOutputStream(fos);
			out.writeObject(oAuth);
			
			loginFromQQ();//QQ登录成功请求接口
			
			out.flush();
			fos.flush();
		} catch (Exception e) {
			HaloToast.show(LoginMainActivity.this, "QQ微博登陆失败");
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}
	}
	
	private void loginFromQQ() {
		try {
			UserAPI userAPI = new UserAPI(OAuthConstants.OAUTH_VERSION_1);
			JSONTokener jsonParser = new JSONTokener(userAPI.info(oAuth, "json"));
			JSONObject vo = (JSONObject) jsonParser.nextValue();
			JSONObject jsonObject = vo.getJSONObject("data");
			loginFrom3("qq",jsonObject.getString("openid"),mQqReturnUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
