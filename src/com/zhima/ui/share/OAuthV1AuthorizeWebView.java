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
 * 使用Webview显示OAuth Version 1.0 授权的页面<br>
 * <p>本类使用方法：</p>
 * <li>需要调用本类的地方请添加如下代码
 * <pre>
 * //请将OAuthV1Activity改为所在类的类名
 * Intent intent = new Intent(OAuthV1Activity.this, OAuthV1AuthorizeWebView.class);   
 * intent.putExtra("oauth", oAuth);  //oAuth为OAuthV1类的实例，存放授权相关信息
 * startActivityForResult(intent, myRrequestCode);  //请设置合适的requsetCode
 * </pre>
 * <li>重写接收回调信息的方法
 * <pre>
 * if (requestCode==myRrequestCode) {  //对应之前设置的的myRequsetCode
 *     if (resultCode==OAuthV1AuthorizeWebView.RESULT_CODE) {
 *         //取得返回的OAuthV1类实例oAuth
 *         oAuth=(OAuthV1) data.getExtras().getSerializable("oauth");
 *     }
 * }
 * <pre>
 * @see android.app.Activity#onActivityResult(int requestCode, int resultCode,  Intent data)
 */
/**
 * @ClassName: OAuthV1AuthorizeWebView
 * @Description: TODO
 * @author yusonglin
 * @date 2012-12-26 下午4:35:11
*/
public class OAuthV1AuthorizeWebView extends BaseActivity
{
    public final static int RESULT_CODE=1;
	private static final String TAG = "OAuthV1AuthorizeWebView";
	private OAuthV1 oAuth;
//	private ProgressDialog dialog;
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
             * 回调方法，当页面开始加载时执行
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
                    Intent intent = new Intent();
                    intent.putExtra("oauth", oAuth);
                    setResult(RESULT_CODE, intent);
                    view.destroyDrawingCache();
                    view.destroy();
                    finish();
                    dismissWaitingDialog();
                }
                super.onPageStarted(view, url, favicon);
            }
            
            
		    
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				dismissWaitingDialog();
			}

			/*
			 * TODO
			 * Android2.2及以上版本才能使用该方法
			 * 目前https://open.t.qq.com中存在http资源会引起sslerror，待网站修正后可去掉该方法
			 */
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
			    if((null != view.getUrl())&&(view.getUrl().startsWith("https://open.t.qq.com"))){
			        handler.proceed();//接受证书
			    }else{
			        handler.cancel(); //默认的处理方式，WebView变成空白页
			    }
			    dismissWaitingDialog();
		        //handleMessage(Message msg); 其他处理
		  }

		};
		webView.setWebViewClient(client);
//		showDialog();
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
				QQShareActivity.bind_from_space = 100;
				finish();
			}
		});
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}

	private void findView() {
		webView = (WebView) this.findViewById(R.id.wv_qq_login_webview);
	}

//	private void showDialog(){
//		if(dialog == null){
//			dialog = new ProgressDialog(this);
//			dialog.setMessage("请稍等");
//			dialog.show();
//		}else{
//			dialog.show();
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(webView.canGoBack()){
				webView.goBack();// 返回前一个页面
			}
			QQShareActivity.bind_from_space = 100;
			
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
}
