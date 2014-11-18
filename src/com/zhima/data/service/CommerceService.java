/* 
 * @Title: CommerceService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.CommerceProtocolHandler.CheckMyCouponOfCommerceProtocolHandler;
import com.zhima.base.protocol.CommerceProtocolHandler.GainCouponProtocolHandler;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommerceProductProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommercePromotionListProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommercePromotionProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetMyCouponOfCommerceListProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetOwnerNoticeListProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetOwnerNoticeProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.UseCouponProtocolHandler;
import com.zhima.base.protocol.UserProtocolHandler.DeleteMyCouponProtocol;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommerceProduct;
import com.zhima.data.model.CommercePromotion;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;

/**
* @ClassName: CommerceService
* @Description: 商户
* @author liubingsr
* @date 2012-7-13 上午9:54:27
*
*/
public class CommerceService extends BaseService {
	private final static String TAG = "CommerceService";
	private static CommerceService mInstance = null;
		
	private TreeMap<Long, RefreshListData<CommerceProduct>> mCommerceProductMap;
	private TreeMap<Long, RefreshListData<CommercePromotion>> mCommercePromotionMap;
	private TreeMap<Long, RefreshListData<UserCoupon>> mMyCouponMap;
	//
	/**
	 * 缓存的商家公告
	 */
	private TreeMap<String, RefreshListData<Notice>> mOwnerNoticeMap;
	
	private CommerceService(Context context) {
		super(context);		
		mCommerceProductMap = new TreeMap<Long, RefreshListData<CommerceProduct>>();
		mCommercePromotionMap = new TreeMap<Long, RefreshListData<CommercePromotion>>();		
		mOwnerNoticeMap = new TreeMap<String, RefreshListData<Notice>>();
		mMyCouponMap = new TreeMap<Long, RefreshListData<UserCoupon>>();
		onCreate();
	}

	public static CommerceService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new CommerceService(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
	}
	/**
	* @Title: addOwnerNotice
	* @Description: 加入到本地缓存
	* @param notice 公告
	* @return void
	*/
	public void addOwnerNotice(Notice notice) {
//		ArrayList<Notice> array;
//		String zmCode = notice.getZMCode();
//		if (mOwnerNoticeMap.containsKey(zmCode)) {
//			array = mOwnerNoticeMap.get(zmCode);
//			for (int index = 0,count = array.size(); index < count; ++index) {
//				if (array.get(index).getId() == notice.getId()) {
//					return;
//				}
//			}			
//		} else {
//			array = new ArrayList<Notice>();
//		}
//		array.add(notice);
//		mOwnerNoticeMap.put(zmCode, array);
	}
	/**
	* @Title: getCacheOwnerNoticeList
	* @Description: 从本地缓存获取商家公告列表
	* @param zmCode 
	* @return
	*/
	public RefreshListData<Notice> getCacheOwnerNoticeList(String zmCode) {
		if (mOwnerNoticeMap.containsKey(zmCode)) {
			return mOwnerNoticeMap.get(zmCode);
		} else {
			return new RefreshListData<Notice>();
		}
	}
	
