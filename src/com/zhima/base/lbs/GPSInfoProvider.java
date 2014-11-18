package com.zhima.base.lbs;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @ClassName: GPSInfoProvider
 * @Description: 提供获取当前位置的经纬度
 * @author yusonglin
 * @date 2012-7-12
 */
public class GPSInfoProvider {

	private Context mContext;
	private TextView mTextView;

	private GPSInfoProvider() {
	};

	public GPSInfoProvider(Context context, TextView textView) {
		this.mContext = context;
		this.mTextView = textView;
	}

	/**
	 * 通过系统的GPS或网络获取经纬度
	 * 
	 * @return 获取到的经纬度
	 */
	public String getLocation() {
		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		// 获取系统位置管理服务
		locationManager = (LocationManager) mContext.getSystemService(serviceName);

		// 构建位置查询条件对象
		Criteria criteria = new Criteria();
		// 获取准确的位置
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 对海拔信息不敏感
		criteria.setAltitudeRequired(false);
		// 是否查询方位角 : 否
		criteria.setBearingRequired(false);
		// 允许产生开销
		criteria.setCostAllowed(true);
		// 电量要求：低
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
		// provider
		String provider = locationManager.getBestProvider(criteria, true);

		if (provider != null) {
			Location location = locationManager.getLastKnownLocation(provider);
			String newLocation = updateWithNewLocation(location);
			locationManager.requestLocationUpdates(provider, 2000, 10, mLocationListener);
			return newLocation;
		} else {
			return "无法获取地理信息";
		}
	}
	/**
	 * Gps 消息监听器
	 */
	private final LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * 根据Location对象获取到当前位置的经纬度
	 * 
	 * @param location
	 *            当前位置的location对象
	 * @return 当前位置的经纬度
	 */
	public String updateWithNewLocation(Location location) {

		String latLongString;// 经纬度字符串
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "纬度:" + lat + "\n经度:" + lng;
		} else {
			latLongString = "无法获取地理信息";
		}
		mTextView.setText(latLongString);
		return latLongString;
	}
}
