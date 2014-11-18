package com.zhima.ui.setting.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.utils.QHttpClient;
import com.zhima.R;
import com.zhima.base.config.FilePathConfig;
import com.zhima.base.logger.Logger;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.NetUtils;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.QQShareActivity;
import com.zhima.ui.share.QQWeiboLoginActivity;
import com.zhima.ui.share.RenRenSpaceShare;
import com.zhima.ui.share.RenRenSpaceShare.RenrenLoginListener;
import com.zhima.ui.share.SinaWeiboShare;
import com.zhima.ui.share.SinaWeiboShare.SinaLoginListener;

/**
 * @ClassName ShareConfigActivity
 * @Description 绑定分享账号
 * @author jiang
 * @date 2012-11-21 上午11:50:56
 */
public class ShareConfigActivity extends BaseActivity {
	private RelativeLayout sinaLayout;
	private RelativeLayout qqLayout;
	private RelativeLayout renrenLayout;
	private CheckBox cb_sina;
	private CheckBox cb_qq;
	private CheckBox cb_renren;

	private String oauthCallback = "null";
	private OAuthV1 oAuth;
	public static String QQ_SHARE_TYPE = "qqshareType";
	private int bind_from_space;
	private final static String TAG = "ShareConfigActivity";

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:

				if (!NetUtils.isNetworkAvailable(ShareConfigActivity.this)) {
					HaloToast.show(ShareConfigActivity.this, R.string.network_request_failed);
					return;
				}

				Intent intent = new Intent(ShareConfigActivity.this, QQWeiboLoginActivity.class);
				intent.putExtra("oauth", oAuth);
				startActivityForResult(intent, 1);

				dismissWaitingDialog();
				break;
			case 1:
				dismissWaitingDialog();
				String info = (String) msg.obj;
				Toast.makeText(ShareConfigActivity.this, info, 0).show();
				break;
			case 2:
				dismissWaitingDialog();
				FileOutputStream fos = null;
				ObjectOutputStream out = null;
				String fileName = FileHelper.getRootDir() + FilePathConfig.OAUTH_FILE;
				File file = new File(fileName);
				try {
					fos = new FileOutputStream(file, false);
					out = new ObjectOutputStream(fos);
					out.writeObject(oAuth);
					HaloToast.show(ShareConfigActivity.this, "绑定成功");
					out.flush();
					fos.flush();
				} catch (Exception e) {
					HaloToast.show(ShareConfigActivity.this, "绑定失败，请重新绑定");
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
				initCheckState();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_config_activity);

		setTopBar();
		initView();
		initOAuth();

