package com.zhima.ui.common.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * @ClassName: VideoWeb
 * @Description: 封装web.解决webview打开视频问题。默认开启JavaScript
 * @author luqilong
 * @date 2013-1-19 下午8:43:10
 */
public class VideoWebView extends WebView {

	//TODO ua信息
	private final static String UA = "Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; MB525 Build/3.4.2-117) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	private Context mContext;

	public VideoWebView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public VideoWebView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
		// TODO Auto-generated constructor stub
	}

	public VideoWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated method stub
		WebSettings settings = this.getSettings();
		JavaScriptInterface jsInterface = new JavaScriptInterface(context);
		this.addJavascriptInterface(jsInterface, "JSInterface");
		settings.setJavaScriptEnabled(true); // js
		settings.setPluginsEnabled(true); // 支持插件
		settings.setUserAgent(0); // 0为手机默认, 1为PC台机，2为IPHONE
		settings.setUserAgentString(UA);
//		settings.setSupportZoom(true);// 缩放
//		settings.setBuiltInZoomControls(true);// 支持手势缩放
	}

	private class JavaScriptInterface {
		Context mContext;

		public JavaScriptInterface(Context context) {
			mContext = context;
		}

		public void startVideo(String videoAddress) {
			// 调用播放器(这里看你自己怎么写了)
			// Logger.getInstance(TAG).debug("startVideo");
			Intent it = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(videoAddress);
			it.setDataAndType(uri, "video/*");
			mContext.startActivity(it);
		}
	}
}
