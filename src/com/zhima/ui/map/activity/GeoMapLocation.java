package com.zhima.ui.map.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.PoiItem;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.PoiOverlay;
import com.zhima.R;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.CommonViewUtils;
import com.zhima.ui.common.view.CustomLoadDialog;
import com.zhima.ui.common.view.ZhimaTopbar;

public class GeoMapLocation extends MapActivity {
	/** 目标定位失败 */
	private static final int MSG_TARGET_ERROR = 402;
	/** 目标搜索成功 */
	private static final int MSG_TARGET_SUCCEED = 200;

	public static final String ACTIVITY_LOCATION = "location";

	private MapView mMapView;
	private MapController mMapController;
	
	/**目标对象*/
	private ZMObject mZMObject;
	/**目标对象坐标*/
	private GeoPoint mTargetGeoPoint = null;
	/**目标对象的POI内容*/
	private String mTargetAddress;
	//由于不是继承与BaseActivity所以需要对话框
	private CustomLoadDialog mWaitDlg;

	private ZhimaTopbar mTopbar;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dismissWaitingDialog();
			switch (msg.what) {
			case MSG_TARGET_ERROR:
				//如果发生错误则不显示导航
				ErrorManager.showErrorMessage(getApplicationContext());
				mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.INVISIBLE);
				break;
			case MSG_TARGET_SUCCEED:
				mMapController.setCenter(mTargetGeoPoint); //设置地图中心点
				drawPoint();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImeHelper.setNoTitleAndInput(this);

		setContentView(R.layout.map_activity);
		findView();
		setTopbar();

		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();

		//ZMObject id 和 位置
		Intent intent = getIntent();
		//目标位置
		long id = intent.getLongExtra(BaseActivity.ACTIVITY_EXTRA, -1);
		mZMObject = ScanningcodeService.getInstance(this).getCacheZMObject(id);

		mTargetAddress = intent.getStringExtra(ACTIVITY_LOCATION);
		GeoCoordinate geo = mZMObject.getGeo();

		startWaitingDialog(null, getText(R.string.loading).toString());
		if (geo.isMapabcValid()) {
			mTargetGeoPoint = new GeoPoint((int) (geo.getGdLatitude() * 1E6), (int) (geo.getGdLongitude() * 1E6));
			mHandler.sendEmptyMessage(MSG_TARGET_SUCCEED);
		} else {
			if (geo.isGpsValid()) {
				//Mapabc坐标偏移转换
				mTargetGeoPoint = new GeoPoint((int) (geo.getLatitude() * 1E6), (int) (geo.getLongitude() * 1E6));
				mHandler.sendEmptyMessage(MSG_TARGET_SUCCEED);
				//				getGeoGeoCoder(geo.getLatitude(), geo.getLongitude());
			} else {
				//如果高德地图无效并系统坐标无效则标题栏不显示导航按钮
				mHandler.sendEmptyMessage(MSG_TARGET_ERROR);
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		ZMLocationManager.getInstance().startGeoListening(null);
		ZMLocationManager.getInstance().startZhimaListening(null);
	}

	//关闭定位
	@Override
	protected void onStop() {
		super.onStop();
		ZMLocationManager.getInstance().stopGeoListening();
		ZMLocationManager.getInstance().stopZhimaListening();

	}

	private void drawPoint() {
		PoiItem poiItem = new PoiItem(mZMObject.getZMID(), mTargetGeoPoint, mZMObject.getName(), mTargetAddress);
		ArrayList<PoiItem> poiItems = new ArrayList<PoiItem>();
		poiItems.add(poiItem);
		Drawable drawable = this.getResources().getDrawable(R.drawable.da_marker_red);
		PoiOverlay poiOverlay = new PoiOverlay(drawable, poiItems);
		poiOverlay.addToMap(mMapView);
		goTargetPoint(true);
	}

	public void startWaitingDialog(String title, String text) {
		mWaitDlg = CommonViewUtils.getWaitDlg(this, title, text);
		mWaitDlg.show();
	}

	public void dismissWaitingDialog() {
		if (mWaitDlg != null && mWaitDlg.isShowing()) {
			mWaitDlg.dismiss();
		}
	}

	private void goTargetPoint(boolean zoom) {
		if (mTargetGeoPoint != null) {
			mMapController.animateTo(mTargetGeoPoint);
			if (zoom) {
				mMapController.setZoom(17);
			}
		}
	}

	/** 导航 */
	private View.OnClickListener trafficTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent it = new Intent(GeoMapLocation.this, NavigationActivity.class);
			it.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMObject.getId());
			startActivity(it);
		}
	};
	

	private void findView() {
		mMapView = (MapView) findViewById(R.id.mapView);
	}

	private void setTopbar() {
		 mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.locate);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(trafficTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.map);
	}

}
