package com.zhima.ui.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.tencent.weibo.utils.QHttpClient;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
/**
 * @ClassName TencentShare
 * @Description 腾讯分享
 * @author jiang
 * @date 2012-11-23 上午10:37:07
 */
public class TencentShare {

	private Context mContext;
	private BaseActivity mActivity;
	// 分享的内容
	private String mContent;
	//
	private String oauthCallback = "null";

	public static OAuthV1 oAuth;

//	private ProgressDialog dialog;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(mContext, OAuthV1AuthorizeWebView.class);
				intent.putExtra("oauth", oAuth);
				((BaseActivity) mContext).startActivityForResult(intent, 1);
				mActivity.dismissWaitingDialog();
				break;
			case 1:
				mActivity.dismissWaitingDialog();
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
					HaloToast.show(mContext, "分享成功");
				} else {
					HaloToast.show(mContext, "分享失败，请检查网络");
				}
				break;
			case 2:
				mActivity.dismissWaitingDialog();

				FileOutputStream fos = null;
				ObjectOutputStream out = null;
				try {
					 fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),
							"zhima/oauth.txt"), false);
					 out = new ObjectOutputStream(fos);
					out.writeObject(oAuth);
					out.flush();
					fos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(out != null){
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(fos != null){
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

	public TencentShare(Context context,BaseActivity activity, String content) {
		this.mContext = context;
		this.mActivity = activity;
		this.mContent = content;
	}

	/**
	 * @Title: doShare
	 * @Description:分享至腾讯
	 * @param
	 * @return void
	 */
	public void doShare() {
		if (checkBindStatus()) {
			initAuthV1();
			shareTencent();
		} else {
			// initAuthV1();
			logQQShare();
		}
	}

	private void initAuthV1() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			 fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(),
					"zhima/oauth.txt"));
			 ois = new ObjectInputStream(fis);
			oAuth = (OAuthV1) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			oAuth = new OAuthV1(oauthCallback);
			oAuth.setOauthConsumerKey(QQShareActivity.oauthConsumeKey);
			oAuth.setOauthConsumerSecret(QQShareActivity.oauthConsumerSecret);
		}finally{
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis != null){
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
	}

	private boolean checkBindStatus() {
		File file = new File(Environment.getExternalStorageDirectory(), "zhima/oauth.txt");
		if (file != null && file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * @Title: shareTencent
	 * @Description:分享微博
	 * @param
	 * @return void
	 */
	private void shareTencent() {
		final String content = mContent;
		showDialog();
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

	private void logQQShare() {
		Intent intent = new Intent(mContext, QQShareActivity.class);
		intent.putExtra(QQShareActivity.QQ_SHARE_TYPE, 1);
		intent.putExtra(QQShareActivity.QQ_SHARE_CONTENT, mContent);
		mContext.startActivity(intent);
	}

	private void showDialog() {
		mActivity.startWaitingDialog("", "请稍等");
	}

	public void onBackPressed() {
		// 关闭OAuthV1Client中的自定义的QHttpClient。
		OAuthV1Client.getQHttpClient().shutdownConnection();
	}
}