	/**
	* @Title: getOwnerNoticeList
	* @Description: 从服务器获取商家公告列表
	* @param code 码内容
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/	
	public void getOwnerNoticeList(String zmCode, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<Notice> list = null;
		if (!mOwnerNoticeMap.containsKey(zmCode)) {
			list = new RefreshListData<Notice>();
			mOwnerNoticeMap.put(zmCode, list);
		} else {
			list = mOwnerNoticeMap.get(zmCode);
		}
		GetOwnerNoticeListProtocol protocol = new GetOwnerNoticeListProtocol(mContext, refreshed, list, callBack);
		protocol.getNoticeList(zmCode);
	}
	/**
	* @Title: getOwnerNotice
	* @Description: 获取商家公告详情
	* @param noticeId 公告Id
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getOwnerNotice(long noticeId, IHttpRequestCallback callBack) {
		GetOwnerNoticeProtocol protocol = new GetOwnerNoticeProtocol(mContext, callBack);
		protocol.getOwnerNotice(noticeId);
	}
	/**
	* @Title: addCommerceProduct
	* @Description: 商户产品加入本地缓存
	* @param product 产品
	* @return void
	*/
	public void addCommerceProduct(CommerceProduct product) {
//		ArrayList<CommerceProduct> array;
//		long commerceId = product.getCommerceId();
//		if (mCommerceProductMap.containsKey(commerceId)) {
//			array = mCommerceProductMap.get(commerceId);
//			for (int index = 0,count = array.size(); index < count; ++index) {
//				if (array.get(index).getId() == product.getId()) {
//					return;
//				}
//			}			
//		} else {
//			array = new ArrayList<CommerceProduct>();
//		}
//		array.add(product);
//		mCommerceProductMap.put(commerceId, array);
	}
	/**
	* @Title: getCacheCommerceProduct
	* @Description: 从本地缓存中获取CommerceProduct信息
	* @param productId 产品id
	* @return null 本地无数据
	*/
	public CommerceProduct getCacheCommerceProduct(long productId) {
		Set<Long> keys = mCommerceProductMap.keySet();
		 RefreshListData<CommerceProduct> list;
		 for (Long key : keys) {
			 list = mCommerceProductMap.get(key);
			 if (list.getMap().containsKey(productId)) {
				 return list.getMap().get(productId);
			 }
		 }
		 return null;
	}
	/**
	* @Title: getCacheCommerceProductList
	* @Description: 从本地缓存获取商户产品列表
	* @param commerce 商户
	* @return
	*/
	public RefreshListData<CommerceProduct> getCacheCommerceProductList(CommerceObject commerce) {
		if (mCommerceProductMap.containsKey(commerce.getRemoteId())) {
			return mCommerceProductMap.get(commerce.getRemoteId());
		} else {
			return new RefreshListData<CommerceProduct>();
		}
	}
	/**
	* @Title: getCommerceProductList
	* @Description: 得到商家商品列表
	* @param commerce 商户
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getCommerceProductList(CommerceObject commerce, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mCommerceProductMap.remove(commerce.getRemoteId());
//		}
		RefreshListData<CommerceProduct> list = null;
		if (!mCommerceProductMap.containsKey(commerce.getRemoteId())) {
			list = new RefreshListData<CommerceProduct>();
			mCommerceProductMap.put(commerce.getRemoteId(), list);
		} else {
			list = mCommerceProductMap.get(commerce.getRemoteId());
		}
		GetCommerceProductProtocol protocol = new GetCommerceProductProtocol(mContext, refreshed, list, callBack);
		protocol.getCommerceProducts(commerce.getRemoteId());
	}
	
	/**
	* @Title: addCommercePromotion
	* @Description: 商户活动加入本地缓存
	* @param promotion 商户活动
	* @return void
	*/
	public void addCommercePromotion(CommercePromotion promotion) {
		RefreshListData<CommercePromotion> list;
		long commerceId = promotion.getCommerceId();
		if (mCommercePromotionMap.containsKey(commerceId)) {
			list = mCommercePromotionMap.get(commerceId);
		} else {
			list = new RefreshListData<CommercePromotion>();			
		}
		list.add(promotion);
		mCommercePromotionMap.put(commerceId, list);
	}
	/**
	* @Title: getCacheCommercePromotion
	* @Description: 从本地缓存中获取CommercePromotion信息
	* @param promotionId 活动id
	* @return null 本地无数据
	*/
	public CommercePromotion getCacheCommercePromotion(long promotionId) {
		Set<Long> keys = mCommercePromotionMap.keySet();
		RefreshListData<CommercePromotion> list;
		for (Long key : keys) {
			list = mCommercePromotionMap.get(key);
			if (list.getMap().containsKey(promotionId)) {
				return list.getMap().get(promotionId);
			}
		}
		return null;
	}
	/**
	* @Title: getCommercePromotion
	* @Description: 获取CommercePromotion信息
	* @param promotionId 活动id
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getCommercePromotion(long remoteId, long promotionId, IHttpRequestCallback callBack) {
		GetCommercePromotionProtocol protocol = new GetCommercePromotionProtocol(mContext, callBack);
		protocol.getPromotion(remoteId, promotionId);
	}
	/**
	* @Title: getCommercePromotion
	* @Description: 获取CommercePromotion信息
	* @param couponId 活动id
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getCommercePromotion(long couponId, IHttpRequestCallback callBack) {
		GetCommercePromotionProtocol protocol = new GetCommercePromotionProtocol(mContext, callBack);
		protocol.getPromotion(0, couponId);
	}
	/**
	* @Title: getCacheCommercePromotionList
	* @Description: 从本地缓存获取商家活动列表
	* @param commerce 商户
	* @return
	*/
	public RefreshListData<CommercePromotion> getCacheCommercePromotionList(CommerceObject commerce) {
		if (mCommercePromotionMap.containsKey(commerce.getRemoteId())) {
			return mCommercePromotionMap.get(commerce.getRemoteId());
		} else {
			return new RefreshListData<CommercePromotion>();
		}
	}
	/**
	* @Title: getCommercePromotionList
	* @Description: 得到商家活动列表
	* @param commerce 商户
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getCommercePromotionList(CommerceObject commerce, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mCommercePromotionMap.remove(commerce.getRemoteId());
//		}
		RefreshListData<CommercePromotion> list = null;
		if (!mCommercePromotionMap.containsKey(commerce.getRemoteId())) {
			list = new RefreshListData<CommercePromotion>();
			mCommercePromotionMap.put(commerce.getRemoteId(), list);
		} else {
			list = mCommercePromotionMap.get(commerce.getRemoteId());
		}
		GetCommercePromotionListProtocol protocol = new GetCommercePromotionListProtocol(mContext, refreshed, list, callBack);
		protocol.getPromotionList(commerce.getRemoteId());
	}
	/**
	* @Title: gainCoupon
	* @Description: 领取优惠券
	* @param couponId 优惠券id
	* @param callBack 结果通知回调	
	* @return void
	*/
	public void gainCoupon(long couponId, IHttpRequestCallback callBack) {
		GainCouponProtocolHandler protocol = new GainCouponProtocolHandler(mContext, callBack);
		protocol.gainCoupon(couponId);
	}
	/**
	* @Title: getCacheCoupon
	* @Description: 从本地缓存中获取Coupon信息
	* @param couponId 优惠券id
	* @return null 本地无数据
	*/
	public UserCoupon getCacheCoupon(long couponId) {
		Set<Long> keys = mMyCouponMap.keySet();
		RefreshListData<UserCoupon> list;
		for (Long key : keys) {
			list = mMyCouponMap.get(key);
			if (list.getMap().containsKey(couponId)) {
				return list.getMap().get(couponId);
			}
		}
		return null;
	}
	public void removeCacheCoupon(long couponId) {
		Set<Long> keys = mMyCouponMap.keySet();
		RefreshListData<UserCoupon> list;
		for (Long key : keys) {
			list = mMyCouponMap.get(key);
			if (list.getMap().containsKey(couponId)) {
				list.removeObject(couponId);
			}
		}
	}
	/**
	* @Title: getCacheMyCouponOfCommerceList
	* @Description: 从本地缓存获取拥有的来自该商户的优惠券列表
	* @param commerce 商户
	* @return
	*/
	public RefreshListData<UserCoupon> getCacheMyCouponOfCommerceList(CommerceObject commerce) {
		if (mMyCouponMap.containsKey(commerce.getRemoteId())) {
			return mMyCouponMap.get(commerce.getRemoteId());
		} else {
			return new RefreshListData<UserCoupon>();
		}
	}
	/**
	* @Title: checkMyCouponOfCommerce
	* @Description: 检查当前用户是否拥有该商户的优惠券
	* @param commerce 商户
	* @param callBack 结果通知回调
	* @return void
	*/
	public void checkMyCouponOfCommerce(CommerceObject commerce, IHttpRequestCallback callBack) {
		CheckMyCouponOfCommerceProtocolHandler protocol = new CheckMyCouponOfCommerceProtocolHandler(mContext , callBack);
		protocol.checkMyCouponOf(commerce.getRemoteId());
	}
	/**
	* @Title: getMyCouponOfCommerceList
	* @Description: 获取拥有的来自该商户的优惠券列表
	* @param commerce 商户
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getMyCouponOfCommerceList(CommerceObject commerce, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mMyCouponMap.remove(commerce.getRemoteId());
//		}
		RefreshListData<UserCoupon> list = null;
		if (!mMyCouponMap.containsKey(commerce.getRemoteId())) {
			list = new RefreshListData<UserCoupon>();
			mMyCouponMap.put(commerce.getRemoteId(), list);
		} else {
			list = mMyCouponMap.get(commerce.getRemoteId());
		}
		GetMyCouponOfCommerceListProtocol protocol = new GetMyCouponOfCommerceListProtocol(mContext, refreshed, list, callBack);
		protocol.getMyCouponList(commerce.getRemoteId());
	}
	/**
	 * @Title: deleteMyCoupon
	 * @Description: 删除优惠券
	 * @param couponId 要删除的优惠券id
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void deleteMyCoupon(long couponId, IHttpRequestCallback callBack) {
		DeleteMyCouponProtocol protocol = new DeleteMyCouponProtocol(mContext, callBack);
		protocol.deleteMyCoupon(couponId);
	}
	/**
	* @Title: updateCouponStatus
	* @Description: 更新本地缓存的优惠券状态
	* @param commerce 商户
	* @param couponId 优惠券id
	* @return void
	*/
	public void updateCouponStatus(CommerceObject commerce, long couponId) {
		if (mMyCouponMap.containsKey(commerce.getRemoteId())) {
			RefreshListData<UserCoupon> list = mMyCouponMap.get(commerce.getRemoteId());
			list.getMap().get(couponId).setUsed(true);
		}
	}
	/**
	* @Title: useCoupon
	* @Description: 使用优惠券
	* @param commerce 商户
	* @param couponId 优惠券id
	* @param password 密码
	* @param callBack 结果通知回调
	* @return void
	*/
	public void useCoupon(CommerceObject commerce, long couponId, String password, IHttpRequestCallback callBack) {
		UseCouponProtocolHandler protocol = new UseCouponProtocolHandler(mContext, callBack);
		protocol.useCoupon(commerce, couponId, password);
	}
	
	@Override
	public void onDestroy() {
		clear();
		mCommerceProductMap = null;
		mCommercePromotionMap = null;
		mMyCouponMap = null;
		System.gc();
	}

	@Override
	public void clear() {
		if (mCommerceProductMap != null) {
			mCommerceProductMap.clear();			
		}
		if (mCommercePromotionMap != null) {
			mCommercePromotionMap.clear();			
		}
		if (mMyCouponMap != null) {
			mMyCouponMap.clear();
		}
	}
}
