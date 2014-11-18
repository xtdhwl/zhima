package com.zhima.base.lbs;

import android.location.Address;
import android.location.LocationListener;

import com.zhima.app.ZhimaApplication;
import com.zhima.data.model.GeoCoordinate;

public class ZMLocationManager {
	public static final String TAG = "ZMLocationManager";
	private LocationService mLocationService = null;
	private static ZMLocationManager instance = null;
	/**获取Location失败*/
	public static final int LOCATION_ERROR = 1001;
	/**获取Location成功*/
	public static final int LOCATION_SUCCEED = 1002;
	/**获取address失败*/
	public static final int ADDRESS_ERROR = 1003;
	/**获取address成功*/
	public static final int ADDRESS_SUCCEED = 1004;

	private ZMLocationManager() {
		mLocationService = LocationService.getInstance(ZhimaApplication.getContext());
	}

	public static synchronized ZMLocationManager getInstance() {
		if (instance == null) {
			instance = new ZMLocationManager();
		}
		return instance;
	}

	//-----------------------------------------------------
	//客户端需要的功能
	//-----------------------------------------------------
	/**开启高德定位*/
	public boolean startGeoListening(LocationListener listener) {
		return mLocationService.startGeoListening(listener);
	}

	/**开启系统定位*/
	public boolean startZhimaListening(LocationListener listener) {
		return mLocationService.startZhimaListening(listener);
	}

	/**关闭高德定位*/
	public void stopGeoListening() {
		mLocationService.stopGaodeListening();
	}

	/**关闭系统定位*/
	public void stopZhimaListening() {
		mLocationService.stopZhimaListening();
	}

	/**获得最后已知的坐标*/
	public GeoCoordinate getLastKnownGeoCoordinate() {
		return mLocationService.getLastKnownGeoCoordinate();
	}

	/**获得最后已知的Address*/
	public Address getGeoLastKnownAddress() {
		return mLocationService.getGeoLaseKnowAddress();
	}

	/**获得Address:有结果address,没有结果返回空.(只回调一次)*/
	public void getGeoAddress(AddressLocationListener listener) {
		mLocationService.getGeoAddress(listener);
	}

	//	public void requestGeoCoordinate(GeoCoordLocationListener listener){
	//		 mLocationService.requestGeoCoordinate(listener);
	//	}
	//	
	//	public interface GeoCoordLocationListener{
	//		void onGeoCoordLocationListener(GeoCoordinate geoCoordinate);
	//	}
	public interface AddressLocationListener {
		void onAddressLocationListener(Address geoAddress);
	}
}
