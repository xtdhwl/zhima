package com.zhima.ui.map.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName: TrafficWebActivity
 * @Description: web展示交通导航
 * @author luqilong
 * @date 2012-11-30 下午4:17:52
 */
public class NavigationActivity extends BaseActivity implements LocationListener {
	private static final String TAG = NavigationActivity.class.getSimpleName();
	/** google */
	private static final String MAP_GOOGLE_RUL = "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f";
	private static final String MAP_GOOGLE_SSL_RUL = "https://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f";
	//百度地图： 起点经纬度 终点经纬度  城市
	//	private static final String MAP_BAIDU_RUL = "http://api.map.baidu.com/direction?origin=latlng:%f,%f|name:起点&destination=latlng:%f,%f|name:终点&region=%s&mode=driving&output=html";

	/** 开始坐标 */
	private GeoCoordinate mStartPoint;
	/** 结束坐标 */
	private GeoCoordinate mEndPoint;
	private WebView mTrafficeView;
	/** 商户ID */
	private long mId;
	/** 目标商户 */
	private ZMObject mZMObject;

	private String locationUrl = "";
	/** 记录加载MAP_GOOGLE_RUL 是否失败 */
	private boolean isLoadError = false;
	private boolean isGoBack = false;
	//---------------------------------
	//经纬度
	private double startLatitud;
	private double startLongitude;
	private double endLatitud;
	private double endLongitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_trafficeweb_activity);
		setTopbar();
		findView();

		Intent intent = getIntent();
		mId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = ScanningcodeService.getInstance(this).getCacheZMObject(mId);

		mStartPoint = ZMLocationManager.getInstance().getLastKnownGeoCoordinate();
		mEndPoint = mZMObject.getGeo();
		if (mStartPoint.isGpsValid() || mStartPoint.isMapabcValid()) {
			showNavigation();
		} else {
			//获取用户坐标定位onResume开启定位
			startWaitingDialog(null, R.string.loading);
		}
	}

	@Override
	protected void onStart() {
		ZMLocationManager.getInstance().startGeoListening(this);
		ZMLocationManager.getInstance().startZhimaListening(this);
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		ZMLocationManager.getInstance().stopGeoListening();
		ZMLocationManager.getInstance().stopZhimaListening();
	}

	public void showNavigation() {
		//初始化坐标：有先选择高德，高德无效选择系统坐标
		startLatitud = ZMConsts.INVALID_LONGITUDE;
		startLongitude = ZMConsts.INVALID_LONGITUDE;
		endLatitud = ZMConsts.INVALID_LONGITUDE;
		endLongitude = ZMConsts.INVALID_LONGITUDE;

		if (mStartPoint.isMapabcValid()) {
			startLatitud = mStartPoint.getGdLatitude();
			startLongitude = mStartPoint.getGdLongitude();
		} else {
			startLatitud = mStartPoint.getLatitude();
			startLongitude = mStartPoint.getLongitude();
		}

		if (mEndPoint.isMapabcValid()) {
			endLatitud = mEndPoint.getGdLatitude();
			endLongitude = mEndPoint.getGdLongitude();
		} else {
			endLatitud = mEndPoint.getLatitude();
			endLongitude = mEndPoint.getLongitude();
		}

		//定位
		locationUrl = String.format(MAP_GOOGLE_RUL, startLatitud, startLongitude, endLatitud, endLongitude);

		mTrafficeView.loadUrl(locationUrl);
		mTrafficeView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (isLoadError && url.equals(locationUrl) && isGoBack) {
					//如果加载 locationUrl失败 并且在次加载，并且是按返回健
					finish();
				} else {
					super.onPageStarted(view, url, favicon);
					startWaitingDialog(null, R.string.loading);
					isGoBack = false;
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				dismissWaitingDialog();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				dismissWaitingDialog();
				if (failingUrl.equals(locationUrl) && !isLoadError) {
					//如果是第一次加载失败只记录一次，尝试加载ssl

					isLoadError = true;
					String ssl_url = String.format(MAP_GOOGLE_SSL_RUL, startLatitud, startLongitude, endLatitud,
							endLongitude);
					mTrafficeView.loadUrl(ssl_url);
				}
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mTrafficeView.canGoBack()) {
			isGoBack = true;
			mTrafficeView.stopLoading();
			mTrafficeView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onLocationChanged(Location location) {
		//如果webview没有url.防止多次现实。加载框在web关闭这里不用处理
		dismissWaitingDialog();
		mStartPoint = ZMLocationManager.getInstance().getLastKnownGeoCoordinate();
		String url = mTrafficeView.getUrl();
		if (TextUtils.isEmpty(url)) {
			showNavigation();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	private void findView() {
		mTrafficeView = (WebView) findViewById(R.id.wbv_traffice);
		WebSettings settings = mTrafficeView.getSettings();
		settings.setJavaScriptEnabled(true); // js
		settings.setPluginsEnabled(true); // 支持插件
		settings.setSupportZoom(true);// 缩放
	}

	private void setTopbar() {
		ZhimaTopbar topBar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		topBar.addRightLayoutView(ll_right);
		topBar.addLeftLayoutView(ll_left);
		topBar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		((TextView) topBar.findViewById(R.id.txt_topbar_title)).setText("导航");
	}
}
