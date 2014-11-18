/* 
 * @Title: SearchProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpUtils;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.data.model.CouponQueryResult;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Orderkind;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.SpaceQueryResult;

/**
* @ClassName: SearchProtocolHandler
* @Description: 搜索、检索相关协议
* @author liubingsr
* @date 2012-9-22 下午6:20:09
*
*/
public final class SearchProtocolHandler {	
	/**
	* @ClassName: SearchZMObjectProtocol
	* @Description: 搜索空间协议
	* @author liubingsr
	* @date 2012-9-22 下午6:10:00
	*
	*/
	public final static class SearchZMObjectProtocol extends ListProtocolHandlerBase<SpaceQueryResult> {
		private final static String TAG = "SearchZMObjectProtocol";
//		private String mKeyword;
		
		public SearchZMObjectProtocol(Context context, boolean refreshed, RefreshListData<SpaceQueryResult> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: search
		* @Description: 搜索空间
		* @param keyword 关键字
		* @param zmObjectType 空间类型
		* @return void
		*/
		public void search(String keyword, int zmObjectType) {
//			mKeyword = keyword;
			mSubUrl = "search/space/%s?q=%s&pageSize=%d&startIndex=%d";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObjectType), HttpUtils.urlEncode(keyword, "utf-8"), mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SEARCH_SPACE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseResult();
			return true;
		}

		/**
		* @Title: parseResult
		* @Description: 解析数据包
		* @param json
		* @return ArrayList<SpaceQueryResult>
		*/
		private ArrayList<SpaceQueryResult> parseResult() {
			ArrayList<SpaceQueryResult> resultList = new ArrayList<SpaceQueryResult>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								SpaceQueryResult result = SpaceQueryResult.parse(item);
								if (result != null) {
									resultList.add(result);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (Exception ex) {
				mDataList.setLastPage(true);
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return resultList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				if (mReceiveDataList.size() < mPageSize) {
					mDataList.setLastPage(true);
				}
			}
		}
	}
	/**
	* @ClassName: SearchBusinessPromotionProtocol
	* @Description: 搜索优惠劵协议
	* @author liubingsr
	* @date 2012-9-22 下午7:22:11
	*
	*/
	public final static class SearchBusinessPromotionProtocol extends ListProtocolHandlerBase<CouponQueryResult> {
		private final static String TAG = "SearchBusinessPromotionProtocol";
//		private String mKeyword;
		
