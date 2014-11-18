package com.zhima.base.lbs;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mapabc.mapapi.geocoder.Geocoder;
import com.zhima.R;
import com.zhima.base.lbs.ZMLocationManager.AddressLocationListener;
import com.zhima.base.logger.Logger;
import com.zhima.data.model.GeoCoordinate;

public class LocationService {
	private static final String TAG = "LocationService";
	private static final int MSG_ADDRESS = 201;
	//定时器消息
	private final static String MSG_GAODE_LOCATION = "msg_gaode_location";
	private final static String MSG_ZHIMA_LOCATION = "msg_zhima_location";

	private Context mContext;

	//TODO 时间和距离设置
	private long mLocationUpdateMinTime = 120000L;
	private float mLocationUndateMinDistance = 50F;
	//1-5
	private int MAX_RESULTS = 1;

	private static LocationService instance;

	//LocationService 监听
	private GaodeLocationListener mGaodeLocationListener;
	private ZhimaLocationListener mZhimaLocationListener;
	//	private GoogleAddressLocationListener mGoogleAddressLocationListener;

	private GaodeLocationListenerProxy mGaodeLocationListenerProxy;
	private ZhimaLocationListenerProxy mZhimaLocationListenerProxy;

	//客户端回调
	private LocationListener mGaodeLocationCallback;
	private LocationListener mZhimaLocationCallback;
	private AddressLocationListener mGeodeAddressLocationCallback; // 逆地理编码

	// 逆地理编码
	private Geocoder mGeocoder;

	private GeoCoordinate mGeoCoordinate;
	private Address mOdlAddress;

