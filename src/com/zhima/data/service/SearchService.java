/* 
 * @Title: SearchService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import android.content.Context;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.SearchProtocolHandler.QueryBusinessPromotionProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.QueryZMObjectProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.SearchBusinessPromotionProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.SearchZMObjectProtocol;
import com.zhima.data.model.CouponQueryResult;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Orderkind;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.SpaceQueryResult;
import com.zhima.data.model.ZMSpaceKind;

/**
* @ClassName: SearchService
* @Description: 搜索、检索
* @author liubingsr
* @date 2012-9-22 下午6:14:22
*
*/
public class SearchService extends BaseService {
	private final static String TAG = "SearchService";
	private static SearchService mInstance = null;
	
	private RefreshListData<SpaceQueryResult> mZMObjectResultList;
	private RefreshListData<SpaceQueryResult> mZMObjectNewResultList;
	private RefreshListData<SpaceQueryResult> mZMObjectHotResultList;
	private RefreshListData<CouponQueryResult> mCouponResultList;
	
	private SearchService(Context context) {
		super(context);		
		mZMObjectResultList = new RefreshListData<SpaceQueryResult>();
		mZMObjectNewResultList = new RefreshListData<SpaceQueryResult>();
		mZMObjectHotResultList = new RefreshListData<SpaceQueryResult>();
		mCouponResultList = new RefreshListData<CouponQueryResult>();
	}

	public static SearchService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SearchService(context);
		}
//		mInstance.mContext = context;
		return mInstance;
	}	
	/**
	* @Title: searchZMObject
	* @Description: 搜索空间
	* @param keyword 搜索关键字
	* @param zmObjectType 空间类型(目前只支持:BUSINESS_OBJECT、PUBLICPLACE_OBJECT、VEHICLE_OBJECT)
	* @see com.zhima.base.consts.ZMConsts.ZMObjectKind
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void searchZMObject(String keyword, long zmObjectType, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mZMObjectResultList.clear();
//		}
		SearchZMObjectProtocol protocol = new SearchZMObjectProtocol(mContext, refreshed, mZMObjectResultList, callBack);
		protocol.search(keyword, (int)zmObjectType);
	}
	/**
	* @Title: searchBusinessPromotion
	* @Description: 搜索优惠劵
	* @param keyword 搜索关键字
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void searchBusinessPromotion(String keyword, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mCouponResultList.clear();
//		}
		SearchBusinessPromotionProtocol protocol = new SearchBusinessPromotionProtocol(mContext, refreshed, mCouponResultList, callBack);
		protocol.search(keyword);
	}
	/**
	* @Title: queryZMObject
	* @Description: 检索空间
	* @param zmObjectType 空间类型(目前只支持:BUSINESS_OBJECT、PUBLICPLACE_OBJECT、VEHICLE_OBJECT)
	* @see com.zhima.base.consts.ZMConsts.ZMObjectKind
	* @param spacekindId 空间子类型。< 0，忽略此参数
	* @param cityId 需检索的空间所属地区id。 < 0，忽略此参数
	* @param geo gps坐标。null，忽略此参数
	* @param order 排序方式。null，忽略此参数
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void queryZMObject(int zmObjectType, long spacekindId, long cityId, GeoCoordinate geo,
			Orderkind order,long jobKindId, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mZMObjectResultList.clear();
//		}
		if (cityId <= 0) {
			cityId = 1;
		}
		QueryZMObjectProtocol protocol = new QueryZMObjectProtocol(mContext, refreshed, mZMObjectResultList, callBack);
		protocol.query(zmObjectType, spacekindId, cityId, geo, order,jobKindId);
	}
	public void queryZMObject(int zmObjectType, long spacekindId,
			String order, boolean refreshed, IHttpRequestCallback callBack) {
		QueryZMObjectProtocol protocol = new QueryZMObjectProtocol(mContext, refreshed, mZMObjectNewResultList, callBack);
		protocol.query(zmObjectType, spacekindId, order);
	}
	/**
	* @Title: queryBusinessPromotion
	* @Description: 检索优惠劵
	* @param spacekindId 空间类型。< 0，忽略此参数
	* @param cityId 需检索的空间所属地区id。 < 0，忽略此参数
	* @param geo gps坐标。null，忽略此参数
	* @param order 排序方式。null，忽略此参数
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void queryBusinessPromotion(long spacekindId, long cityId, GeoCoordinate geo,
			Orderkind order, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mCouponResultList.clear();
//		}
		QueryBusinessPromotionProtocol protocol = new QueryBusinessPromotionProtocol(mContext, refreshed, mCouponResultList, callBack);
		protocol.query(spacekindId, cityId, geo, order);
	}
	
	/**
	 * 芝麻空间_广场_检索空间
	 * @Title: queryPlazaSpace
	 * @Description: TODO
	 * @param zmObjectType
	 * @param spacekindId
	 * @param cityId
	 * @param jobKindId
	 * @param order
	 * @param refreshed
	 * @param callBack
	 */
//	public void queryPlazaSpace(int zmObjectType, long spacekindId, long cityId,long jobKindId,Orderkind order,boolean refreshed,IHttpRequestCallback callBack) {
////		if (refreshed) {
////			mCouponResultList.clear();
////		}
//		QueryZMObjectProtocol protocol = new QueryZMObjectProtocol(mContext, refreshed, mZMObjectResultList, callBack);
////		protocol.query(spacekindId, cityId, geo, order);
//		protocol.queryPlazaSpace(zmObjectType, spacekindId, cityId, jobKindId, order);
//	}
	
	/**
	 * 获取广场 最热空间
	 * @Title: getPlazaSpaceByHot
	 * @Description: TODO
	 * @param spaceKindId
	 * @param refreshed
	 * @param callback
	 */
	public void getPlazaSpaceByHot(int spaceKindType,boolean refreshed,IHttpRequestCallback callback){
		QueryZMObjectProtocol protocol = new QueryZMObjectProtocol(mContext, refreshed, mZMObjectHotResultList, callback);
		protocol.getSpaceByHot(ZMObjectKind.getZMObjectType(spaceKindType));
	}
	
	@Override
	public void onCreate() {		
	}

	@Override
	public void onDestroy() {
		clear();
		mZMObjectResultList = null;
		mZMObjectNewResultList = null;
		mZMObjectHotResultList = null;
		mCouponResultList = null;
		System.gc();
	}

	@Override
	public void clear() {
		if (mZMObjectResultList != null) {
			mZMObjectResultList.clear();
		}
		if (mZMObjectNewResultList != null) {
			mZMObjectNewResultList.clear();
		}
		if (mZMObjectHotResultList != null) {
			mZMObjectHotResultList.clear();
		}
		if (mCouponResultList != null) {
			mCouponResultList.clear();
		}
	}
}
