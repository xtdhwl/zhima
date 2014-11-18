package com.zhima.ui.share.sina;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.util.Utility;
import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SinaWeiboShare;

public class WeiboLoginActivity extends BaseActivity {

//	public static final int WeiboLoginCode = 0;
	private WeiboAuthListener mListener;
//	private ProgressDialog mSpinner;
	private WebView mWebView;
	private RelativeLayout webViewContainer;
//	private RelativeLayout mContent;

	private final static String TAG = "WeiboLoginActivity";
	
	private static int theme=android.R.style.Theme_Translucent_NoTitleBar;
	private  static int left_margin=0;
    private  static int top_margin=0;
    private  static int right_margin=0;
    private  static int bottom_margin=0;
    
	private String mUrl;
    
    public static String app_key = "";//第三方应用的appkey
	public static String redirecturl = "";// 重定向url
	
	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESHTOKEN = "refresh_token";
	public static boolean isWifi=false;//当前是否为wifi
	
	public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";
	
	private String mReturnUrl = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_sina_login_activity);
		
		setTopbar();
		findView();
		initData();
		initView();
	}

	private void setTopbar() {
//		
//		VCardResultParser vCardResultParser = new VCardResultParser();
//		AddressBookParsedResult parse = vCardResultParser.parse(null);
//		parse.maybeAppend(value, result)
//		parse.get
		
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("新浪微博登录");
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
		mWebView = (WebView) this.findViewById(R.id.wv_sina_login_webview);
	}

	private void initView() {
//		mSpinner = new ProgressDialog(this);
//		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		mSpinner.setMessage("Loading...");
//		mSpinner.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				onBack();
//				return false;
//			}
//
//		});
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);  
//		mContent = new RelativeLayout(this);
		setUpWebView();

//		addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT));
	}

	private void initData() {
		
		isWifi=Utility.isWifi(this);
		Intent intent = getIntent();
		app_key = intent.getStringExtra("app_key");
		redirecturl = intent.getStringExtra("redirecturl");
		
		startAuthDialog(this, SinaWeiboShare.getInstance(this, this).getWeiboAuthListener());
	}
	
//	protected void onBack() {
//		try {
//			mSpinner.dismiss();
//			if (null != mWebView) {
//				mWebView.stopLoading();
//				mWebView.destroy();
//			}
//		} catch (Exception e) {
//		}
//	}
	
	private void setUpWebView() {
		webViewContainer = new RelativeLayout(this);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WeiboWebViewClient());
		mWebView.loadUrl(mUrl);
		mWebView.setVisibility(View.INVISIBLE);
		
//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT);
//		
//		RelativeLayout.LayoutParams lp0 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
//                LayoutParams.FILL_PARENT);
		
//        mContent.setBackgroundColor(Color.TRANSPARENT);
//        AssetManager asseets = getAssets();
//        InputStream is=null;
//        try {
//             try {
//               is=asseets.open("weibosdk_dialog_bg.9.png");
//               DisplayMetrics dm = getResources()
//                       .getDisplayMetrics();
//               float density=dm.density;
//               lp0.leftMargin =(int) (10*density);
//               lp0.topMargin = (int) (10*density);
//               lp0.rightMargin =(int) (10*density);
//               lp0.bottomMargin = (int) (10*density);
//           } catch (Exception e) {
//               e.printStackTrace();
//           }
//             if(is==null){
////                     webViewContainer.setBackgroundResource(R.drawable.weibosdk_dialog_bg);
//             }
//             else{
//                   Bitmap bitmap = BitmapFactory.decodeStream(is);
//                   NinePatchDrawable npd=new NinePatchDrawable(bitmap, bitmap.getNinePatchChunk(), new Rect(0,0,0,0), null); 
//                   webViewContainer.setBackgroundDrawable(npd);
//             }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            if(is!=null){
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
       
		
//        webViewContainer.addView(mWebView,lp0);
//		webViewContainer.setGravity(Gravity.CENTER);
		
