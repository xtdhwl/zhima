package com.zhima.base.lbs;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.mapabc.mapapi.location.LocationManagerProxy;
import com.mapabc.mapapi.location.LocationProviderProxy;
import com.zhima.R;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: GeoLocationListenerProxy
 * @Description: 高德API获取坐标
 * @author luqilong
 * @date 2012-10-5 下午6:17:22
 */
class GaodeLocationListenerProxy implements LocationListener {

	private static final String TAG = GaodeLocationListenerProxy.class.getSimpleName();

	private Context mContext;
	private LocationManagerProxy mLocationManager;
	private LocationListener mGaodeLocationListener = null;

	private String key = "";

	public GaodeLocationListenerProxy(Context context) {
		mContext = context;
		key = mContext.getText(R.string.maps_api_key).toString();

	}

	public boolean startListening(final LocationListener listener, final long pUpdateTime, final float pUpdateDistance) {
		boolean result = false;
		mGaodeLocationListener = listener;
		//获取当前可用的Provider，其中MapABCNetwork为MapABC网络定位（基站和WiFi）
		if (mLocationManager == null) {
			mLocationManager = LocationManagerProxy.getInstance(mContext, key);
			for (final String provider : mLocationManager.getProviders(true)) {
				if (LocationManager.GPS_PROVIDER.equals(provider)
						|| LocationProviderProxy.MapABCNetwork.equals(provider)) {
					//	if (LocationManagerProxy.NETWORK_PROVIDER.equals(provider)) {
					result = true;
					mLocationManager.requestLocationUpdates(provider, pUpdateTime, pUpdateDistance, this);
					Logger.getInstance(TAG).debug("startListening");
				}
			}
		}
		return result;
	}

	public void stopListening() {
		if (mLocationManager != null) {
			mLocationManager.removeUpdates(this);
			mLocationManager.destory();
			mLocationManager = null;
		}
		Logger.getInstance(TAG).debug("stopListening");
	}

	public void onLocationChanged(final Location location) {
		if (location != null) {
			Logger.getInstance(TAG).debug(
					"onLocationChanged:" + "time :" + new Date(location.getTime()) + "provider:"
							+ location.getProvider() + "  point:" + location.getLatitude() + "   "
							+ location.getLongitude());
		}

		if (mGaodeLocationListener != null) {
			mGaodeLocationListener.onLocationChanged(location);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Logger.getInstance(TAG).debug("onProviderDisabled:::" + "provider:" + provider);

		if (mGaodeLocationListener != null) {
			mGaodeLocationListener.onProviderDisabled(provider);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Logger.getInstance(TAG).debug("onProviderEnabled:::" + "provider:" + provider);

		if (mGaodeLocationListener != null) {
			mGaodeLocationListener.onProviderEnabled(provider);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Logger.getInstance(TAG).debug("onStatusChanged:::" + "provider:" + provider);

		if (mGaodeLocationListener != null) {
			mGaodeLocationListener.onStatusChanged(provider, status, extras);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

}