	private Handler mAddressHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ADDRESS:
				Address address = (Address) msg.obj;
				mOdlAddress = address;
				if (mGeodeAddressLocationCallback != null) {
					mGeodeAddressLocationCallback.onAddressLocationListener(address);
					mGeodeAddressLocationCallback = null;
				}
				break;
			}
		}
	};

	private LocationService(Context context) {
		mContext = context;

		mGaodeLocationListener = new GaodeLocationListener();
		mZhimaLocationListener = new ZhimaLocationListener();

		mZhimaLocationListenerProxy = new ZhimaLocationListenerProxy(mContext);
		mGaodeLocationListenerProxy = new GaodeLocationListenerProxy(mContext);

//		mOdlAddress = new Address(Locale.getDefault());
		mGeoCoordinate = new GeoCoordinate();
		mGeocoder = new Geocoder(context, context.getText(R.string.maps_api_key).toString());
	}

	public static synchronized LocationService getInstance(Context context) {
		if (instance == null) {
			instance = new LocationService(context);
		}
		return instance;
	}

	/**
	 * 启动高德定位
	 */
	public boolean startGeoListening(LocationListener listener) {
		mGaodeLocationCallback = listener;
		return mGaodeLocationListenerProxy.startListening(mGaodeLocationListener, mLocationUpdateMinTime,
				mLocationUndateMinDistance);
	}

	/**
	 * 启动系统定位
	 */
	public boolean startZhimaListening(LocationListener listener) {
		mZhimaLocationCallback = listener;
		return mZhimaLocationListenerProxy.startListening(mZhimaLocationListener, mLocationUpdateMinTime,
				mLocationUndateMinDistance);
	}

	/**
	 * 停止高德定位
	 */
	public void stopGaodeListening() {
		//这里顺序不能颠倒：先把逆编码关闭，在把高德定位关闭，高德定位会回调mGaodeLocationCallback。回调完毕在把mGaodeLocationCallback指空
		mGeodeAddressLocationCallback = null;
		mGaodeLocationListenerProxy.stopListening();
		mGaodeLocationCallback = null;
	}

	/**
	 * 停止系统定位
	 */
	public void stopZhimaListening() {
		mZhimaLocationListenerProxy.stopListening();
		mZhimaLocationCallback = null;
	}

	/**
	 * 获得最后已知的GeoCoordinate坐标
	 */
	public GeoCoordinate getLastKnownGeoCoordinate() {
		return mGeoCoordinate;
	}

	/**
	 * 获得最后已知的Address坐标
	 */
	public Address getGeoLaseKnowAddress() {
		return mOdlAddress;
	}

	/**
	 * 获得Address坐标
	 */
	public void getGeoAddress(AddressLocationListener listener) {
		mGeodeAddressLocationCallback = listener;
		startGeoListening(mGaodeLocationCallback);
	}

	//------------------------------------------------------------
	// 高德 和 系统回调监听
	//------------------------------------------------------------

	private class GaodeLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(final Location location) {
			//如果逆地址监听不为空获取逆地址
			if (mGeodeAddressLocationCallback != null && location != null) {
				Logger.getInstance(TAG).debug("GaodeLocationListener AddressTask().execute(location)");
				//这里可能会有多次回调，但我们已经在mAddressHandler中把mAddressLocationCallback指null
				new AddressTask().execute(location);
			}
			if (mGaodeLocationCallback != null) {
				mGaodeLocationCallback.onLocationChanged(location);
			}
			mGeoCoordinate.setGdLatitude(location.getLatitude());
			mGeoCoordinate.setGdLongitude(location.getLongitude());
			mGeoCoordinate.setGdTime(System.currentTimeMillis());
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (mGaodeLocationCallback != null) {
				mGaodeLocationCallback.onStatusChanged(provider, status, extras);
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			if (mGaodeLocationCallback != null) {
				mGaodeLocationCallback.onProviderEnabled(provider);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if (mGaodeLocationCallback != null) {
				mGaodeLocationCallback.onProviderDisabled(provider);
			}
		}
	}

	//系统
	private class ZhimaLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (mZhimaLocationCallback != null) {
				mZhimaLocationCallback.onLocationChanged(location);
			}
			mGeoCoordinate.setLatitude(location.getLatitude());
			mGeoCoordinate.setLongitude(location.getLongitude());
			mGeoCoordinate.setTime(System.currentTimeMillis());

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (mZhimaLocationCallback != null) {
				mZhimaLocationCallback.onStatusChanged(provider, status, extras);
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			if (mZhimaLocationCallback != null) {
				mZhimaLocationCallback.onProviderEnabled(provider);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if (mZhimaLocationCallback != null) {
				mZhimaLocationCallback.onProviderDisabled(provider);
			}
		}
	}

	//	//谷歌基站
	//	private class GoogleAddressLocationListener implements AddressLocationListener {
	//		@Override
	//		public void onAddressLocationListener(Address googleAddress) {
	//			if (googleAddress != null) {
	//				mAddressHandler.obtainMessage(MSG_ADDRESS, googleAddress).sendToTarget();
	//
	//				mOdlAddress = googleAddress;
	//				//这里只修改系统经纬度，不能修改高德经纬度
	//				mGeoCoordinate.setLatitude(googleAddress.getLatitude());
	//				mGeoCoordinate.setLongitude(googleAddress.getLongitude());
	//				mGeoCoordinate.setTime(System.currentTimeMillis());
	//			}
	//		}
	//	}

	//转码
	private class AddressTask extends AsyncTask<Location, Void, Address> {

		@Override
		protected Address doInBackground(Location... params) {
			Location location = params[0];
			Address address = null;
			try {
				List<Address> addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(),
						MAX_RESULTS);
				if (addresses != null && addresses.size() > 0) {
					address = addresses.get(0);
				}
			} catch (Exception e) {
				Logger.getInstance(TAG).debug(e.getMessage());
			}
			return address;
		}

		@Override
		protected void onPostExecute(Address result) {
			Logger.getInstance(TAG).debug("AddressTask  onPostExecute");
			mAddressHandler.obtainMessage(MSG_ADDRESS, result).sendToTarget();
		}
	}
}
