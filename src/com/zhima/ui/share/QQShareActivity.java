package com.zhima.ui.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.utils.QHttpClient;
import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName QQShareActivity
 * @Description 腾讯分享的过渡页面
 * @author jiang
 * @date 2012-11-27 上午11:46:28
 */
public class QQShareActivity extends BaseActivity {
	//
	private String oauthCallback = "null";
	// APP KEY
	public static final String oauthConsumeKey = "100657414";
	// APP SECRET
	public static final String oauthConsumerSecret = "42f200a0912f796e7964408a8b7f927a";

	public static OAuthV1 oAuth;

	public static String QQ_SHARE_TYPE = "qqshareType";
	public static String QQ_SHARE_CONTENT = "qqshareContent";
	public static int bind_from_space;
	private String mContent;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(QQShareActivity.this, OAuthV1AuthorizeWebView.class);
				intent.putExtra("oauth", oAuth);
				startActivityForResult(intent, 1);

				dismissWaitingDialog();
				break;
			case 1:
				dismissWaitingDialog();
				JSONObject json;
				int info = -1;
				try {
					if (msg != null && msg.obj != null && !msg.obj.equals("")) {
						json = new JSONObject((String) msg.obj);
						info = json.getInt("ret");
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (info == 0) {
					HaloToast.show(QQShareActivity.this, "分享成功");
				} else {
					HaloToast.show(QQShareActivity.this, "分享失败，请检查网络");
				}
				break;
			case 2:
				dismissWaitingDialog();
				FileOutputStream fos = null;
				ObjectOutputStream out = null;
				try {
					fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "zhima/oauth.txt"),
							false);
					out = new ObjectOutputStream(fos);
					out.writeObject(oAuth);
					out.flush();
					fos.flush();
					shareTencent();
				} catch (Exception e) {
					HaloToast.show(QQShareActivity.this, "绑定失败，重新绑定后分享");
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tencent_share_transit);
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "zhima/oauth.txt"));
			ois = new ObjectInputStream(fis);
			oAuth = (OAuthV1) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			oAuth = new OAuthV1(oauthCallback);
			oAuth.setOauthConsumerKey(oauthConsumeKey);
			oAuth.setOauthConsumerSecret(oauthConsumerSecret);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 关闭OAuthV1Client中的默认开启的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();

		// 为OAuthV1Client配置自己定义QHttpClient。
		OAuthV1Client.setQHttpClient(new QHttpClient());

		Intent intent = getIntent();
		bind_from_space = intent.getExtras().getInt(QQ_SHARE_TYPE, 0);
		mContent = intent.getExtras().getString(QQ_SHARE_CONTENT);
		if (bind_from_space == 1) {
			showDialog();
			bindTencentShare();
		}

	}

	private void showDialog() {
		startWaitingDialog("", "正在加载");
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
		if (bind_from_space == 100) {
			finish();
		}
	}

	public void onBackPressed() {
		// 关闭OAuthV1Client中的自定义的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();
		finish();
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

	/**
	 * @Title: shareTencent
	 * @Description:分享微博
	 * @param
	 * @return void
	 */
	private void shareTencent() {
		final String content = mContent;
		new Thread() {
			public void run() {
				TAPI tAPI = new TAPI(OAuthConstants.OAUTH_VERSION_1);
				try {
					String response = tAPI.add(oAuth, "json", content, "127.0.0.1", " ", " ", "0");
					Message msg = new Message();
					msg.what = 1;
					msg.obj = response;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				tAPI.shutdownConnection();
			};
		}.start();
	}

	/*
	 * 通过读取OAuthV1AuthorizeWebView返回的Intent，获取用户授权后的验证码
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == OAuthV1AuthorizeWebView.RESULT_CODE) {
				oAuth = (OAuthV1) data.getExtras().getSerializable("oauth");

				new Thread() {
					public void run() {
						try {
							oAuth = OAuthV1Client.accessToken(oAuth);
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
}