//		if(parseDimens()){
//		    lp.leftMargin = left_margin;
//	        lp.topMargin = top_margin;
//	        lp.rightMargin =right_margin;
//	        lp.bottomMargin = bottom_margin;
//		}
//		else{
//		    Resources resources = getResources();
//		    lp.leftMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_left_margin);
//		    lp.rightMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_right_margin);
//		    lp.topMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_top_margin);
//		    lp.bottomMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_bottom_margin);
//		}
//        mContent.addView(webViewContainer, lp);
	}
	
	public void startAuthDialog(Context context, final WeiboAuthListener listener) {
		WeiboParameters params = new WeiboParameters();
//		CookieSyncManager.createInstance(context);
		mListener = new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values,String returnUrl) {
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				if (null == SinaWeiboShare.accessToken) {
					SinaWeiboShare.accessToken = new Oauth2AccessToken();
				}
				SinaWeiboShare.accessToken.setToken(values.getString(KEY_TOKEN));
				SinaWeiboShare.accessToken.setExpiresIn(values.getString(KEY_EXPIRES));
				SinaWeiboShare.accessToken.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
				if (SinaWeiboShare.accessToken.isSessionValid()) {
					Log.d("Weibo-authorize",
							"Login Success! access_token=" + SinaWeiboShare.accessToken.getToken() + " expires="
									+ SinaWeiboShare.accessToken.getExpiresTime() + " refresh_token="
									+ SinaWeiboShare.accessToken.getRefreshToken());
					listener.onComplete(values,returnUrl);
					
//					Intent intent = new Intent();
//					intent.putExtra("returnUrl", mReturnUrl);
//					setResult(WeiboLoginCode, intent);
//					SinaWeiboShare.setReturnUrl(mReturnUrl);
					finish();
				} else {
					Log.d("Weibo-authorize", "Failed to receive access token");
					listener.onWeiboException(new WeiboException("Failed to receive access token."));
				}
			}

			@Override
			public void onError(WeiboDialogError error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onError(error);
			}

			@Override
			public void onWeiboException(WeiboException error) {
				Log.d("Weibo-authorize", "Login failed: " + error);
				listener.onWeiboException(error);
			}

			@Override
			public void onCancel() {
				Log.d("Weibo-authorize", "Login canceled");
				listener.onCancel();
			}
		};
		startDialog(context, params, mListener );
	}

	public void startDialog(Context context, WeiboParameters parameters,
			final WeiboAuthListener listener) {
		parameters.add("client_id", app_key);
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", redirecturl);
		parameters.add("display", "mobile");
		parameters.add("forcelogin", "true");

		if (SinaWeiboShare.accessToken != null && SinaWeiboShare.accessToken.isSessionValid()) {
			parameters.add(KEY_TOKEN, SinaWeiboShare.accessToken.getToken());
		}
		mUrl = URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error",
					"Application requires permission to access the Internet");
		} /*else {
			new WeiboDialog(context, mUrl, listener).show();
		}*/
	}
	
	private void handleRedirectUrl(WebView view, String url) {
		mReturnUrl = url;
		
		Bundle values = Utility.parseUrl(url);
		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			mListener.onComplete(values,url);
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			mListener.onCancel();
		} else {
			if(error_code==null){
				mListener.onWeiboException(new WeiboException(error, 0));
			}
			else{
				mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
			}
			
		}
	}
	
//	private boolean parseDimens(){
//	    boolean ret=false;
//        AssetManager asseets = getAssets();
//        DisplayMetrics dm = getResources()
//                .getDisplayMetrics();
//        float density=dm.density;
//        InputStream is=null;
//        try {
//            is=asseets.open("values/dimens.xml");
//            XmlPullParser xmlpull = Xml.newPullParser();  
//            try {
//                xmlpull.setInput(is,"utf-8");
//                int eventCode = xmlpull.getEventType();  
//                ret=true;
//                while(eventCode!=XmlPullParser.END_DOCUMENT)  {
//                    switch (eventCode)  
//                    {  
//                    case XmlPullParser.START_TAG:
//                        if(xmlpull.getName().equals("dimen")){
//                            String name=xmlpull.getAttributeValue(null, "name");
//                            if("weibosdk_dialog_left_margin".equals(name)){
//                                    String value=xmlpull.nextText();
//                                    left_margin=(int)(Integer.parseInt(value)*density);
//                            }
//                            else if("weibosdk_dialog_top_margin".equals(name)){
//                                String value=xmlpull.nextText();
//                                top_margin=(int)(Integer.parseInt(value)*density);
//                            }
//                            else if("weibosdk_dialog_right_margin".equals(name)){
//                                String value=xmlpull.nextText();
//                                right_margin=(int)(Integer.parseInt(value)*density);
//                            }
//                            else if("weibosdk_dialog_bottom_margin".equals(name)){
//                                String value=xmlpull.nextText();
//                                bottom_margin=(int)(Integer.parseInt(value)*density);
//                            }
//                        }
//                        break;
//                    }
//                    eventCode = xmlpull.next();//没有结束xml文件就推到下个进行解析  
//                }
//                
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            }
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally{
//            if(is!=null){
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return ret;
//    }
	
	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			 if (url.startsWith("sms:")) {  //针对webview里的短信注册流程，需要在此单独处理sms协议
	                Intent sendIntent = new Intent(Intent.ACTION_VIEW);  
	                sendIntent.putExtra("address", url.replace("sms:", ""));  
	                sendIntent.setType("vnd.android-dir/mms-sms");  
	                WeiboLoginActivity.this.startActivity(sendIntent);  
	                return true;  
	            }  
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new WeiboDialogError(description, errorCode, failingUrl));
			//TODO
			dismissWaitingDialog();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "onPageStarted URL: " + url);
			if (url.startsWith(redirecturl)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				//TODO WeiboDialog.this.dismiss();
				return;
			}
			super.onPageStarted(view, url, favicon);
