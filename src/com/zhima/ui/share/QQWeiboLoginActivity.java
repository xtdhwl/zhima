package com.zhima.ui.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * 浣跨敤Webview鏄剧ずOAuth Version 1.0 鎺堟潈鐨勯〉闈�br>
 * <p>鏈被浣跨敤鏂规硶锛�/p>
 * <li>闇�璋冪敤鏈被鐨勫湴鏂硅娣诲姞濡備笅浠ｇ爜
 * <pre>
 * //璇峰皢OAuthV1Activity鏀逛负鎵�湪绫荤殑绫诲悕
 * Intent intent = new Intent(OAuthV1Activity.this, OAuthV1AuthorizeWebView.class);   
 * intent.putExtra("oauth", oAuth);  //oAuth涓篛AuthV1绫荤殑瀹炰緥锛屽瓨鏀炬巿鏉冪浉鍏充俊鎭�
 * startActivityForResult(intent, myRrequestCode);  //璇疯缃悎閫傜殑requsetCode
 * </pre>
 * <li>閲嶅啓鎺ユ敹鍥炶皟淇℃伅鐨勬柟娉�
 * <pre>
 * if (requestCode==myRrequestCode) {  //瀵瑰簲涔嬪墠璁剧疆鐨勭殑myRequsetCode
 *     if (resultCode==OAuthV1AuthorizeWebView.RESULT_CODE) {
 *         //鍙栧緱杩斿洖鐨凮AuthV1绫诲疄渚媜Auth
 *         oAuth=(OAuthV1) data.getExtras().getSerializable("oauth");
 *     }
 * }
 * <pre>
 * @see android.app.Activity#onActivityResult(int requestCode, int resultCode,  Intent data)
 */
public class QQWeiboLoginActivity extends BaseActivity
{
    public final static int RESULT_CODE=1;
	private static final String TAG = "OAuthV1AuthorizeWebView";
	private OAuthV1 oAuth;
	private WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{ 
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_qq_login_activity);
		
		
		findView();
		setTopBar();
		
//		LinearLayout linearLayout=new LinearLayout(this);
//		WebView webView = new WebView(this);
//		linearLayout.addView(webView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//		setContentView(linearLayout);
		Intent intent = this.getIntent();
		oAuth = (OAuthV1) intent.getExtras().getSerializable("oauth");
		String urlStr = OAuthConstants.OAUTH_V1_AUTHORIZE_URL+"?oauth_token="+oAuth.getOauthToken();
		urlStr = urlStr.replace("https", "http");
		WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webView.requestFocus();
        webView.loadUrl(urlStr);
        Log.i(TAG, "WebView Starting....");
		WebViewClient client = new WebViewClient()
		{
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				dismissWaitingDialog();
				return true;
			}
			
            /**
             * 鍥炶皟鏂规硶锛屽綋椤甸潰寮�鍔犺浇鏃舵墽琛�
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(TAG, "WebView onPageStarted...");
                Log.i(TAG, "URL = " + url);
                
                if(!isDialogShowing()){
    				startWaitingDialog("", "正在加载");
    			}
                
                if (url.indexOf("checkType=verifycode") != -1) {
                	
                    int start=url.indexOf("checkType=verifycode&v=")+23;
                    String verifyCode=url.substring(start, start+6);
                    oAuth.setOauthVerifier(verifyCode);
//                    int startToken = url.indexOf("oauth_token=");
//                    int startend = url.indexOf("&mobile=");
//                    String responseData=url.substring(startToken,startend);
//                    try {
//						OAuthV1Client.parseAuthorization(responseData, oAuth);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
                    Intent intent = new Intent();
                    intent.putExtra("oauth", oAuth);
                    intent.putExtra("returnUrl", url);
                    setResult(RESULT_CODE, intent);
                    view.destroyDrawingCache();
                    view.destroy();
                    finish();
                }
                super.onPageStarted(view, url, favicon);
            }
		    
			/*
			 * TODO
			 * Android2.2鍙婁互涓婄増鏈墠鑳戒娇鐢ㄨ鏂规硶
			 * 鐩墠https://open.t.qq.com涓瓨鍦╤ttp璧勬簮浼氬紩璧穝slerror锛屽緟缃戠珯淇鍚庡彲鍘绘帀璇ユ柟娉�
			 */
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
			    if((null != view.getUrl())&&(view.getUrl().startsWith("https://open.t.qq.com"))){
			        handler.proceed();//鎺ュ彈璇佷功
			    }else{
			        handler.cancel(); //榛樿鐨勫鐞嗘柟寮忥紝WebView鍙樻垚绌虹櫧椤�
			    }
			    
			    dismissWaitingDialog();
			    
		        //handleMessage(Message msg); 鍏朵粬澶勭悊
		  }
			
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				dismissWaitingDialog();
			}

		};
		webView.setWebViewClient(client);
	}
	
	private void setTopBar() {
		ZhimaTopbar topbar = getTopbar();
		View view = View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText("腾讯微博登录");
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
		webView = (WebView) this.findViewById(R.id.wv_qq_login_webview);
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
