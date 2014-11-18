package com.zhima.base.protocol;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.location.Address;
import android.location.Location;

import com.google.gson.Gson;
import com.zhima.R;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.GoogleCell;

public class LocationProtocolHandler {

	public final static class GetAddressProtocolHandler extends ProtocolHandlerBase {
		public static final String TAG = "GetLocationProtocolHandler";

		private static Gson gson = null;
		private Address address = null;
		static {
			if (gson == null) {
				gson = new Gson();
			}
		}

		public GetAddressProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);

		}

		public Address getAddress() {
			return address;
		}

		public void requestAddress(GoogleCell cellIDInfo) {
			//http://www.google.com/glm/mmap
			String url = "http://www.google.com/loc/json";
			RequestInfo requestInfo = new RequestInfo(url);
			requestInfo.setRequestType(RequestType.POST);
			cellIDInfo.setVersion("1.1.0");
			cellIDInfo.setHost("maps.google.com");
			cellIDInfo.setRequest_address(true);
			String json = gson.toJson(cellIDInfo);
			requestInfo.setBody(json);

			this.setRequestInfo(requestInfo);
			mProtocolType = ProtocolType.LOCATION_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public void parseData() {
			parse();
		}

		@Override
		public boolean parse() {
			boolean result = false;
			mJson = mRequestInfo.getRecieveData();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					JSONTokener jsonParser = new JSONTokener(mJson);
					mResponeVO = (JSONObject) jsonParser.nextValue();
					if (!mResponeVO.isNull("location")) {
						address = new Address(Locale.CHINA);
						JSONObject locationJSON = mResponeVO.getJSONObject("location");
						if (!locationJSON.isNull("latitude")) {//latitude
							address.setLatitude(locationJSON.getDouble("latitude"));
							address.setLongitude(locationJSON.getDouble("longitude"));
						}
						if (!locationJSON.isNull("address")) {
							JSONObject addressJson = locationJSON.getJSONObject("address");
							if (!addressJson.isNull("country")) {
								//Logger.getInstance(TAG).debug("国家："+geoAddress.getCountryName());
								//Logger.getInstance(TAG).debug("省份："+geoAddress.getAdminArea());
								//Logger.getInstance(TAG).debug("市区："+geoAddress.getSubAdminArea());
								//Logger.getInstance(TAG).debug("区域："+geoAddress.getSubLocality());
								//Logger.getInstance(TAG).debug("路段："+geoAddress.getFeatureName());
								//TODO 更详细地址
								address.setCountryName(addressJson.getString("country"));
								address.setCountryCode(addressJson.getString("country_code"));
								address.setAdminArea(addressJson.getString("region"));
								address.setSubAdminArea(addressJson.getString("city"));
								if (!addressJson.isNull("street")) {
									address.setSubLocality(addressJson.getString("street"));
									address.setFeatureName(address.getSubAdminArea() + address.getSubLocality());
								} else {
									address.setSubLocality("");
									address.setFeatureName("");
								}
								result = true;
							}
						}
					}

				}
			} catch (Exception e) {
				Logger.getInstance(TAG).debug(e.toString());
			}
			return result;
		}

		@Override
		public void afterParse() {
			//解析之前
		}
	}

	/**
	* @ClassName: GeoGeocoderProtocolHandler
	* @Description: 高德GPS坐标转换为偏移坐标(通过Ios获取的GPS需要偏移转换)
	* @author luqilong
	* @date 2012-10-17 下午2:13:42
	 */
	public static final class GeoGeocoderProtocolHandler extends ProtocolHandlerBase {
		public static final String TAG = "GeoGeocoderProtocolHandler";

		private Location mLocation;
		private Context mContext;

		public GeoGeocoderProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mContext = context;
		}

		//转换的结果
		public Location getLocation() {
			return mLocation;
		}

		public void getFromRawGpsLocation(double latitude, double longitude) {
			//经纬度,map_key
			String baseUrl = "http://search1.mapabc.com/sisserver?config=RGC&resType=json&x1=%.14f&y1=%.14f&a_k=%s&flag=true";
			String url = String
					.format(baseUrl, longitude, latitude, mContext.getText(R.string.maps_api_key).toString());
			RequestInfo requestInfo = new RequestInfo(url);
			requestInfo.setRequestType(RequestType.GET);

			this.setRequestInfo(requestInfo);
			mProtocolType = ProtocolType.LOCATION_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public void parseData() {
			parse();
		}

		@Override
		public boolean parse() {
			boolean result = false;
			try {
				mJson = mRequestInfo.getRecieveData();
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					JSONTokener jsonParser = new JSONTokener(mJson);
					mResponeVO = (JSONObject) jsonParser.nextValue();
					JSONArray jsonArray = mResponeVO.getJSONArray("list");
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					mLocation = new Location("geo");
					mLocation.setLatitude(jsonObject.getDouble("y"));
					mLocation.setLongitude(jsonObject.getDouble("x"));
					result = true;
				} else {
					Logger.getInstance(TAG).debug("高德 Mapabc坐标偏转 处理错误");
				}
			} catch (Exception e) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", e);
			}
			return result;
		}

		@Override
		public void afterParse() {
			// 解析前

		}

	}
}