//			mSpinner.show();
			startWaitingDialog("", "正在加载...");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
//			if (mSpinner.isShowing()) {
//				mSpinner.dismiss();
//			}
			dismissWaitingDialog();
			mWebView.setVisibility(View.VISIBLE);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
			dismissWaitingDialog();
		}

	}
	
//	class AuthDialogListener implements WeiboAuthListener {
//
//		@Override
//		public void onComplete(Bundle values) {
//			String token = values.getString("access_token");
//			String expires_in = values.getString("expires_in");
//			SinaWeiboShare.accessToken = new Oauth2AccessToken(token, expires_in);
////			SinaWeiboShare.mAccessToken = SinaWeiboShare.accessToken.getToken();
//			if (accessToken.isSessionValid()) {
//				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(accessToken.getExpiresTime()));
//				AccessTokenKeeper.keepAccessToken(mContext, accessToken);
//				if(isHaveSend){
//					sendWeibo(mContent, mPicPath);
//					isHaveSend = false;
//				}
//				if(mSinaLoginListener!=null){
//					mSinaLoginListener.login(true);
//				}
//			}
//		}
//
//		@Override
//		public void onError(WeiboDialogError e) {
//			if(mSinaLoginListener!=null){
//				mSinaLoginListener.login(false);
//			}
//			HaloToast.show(mContext, "新浪微博绑定失败");
//			Logger.getInstance(TAG).debug("Auth error : " + e.getMessage());
//		}
//
//		@Override
//		public void onCancel() {
//			if(mSinaLoginListener!=null){
//				mSinaLoginListener.login(false);
//			}
//			HaloToast.show(mContext, "新浪微博绑定失败");
//			Logger.getInstance(TAG).debug("Auth cancel");
//		}
//
//		@Override
//		public void onWeiboException(WeiboException e) {
//			if(mSinaLoginListener!=null){
//				mSinaLoginListener.login(false);
//			}
//			HaloToast.show(mContext, "新浪微博绑定失败");
//			Logger.getInstance(TAG).debug("Auth exception : " + e.getMessage());
//		}
//	}
	
//	class AuthDialogListener implements WeiboAuthListener {
//
//		@Override
//		public void onComplete(Bundle values) {
//			String token = values.getString("access_token");
//			String expires_in = values.getString("expires_in");
//			SinaWeiboShare.accessToken = new Oauth2AccessToken(token, expires_in);
//			if (SinaWeiboShare.getInstance(WeiboLoginActivity.this, WeiboLoginActivity.this).isLogin()) {
//				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(SinaWeiboShare.accessToken.getExpiresTime()));
//				try {
//	                Class sso=Class.forName("com.weibo.sdk.android.api.WeiboAPI");//如果支持weiboapi的话，显示api功能演示入口按钮
//	            } catch (ClassNotFoundException e) {
////	                e.printStackTrace();
//	                Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");
//	               
//	            }
//				AccessTokenKeeper.keepAccessToken(WeiboLoginActivity.this, SinaWeiboShare.accessToken);
//				Toast.makeText(WeiboLoginActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
//				finish();
//			}
//		}
//
//		@Override
//		public void onError(WeiboDialogError e) {
//			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onCancel() {
//			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
//		}
//
//		@Override
//		public void onWeiboException(WeiboException e) {
//			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(),
//					Toast.LENGTH_LONG).show();
//		}
//	}
	
}