		Intent intent = getIntent();
		bind_from_space = intent.getExtras().getInt(QQ_SHARE_TYPE, 0);
		if (bind_from_space == 1) {
			showDialog();
			bindTencentShare();
		}
	}

	/**
	 * @Title: initCheckState
	 * @Description:初始化是否绑定的状态
	 * @param
	 * @return void
	 */
	private void initCheckState() {
		// TODO Auto-generated method stub
		String fileName = FileHelper.getRootDir() + FilePathConfig.OAUTH_FILE;
		File file = new File(fileName);
		if (file != null && file.exists()) {
			cb_qq.setChecked(true);
		} else {
			cb_qq.setChecked(false);
		}
	}

	/**
	 * @Title: initOAuth
	 * @Description:TODO
	 * @param
	 * @return void
	 */
	private void initOAuth() {
		String fileName = FileHelper.getRootDir() + FilePathConfig.OAUTH_FILE;
		File file = new File(fileName);
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			oAuth = (OAuthV1) ois.readObject();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			oAuth = new OAuthV1(oauthCallback);
			oAuth.setOauthConsumerKey(QQShareActivity.oauthConsumeKey);
			oAuth.setOauthConsumerSecret(QQShareActivity.oauthConsumerSecret);

			// 关闭OAuthV1Client中的默认开启的QHttpClient。
			OAuthV1Client.getQHttpClient().shutdownConnection();
			// 为OAuthV1Client配置自己定义QHttpClient。
			OAuthV1Client.setQHttpClient(new QHttpClient());
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}

	}

	/**
	 * @Title: initView
	 * @Description:初始化控件
	 * @param
	 * @return void
	 */
	private void initView() {
		// TODO Auto-generated method stub
		sinaLayout = (RelativeLayout) this.findViewById(R.id.layout_share_config_sina);
		qqLayout = (RelativeLayout) findViewById(R.id.layout_share_config_qq);
		renrenLayout = (RelativeLayout) findViewById(R.id.layout_share_config_renren);
		cb_sina = (CheckBox) findViewById(R.id.cb_share_config_sina);
		cb_qq = (CheckBox) findViewById(R.id.cb_share_config_qq);
		cb_renren = (CheckBox) findViewById(R.id.cb_share_config_renren);
		cb_sina.setChecked(SinaWeiboShare.getInstance(this, this).isLogin());
		cb_renren.setChecked(RenRenSpaceShare.getInstance(this, this).isLogin());
		cb_sina.setClickable(false);
		cb_renren.setClickable(false);
		sinaLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cb_sina.setChecked(false);
				bindSina(!SinaWeiboShare.getInstance(ShareConfigActivity.this, ShareConfigActivity.this).isLogin());
			}
		});

		// 腾讯网绑定和注销
		qqLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cb_qq.isChecked()) {
					cb_qq.setChecked(false);
					cancelTencentShare();
				} else {
					cb_qq.setChecked(true);
					showDialog();
					bindTencentShare();
				}
			}
		});

		cb_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cb_qq.isChecked()) {
					showDialog();
					bindTencentShare();
				} else {
					cancelTencentShare();
				}
			}
		});
		// 人人网绑定和注销
		renrenLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cb_renren.setChecked(false);
				bindRenren(!RenRenSpaceShare.getInstance(ShareConfigActivity.this, ShareConfigActivity.this).isLogin());
			}
		});

	}

	private void bindSina(boolean isChecked) {
		SinaWeiboShare instance = SinaWeiboShare.getInstance(this, this);
		instance.setSinaLoginListener(new SinaLoginListener() {

			@Override
			public void login(boolean isSuccess,String uid,String returnUrl) {
				cb_sina.setChecked(isSuccess);
				if (!ShareConfigActivity.this.isFinishing()) {
					if (isSuccess) {
						HaloToast.show(ShareConfigActivity.this, "新浪微博绑定成功");
					} else {
						HaloToast.show(ShareConfigActivity.this, "新浪微博绑定失败");
					}
				}
			}
		});
		if (isChecked) {
			instance.login();
		} else {
			instance.logout();
			HaloToast.show(this, "新浪微博注销成功");
		}
	}

	private void bindRenren(boolean isChecked) {
		RenRenSpaceShare instance = RenRenSpaceShare.getInstance(this, this);
		instance.setRenrenLoginListener(new RenrenLoginListener() {

			@Override
			public void login(boolean isSuccess) {
				cb_renren.setChecked(isSuccess);
				if (!ShareConfigActivity.this.isFinishing()) {
					if (isSuccess) {
						HaloToast.show(ShareConfigActivity.this, "人人网绑定成功");
					} else {
						HaloToast.show(ShareConfigActivity.this, "人人网绑定失败");
					}
				}
			}
		});
		if (isChecked) {
			// 打开绑定
			instance.login();
		} else {
			// 注销
			instance.logout();
			HaloToast.show(this, "人人网注销成功");
		}
	}

	private void bindTencentShare() {
		new Thread() {
			public void run() {
				try {
					// 向腾讯微博开放平台请求获得未授权的Request_Token
					oAuth = OAuthV1Client.requestToken(oAuth);
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void cancelTencentShare() {
		String fileName = FileHelper.getRootDir() + FilePathConfig.OAUTH_FILE;
		File file = new File(fileName);
		if (file != null && file.exists()) {
			file.delete();
		} else {

		}
	}

	private void showDialog() {
		startWaitingDialog("", "请稍等");
	}

	@Override
	public void onPause() {
		super.onPause();
		dismissWaitingDialog();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initCheckState();
	}

	@Override
	public void onBackPressed() {
		// 关闭OAuthV1Client中的自定义的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();
		finish();
	}

	/*
	 * 通过读取OAuthV1AuthorizeWebView返回的Intent，获取用户授权后的验证码
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == QQWeiboLoginActivity.RESULT_CODE) {
				showDialog();
				// 从返回的Intent中获取验证码
				oAuth = (OAuthV1) data.getExtras().getSerializable("oauth");
				new Thread() {
					public void run() {
						try {
							oAuth = OAuthV1Client.accessToken(oAuth);
							QQShareActivity.oAuth = oAuth;
							Message msg = new Message();
							msg.what = 2;
							mHandler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.start();
				if (bind_from_space == 1) {
					finish();
				}
			}
		}
	}

	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("绑定账号");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}

}
