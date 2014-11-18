package com.zhima.base.lbs;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.mapabc.mapapi.location.LocationManagerProxy;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: ZhimaLocationListenerProxy
 * @Description: 使用系统LocationManager得到坐标
 * @author luqilong
 * @date 2012-10-5 下午6:16:15
 */
class ZhimaLocationListenerProxy implements LocationListener {

	private static final String TAG = "ZhimaLocationListenerProxy";
	private LocationManager mZhimaLocationManager;
	private LocationListener mZhimaLocationListener;
	private Context mContext;

	public ZhimaLocationListenerProxy(Context context) {
		mContext = context;
	}

	/**
	 * @Title: getProviders
	 * @Description: 返回可用的Provider
	 * @param bl
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getProviders(boolean bl) {
		ArrayList<String> valid = new ArrayList<String>();
		ArrayList<String> invalid = new ArrayList<String>();

		if (mZhimaLocationManager == null) {
			mZhimaLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		}
		for (String provider : mZhimaLocationManager.getAllProviders()) {
			if (mZhimaLocationManager.isProviderEnabled(provider)) {
				valid.add(provider);
			} else {
				invalid.add(provider);
			}
		}

		if (bl) {
			return valid;
		} else {
			return invalid;
		}
	}

	public boolean startListening(final LocationListener listener, final long updateTime, final float updateDistance) {
		boolean result = false;
		mZhimaLocationListener = listener;
		// 获取当前可用的Provider,其中MapABCNetwork为MapABC网络定位(基站和WiFi)
		if (mZhimaLocationManager == null) {
			mZhimaLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			for (final String provider : this.getProviders(true)) {
				// 以后可以添加基站定位
				if (LocationManagerProxy.GPS_PROVIDER.equals(provider)
						|| LocationManager.NETWORK_PROVIDER.equals(provider)) {
					Logger.getInstance(TAG).debug("ZhimaLocationListener startListening:::" + "provider:" + provider);
					mZhimaLocationManager.requestLocationUpdates(provider, updateTime, updateDistance, this);
					result = true;
				}
			}
		}
		return result;
	}

	public void stopListening() {
		if (mZhimaLocationManager != null) {
			mZhimaLocationManager.removeUpdates(this);
			mZhimaLocationManager = null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Logger.getInstance(TAG).debug(
				"ZhimaLocationListener -->onLocationChanged:" + "time :" + new Date(location.getTime()) + "provider:"
						+ location.getProvider() + "  point:" + location.getLatitude() + "   "
						+ location.getLongitude());

		if (mZhimaLocationListener != null) {
			mZhimaLocationListener.onLocationChanged(location);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Logger.getInstance(TAG).debug("ZhimaLocationListener onStatusChanged:::" + "provider:" + provider);

		if (mZhimaLocationListener != null) {
			mZhimaLocationListener.onStatusChanged(provider, status, extras);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Logger.getInstance(TAG).debug("ZhimaLocationListener onProviderEnabled:::" + "provider:" + provider);

		if (mZhimaLocationListener != null) {
			mZhimaLocationListener.onProviderEnabled(provider);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Logger.getInstance(TAG).debug("ZhimaLocationListener onProviderDisabled:::" + "provider:" + provider);

		if (mZhimaLocationListener != null) {
			mZhimaLocationListener.onProviderDisabled(provider);
		} else {
			throw new RuntimeException("监听为LocationService逻辑监听，不能为null");
		}
	}
}
