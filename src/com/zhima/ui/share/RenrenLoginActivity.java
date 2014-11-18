package com.zhima.ui.share;

import org.apache.http.util.EncodingUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.api.connect.android.view.RenrenDialogListener;
import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.RenRenSpaceShare.LoginStatusListener;

public class RenrenLoginActivity extends BaseActivity {

	public static final String DEFAULT_REDIRECT_URI = "http://graph.renren.com/oauth/login_success.html";
//	public static final String DEFAULT_REDIRECT_URI = "http://graph.renren.com/oauth/login_success.html";
	
	private static final String LOG_TAG = "RenrenDialog";
	private static final int RENREN_BLUE = 0xFF005EAC;
//	private static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
//	private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };

	/**
	 * 验证过程被取消的错误码
	 */
	private static final String CODE_AUTH_CANCEL = "login_denied";
	protected String mUrl;
	protected RenrenDialogListener mListener;
//	private ProgressDialog progress;
	protected WebView webView;
//	private LinearLayout content;
//	private TextView title;
//	private boolean showTitle = false;
	private String mPostData;
	private boolean isPost = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_renren_login_activity);
		
		setTopbar();
		initData();
		findView();
		initView();
		setUp();
	}

	private void setUp() {
		RenRenSpaceShare.getInstance(this, this).setLoginStatusListener(new LoginStatusListener() {
			
			@Override
			public void login(boolean isSuccess) {
				if(!RenrenLoginActivity.this.isFinishing()){
					if(isSuccess){
						finish();
					}else{
						
					}
				}
			}
		});
	}

	private void setTopbar() {
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("人人网登录");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}

	private void findView() {
		webView = (WebView) this.findViewById(R.id.wv_renren_login_webview);
	}

	private void initView() {
//		progress = new ProgressDialog(this);
//		progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		progress.setMessage("Loading...");

//		content = new LinearLayout(this);
//		content.setOrientation(LinearLayout.VERTICAL);
		setUpWebView();
//		addContentView(content, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	private void setUpWebView() {
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSavePassword(true);        
		webView.getSettings().setSaveFormData(true);
		webView.setWebViewClient(new RenrenWebViewClient());
		if (isPost) {
			webView.postUrl(mUrl, EncodingUtils.getBytes(mPostData, "BASE64"));
		} else {
			webView.loadUrl(mUrl);
		}
//		FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(
//				ViewGroup.LayoutParams.FILL_PARENT,
//				ViewGroup.LayoutParams.FILL_PARENT);
//		webView.setLayoutParams(fill);
//		content.addView(webView);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mUrl = new String(intent.getStringExtra("url"));
		mUrl = mUrl.replace("https", "http");
		mListener = RenRenSpaceShare.getRenrenAuthListener();
	}
	
	private class RenrenWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(LOG_TAG, "Redirect URL: " + url);
			int b = mListener.onPageBegin(url);
			switch (b) {
			case RenrenDialogListener.ACTION_PROCCESSED:
				return true;
			case RenrenDialogListener.ACTION_DIALOG_PROCCESS:
				return false;
			}
			view.loadUrl(url);
//			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			dismissWaitingDialog();
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(LOG_TAG, "Webview loading URL: " + url);
			boolean b = mListener.onPageStart(url);
			if (b) {
				view.stopLoading();
				return;
			}
			super.onPageStarted(view, url, favicon);
//			progress.
			if(!isDialogShowing()){
				startWaitingDialog("", "正在加载");
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onReceivedError(errorCode, description, failingUrl);
//			progress.hide();
			dismissWaitingDialog();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mListener.onPageFinished(url);
//			if (showTitle) {
//				String t = view.getTitle();
//				if (t != null && t.length() > 0) {
//					title.setText(t);
//				}
//			}
//			progress.hide();
			dismissWaitingDialog();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();// 返回前一个页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
}
