package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.mobile.SystemInfo;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName IdoPhotoActivity
 * @Description
 * @author jiang
 * @date 2012-10-20 下午07:30:46
 */
public class IdoPhotoActivity extends BaseActivity {
	/** Called when the activity is first created. */
	float downXValue;
	long downTime;

	private float lastTouchX, lastTouchY;
	private boolean hasMoved = false;
	
	private final static String UA = "Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; MB525 Build/3.4.2-117) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	public static final String PHOTO_POSITION = "photo_position";
	public static final String ACTIVITY_TITLE = "title";
	public static final String TOPBAR_BACKGROUND = "topbar_bg";

	
	
	private int topbar_bg_r = 0;
	private int CurrentListIndex = -1;
	private int rightListIndex = -1;
	
	
	//---------------------------------
	protected int mPosition = 0;
	protected ArrayList<String> list;
	protected String mTitle;
	
	protected ZhimaTopbar mTopbar;
	protected ViewFlipper flipper;
	protected MyWebView myWebView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ido_photo_viewflipper);

		
		getIntentData();
		setTopbar();
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper);
		
		
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				View elseView = new View(this);
				flipper.addView(elseView);
			}
			if (mPosition < list.size()) {
				flipper.removeViewAt(mPosition);
				flipper.addView(addWebView(list.get(mPosition)), mPosition);
				flipper.setDisplayedChild(mPosition);
				CurrentListIndex = mPosition;
				rightListIndex = mPosition;
			}
		} else {
			list = new ArrayList<String>();
		}

	}
	
	public void getIntentData(){
		Intent intent = getIntent();
		list = intent.getStringArrayListExtra(ACTIVITY_EXTRA);
		mPosition = intent.getIntExtra(PHOTO_POSITION, 0);
		topbar_bg_r = intent.getIntExtra(TOPBAR_BACKGROUND, 0);
		mTitle = intent.getStringExtra(ACTIVITY_TITLE);
	}

	private View addWebView(String url) {
		myWebView = new MyWebView(this);
		if (url != null) {
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			url += "&os=" + SystemConfig.PLATFORM + "&sdk=" + SystemInfo.getSDKVersion();

			WebSettings settings = myWebView.getSettings();
			JavaScriptInterface jsInterface = new JavaScriptInterface(this);
			myWebView.addJavascriptInterface(jsInterface, "JSInterface");
			settings.setJavaScriptEnabled(true); // js
			settings.setPluginsEnabled(true); // 支持插件
			settings.setUserAgent(0); // 0为手机默认, 1为PC台机，2为IPHONE
			settings.setUserAgentString(UA);
			settings.setSupportZoom(true);// 缩放
			settings.setBuiltInZoomControls(true);// 支持手势缩放

			// 使WebView的网页跳转在WebView中进行，而非跳到浏览器
			myWebView.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url != null) {
						if (url.length() > 3) {
							if (url.substring(0, 4).equals("tel:")) {
								return false;
							}
						}
					}
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					startWaitingDialog(null, R.string.loading);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					dismissWaitingDialog();
				}
			});
			myWebView.loadUrl(url);
		}
		return myWebView;
	}

	public class JavaScriptInterface {
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
			startActivity(it);
		}
	}

	private boolean moved(MotionEvent evt) {
		return hasMoved || Math.abs(evt.getX() - lastTouchX) > 10.0 || Math.abs(evt.getY() - lastTouchY) > 10.0;
	}

	public void setTopbar() {
		mTopbar = getTopbar();
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(mTitle);
		if(topbar_bg_r != 0){
			mTopbar.setBackgroundResource(topbar_bg_r);
		}
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	class MyWebView extends WebView {

		float downXValue;
		long downTime;

		public MyWebView(Context context) {
			super(context);
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			boolean consumed = super.onTouchEvent(evt);
			if (isClickable()) {
				switch (evt.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastTouchX = evt.getX();
					lastTouchY = evt.getY();
					downXValue = evt.getX();
					downTime = evt.getEventTime();
					hasMoved = false;
					break;
				case MotionEvent.ACTION_MOVE:
					hasMoved = moved(evt);
					break;
				case MotionEvent.ACTION_UP:
					int mCurrent = flipper.getDisplayedChild();
					float currentX = evt.getX();
					long currentTime = evt.getEventTime();
					float difference = Math.abs(downXValue - currentX);
					long time = currentTime - downTime;
					Log.i("Touch Event:", "Distance: " + difference + "px Time: " + time + "ms");
					/** X轴滑动距离大于100，并且时间小于220ms,并且向X轴右方向滑动 && (time < 220) */
					if ((downXValue < currentX) && (difference > 100 && (time < 220))) {
						/** 跳到上一页 */
						flipper.setInAnimation(AnimationUtils
								.loadAnimation(IdoPhotoActivity.this, R.anim.push_right_in));
						flipper.setOutAnimation(AnimationUtils.loadAnimation(IdoPhotoActivity.this,
								R.anim.push_right_out));
						if (mCurrent == CurrentListIndex) {
							if (CurrentListIndex > 0) {
								flipper.removeViewAt(CurrentListIndex - 1);
								flipper.addView(addWebView(list.get(CurrentListIndex - 1)), CurrentListIndex - 1);
								flipper.setDisplayedChild(CurrentListIndex);
								CurrentListIndex--;
							}
						}
						if (CurrentListIndex == 0 && mCurrent == 0) {
							HaloToast.show(IdoPhotoActivity.this, "已经是第一个", 0);
						} else {
							flipper.showPrevious();
						}
					}
					/** X轴滑动距离大于100，并且时间小于220ms,并且向X轴左方向滑动 */
					if ((downXValue > currentX) && (difference > 100) && (time < 220)) {
						/** 跳到下一页 */
						flipper.setInAnimation(AnimationUtils.loadAnimation(IdoPhotoActivity.this, R.anim.push_left_in));
						flipper.setOutAnimation(AnimationUtils.loadAnimation(IdoPhotoActivity.this,
								R.anim.push_left_out));

						if (mCurrent == rightListIndex) {
							if (rightListIndex < list.size() - 1) {
								flipper.removeViewAt(rightListIndex + 1);
								flipper.addView(addWebView(list.get(rightListIndex + 1)), rightListIndex + 1);
								rightListIndex++;
							}
						}
						if (rightListIndex == list.size() - 1 && mCurrent == flipper.getChildCount() - 1) {
							HaloToast.show(IdoPhotoActivity.this, "已经是最后一个", 0);
						} else {
							flipper.showNext();
						}

					}
					break;
				}
			}
			return consumed || isClickable();
		}

	}

}