		public SearchBusinessPromotionProtocol(Context context, boolean refreshed, RefreshListData<CouponQueryResult> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: search
		* @Description: 搜索空间
		* @param keyword 关键字
		* @return void
		*/
		public void search(String keyword) {
//			mKeyword = keyword;
			mSubUrl = "search/space/business/activity?q=%s&pageSize=%d&startIndex=%d";
			String url = mBaseUrl + String.format(mSubUrl, HttpUtils.urlEncode(keyword, "utf-8"), mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SEARCH_BUSINESS_COUPON_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseResult();
			return true;
		}

		/**
		* @Title: parseResult
		* @Description: 解析数据包
		* @param json
		* @return ArrayList<CouponQueryResult>
		*/
		private ArrayList<CouponQueryResult> parseResult() {
			ArrayList<CouponQueryResult> resultList = new ArrayList<CouponQueryResult>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								CouponQueryResult result = CouponQueryResult.parse(item);
								if (result != null) {
									resultList.add(result);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (Exception ex) {
				mDataList.setLastPage(true);
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return resultList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				if (mReceiveDataList.size() < mPageSize) {
					mDataList.setLastPage(true);
				}
			}
		}		
	}
	/**
	* @ClassName: QueryZMObjectProtocol
	* @Description: 检索空间协议
	* @author liubingsr
	* @date 2012-9-22 下午9:19:05
	*
	*/
	public final static class QueryZMObjectProtocol extends ListProtocolHandlerBase<SpaceQueryResult> {
		private final static String TAG = "QueryZMObjectProtocol";
		
		public QueryZMObjectProtocol(Context context, boolean refreshed, RefreshListData<SpaceQueryResult> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: query
		* @Description: 检索空间
		* @param spacekindId 空间子类型。< 0，忽略此参数
		* @param cityId 需检索的空间所属地区id。 < 0，忽略此参数
		* @param geo gps坐标。null，忽略此参数
		* @param order 排序方式。null，忽略此参数
		* @return void
		*/
		public void query(int zmObjectType, long spacekindId, long cityId, GeoCoordinate geo, Orderkind order,long jobKindId) {
			if (cityId <= 0) {
				cityId = 1;
			}
			String tempUrl = "query/space/%s?spaceKindId=%d&cityId=%d&pageSize=%d&startIndex=%d";//
			mSubUrl = String.format(tempUrl, ZMObjectKind.getZMObjectType(zmObjectType), spacekindId, cityId,mPageSize, mStartIndex);
			if (jobKindId > 0) {
				mSubUrl += String.format("&jobKindId=%d", jobKindId);
			}
			if (geo != null) {
				mSubUrl += String.format("&latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f",
						geo.getLatitude(), geo.getLongitude(), geo.getGdLatitude(), geo.getGdLongitude());
			}
			if (order != null) {
				mSubUrl += String.format("&orderBy=%s", order.getValue());
			}
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.QUERY_SPACE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		public void query(int zmObjectType, long spacekindId, String order) {
			String tempUrl = "query/space/%s?spaceKindId=%d&cityId=%d&pageSize=%d&startIndex=%d";//
			mSubUrl = String.format(tempUrl, ZMObjectKind.getZMObjectType(zmObjectType), spacekindId, 1, mPageSize, mStartIndex);
			if (order != null) {
				mSubUrl += String.format("&orderBy=%s", order);
			}
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.QUERY_SPACE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
//		/**
//		 *  芝麻空间_广场_检索空间
//		 */
//		public void queryPlazaSpace(int zmObjectType, long spacekindId, long cityId,long jobKindId, Orderkind order) {
//			String tempUrl = "query/space/%d";//
//			mSubUrl = String.format(tempUrl,zmObjectType);
//			if (spacekindId > 0) {
//				mSubUrl += String.format("&spaceKindId=%d", spacekindId);
//			}
//			if (cityId > 0) {
//				mSubUrl += String.format("&cityId=%d", cityId);
//			}
//			if (jobKindId > 0){
//				mSubUrl += String.format("&jobKindId=%d", jobKindId);
//			}
//			if (order != null) {
//				mSubUrl += String.format("&orderBy=%s", order.getValue());
//			}
//			String url = mBaseUrl + mSubUrl;
//			RequestInfo info = new RequestInfo(url);
//			info.setRequestType(RequestType.GET);
//			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.QUERY_ZMSPACE_PLAZA_SPACE_PROTOCOL;
//			mRequestService.sendRequest(this);
//		}
		
		public void getSpaceByHot(String spaceKind) {
			//TODO
			mSubUrl = "query/space/%s/hot";
			String url = mBaseUrl + String.format(mSubUrl, spaceKind);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.QUERY_ZMSPACE_PLAZASPACE_HOT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseResult();
			return true;
		}

		/**
		* @Title: parseResult
		* @Description: 解析数据包
		* @param json
		* @return ArrayList<SpaceQueryResult>
		*/
		private ArrayList<SpaceQueryResult> parseResult() {
			ArrayList<SpaceQueryResult> resultList = new ArrayList<SpaceQueryResult>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								SpaceQueryResult result = SpaceQueryResult.parse(item);
								if (result != null) {
									resultList.add(result);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (Exception ex) {
				mDataList.setLastPage(true);
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return resultList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				if (mReceiveDataList.size() < mPageSize) {
					mDataList.setLastPage(true);
				}
			}
		}
	}
	/**
	* @ClassName: QueryBusinessPromotionProtocol
	* @Description:检索优惠劵协议
	* @author liubingsr
	* @date 2012-9-22 下午9:27:40
	*
	*/
	public final static class QueryBusinessPromotionProtocol extends ListProtocolHandlerBase<CouponQueryResult> {
		private final static String TAG = "QueryBusinessPromotionProtocol";
		
		public QueryBusinessPromotionProtocol(Context context, boolean refreshed, RefreshListData<CouponQueryResult> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: search
		* @Description: 检索优惠劵
		* @param keyword 关键字
		* @return void
		*/
		public void query(long spacekindId, long cityId, GeoCoordinate geo, Orderkind order) {
			String tempUrl = "query/space/business/activity?pageSize=%d&startIndex=%d";
			mSubUrl = String.format(tempUrl, mPageSize, mStartIndex);
			if (spacekindId > 0) {
				mSubUrl += String.format("&spaceKindId=%d", spacekindId);
			}
			if (cityId > 0) {
				mSubUrl += String.format("&cityId=%d", cityId);
			}
			if (geo != null) {
				mSubUrl += String.format("&latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f",
						geo.getLatitude(), geo.getLongitude(), geo.getGdLatitude(), geo.getGdLongitude());
			}
			if (order != null) {
				mSubUrl += String.format("&orderBy=%s", order.getValue());
			}
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.QUERY_BUSINESS_COUPON_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseResult();
			return true;
		}

		/**
		* @Title: parseResult
		* @Description: 解析数据包
		* @param json
		* @return ArrayList<CommerceCoupon>
		*/
		private ArrayList<CouponQueryResult> parseResult() {
			ArrayList<CouponQueryResult> resultList = new ArrayList<CouponQueryResult>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								CouponQueryResult result = CouponQueryResult.parse(item);
								if (result != null) {
									resultList.add(result);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (Exception ex) {
				mDataList.setLastPage(true);
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return resultList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				if (mReceiveDataList.size() < mPageSize) {
					mDataList.setLastPage(true);
				}
			}
		}
	}
}
