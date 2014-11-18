/* 
 * @Title: CommerceProtocolHandler.java
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
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpUtils;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommerceProduct;
import com.zhima.data.model.CommercePromotion;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.service.CommerceService;


/**
* @ClassName: CommerceProtocolHandler
* @Description: 商务对象相关协议
* @author liubingsr
* @date 2012-7-12 下午2:27:34
*
*/
public final class CommerceProtocolHandler {	
	/**
	* @ClassName: GetOwnerNoticeListProtocol
	* @Description: 官方公告列表协议
	* @author liubingsr
	* @date 2012-7-16 下午2:59:19
	*
	*/
	public final static class GetOwnerNoticeListProtocol extends ListProtocolHandlerBase<Notice> {
		private final static String TAG = "GetOwnerNoticeListProtocol";
		private String mZMCode = "";
		
		public GetOwnerNoticeListProtocol(Context context, boolean refreshed, RefreshListData<Notice> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getNoticeList
		* @Description: 获取公告列表
		* @param zmCode 码
		* @return void
		*/
		public void getNoticeList(String zmCode) {
			mZMCode = zmCode;
			mSubUrl = "notice/space/by_code/%s";
			String url = mBaseUrl + String.format(mSubUrl, zmCode);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_OWNER_NOTICE_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseNotice();
			return true;
		}

		/**
		* @Title: parseNotice
		* @Description: 解析数据包得到公告信息
		* @param json
		* @return ArrayList<Notice>
		*/
		private ArrayList<Notice> parseNotice() {
			ArrayList<Notice> noticeList = new ArrayList<Notice>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								Notice notice = Notice.parse(item);
								if (notice != null) {
//									notice.setZMCode(mZMCode);
									noticeList.add(notice);
								}
							}
							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
							mDataList.setLastPage(true);
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
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return noticeList;
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
//				for (Notice notice : mReceiveDataList) {
//					// 存入本地缓存
//					CommerceService.getInstance(mContext).addOwnerNotice(notice);
//				}
			}			
		}		
	}
	/**
	* @ClassName: GetOwnerNoticeProtocol
	* @Description: 公告详情
	* @author liubingsr
	* @date 2012-7-16 下午3:17:47
	*
	*/
	public final static class GetOwnerNoticeProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetOwnerNoticeProtocol";
		
		private long mNoticeId;
		private Notice mNotice;
		/**
		* <p>Title: </p>
		* <p>Description: </p>
		* @param context
		* @param callBack 结果通知回调
		*/
		public GetOwnerNoticeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getNotice
		* @Description: 服务端返回的公告信息
		* @return
		* Notice
		*/
		public Notice getNotice() {
			return mNotice;
		}
		
		public void getOwnerNotice(long noticeId) {
			mNoticeId = noticeId;
			mSubUrl = "notice/space/%d";
			String url = mBaseUrl + String.format(mSubUrl, noticeId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_OWNER_NOTICE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mNotice = Notice.parse(objArray.getJSONObject(0));
							if (mNotice != null) {
								mNotice.setNoticeId(mNoticeId);
							}
						}
						
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: GetCommerceProductProtocol
	* @Description: 获取商户商品协议
	* @author liubingsr
	* @date 2012-7-18 上午10:33:13
	*
	*/
	public final static class GetCommerceProductProtocol extends ListProtocolHandlerBase<CommerceProduct> {
		private final static String TAG = "GetCommerceProductProtocol";
		private long mZMObjectId;
		
		public GetCommerceProductProtocol(Context context, boolean refreshed, RefreshListData<CommerceProduct> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		
		public void getCommerceProducts(long zmObjectId) {
			mZMObjectId = zmObjectId;
			mSubUrl = "space/business/%d/product?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, zmObjectId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_COMMERCE_PRODUCT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseProducts();
			return true;
		}

		/**
		* @Title: parseProducts
		* @Description: 解析数据包得到商品信息
		* @param json
		* @return ArrayList<CommerceProduct>
		*/
		private ArrayList<CommerceProduct> parseProducts() {
			ArrayList<CommerceProduct> productList = new ArrayList<CommerceProduct>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								CommerceProduct product = CommerceProduct.parse(item);
								if (product != null) {
									product.setCommerceId(mZMObjectId);
									productList.add(product);
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
					mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
					mDataList.setLastPage(true);
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return productList;
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
//				for (CommerceProduct product : mReceiveDataList) {
//					// 存入缓存
//					CommerceService.getInstance(mContext).addCommerceProduct(product);
//				}
			}			
		}		
	}
	/**
	* @ClassName: GetCommercePromotionListProtocol
	* @Description: 获取商户活动列表协议
	* @author liubingsr
	* @date 2012-7-18 下午3:20:50
	*
	*/
	public final static class GetCommercePromotionListProtocol extends ListProtocolHandlerBase<CommercePromotion> {
		private final static String TAG = "GetCommercePromotionListProtocol";
		private long mZMObjectId;
		
		public GetCommercePromotionListProtocol(Context context, boolean refreshed, RefreshListData<CommercePromotion> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getPromotionList
		* @Description: 获取活动列表
		* @param zmObjectId id
		* @return void
		*/
		public void getPromotionList(long zmObjectId) {
			mZMObjectId = zmObjectId;
			mSubUrl = "space/business/%d/activity?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mZMObjectId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_COMMERCE_PROMOTION_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parsePromotion();
			return true;
		}

		/**
		* @Title: parsePromotion
		* @Description: 解析数据包得到活动信息
		* @param json
		* @return ArrayList<CommercePromotion>
		*/
		private ArrayList<CommercePromotion> parsePromotion() {
			ArrayList<CommercePromotion> promotionList = new ArrayList<CommercePromotion>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								CommercePromotion promotion = CommercePromotion.parse(item);
								if (promotion != null) {
									promotion.setCommerceId(mZMObjectId);
									promotionList.add(promotion);
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
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return promotionList;
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
//				for (CommercePromotion promotion : mReceiveDataList) {
//					// 存入缓存
//					CommerceService.getInstance(mContext).addCommercePromotion(promotion);
//				}
			}			
		}		
	}
	/**
	* @ClassName: GainCouponProtocolHandler
	* @Description: 领取优惠券处理协议
	* @author liubingsr
	* @date 2012-8-13 下午3:25:22
	*
	*/
	public final static class GainCouponProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "GainCouponProtocolHandler";
		private int mRemainCount = 0;
		
		public GainCouponProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getRemainCount
		* @Description: 剩余优惠券数量
		* @return
		*/
		public int getRemainCount() {
			return mRemainCount;
		}
		/**
		* @Title: gainCoupon
		* @Description: 领取优惠券
		* @param promotionId 活动id
		* @return void
		*/
		public void gainCoupon(long promotionId) {
			mSubUrl = "space/business/activity/%d/receive";
			String url = mBaseUrl + String.format(mSubUrl, promotionId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GAIN_COUPON_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							mRemainCount = array.getJSONObject(0).getInt("residueCount");
							return true;
						}
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO
		}
	}
	

	/**
	 * @ClassName:CheckMyCouponOfCommerceProtocolHandler
	 * @Description:检查当前用户是否拥有该商户的优惠券
	 * @author liqilong
	 * @date 2012-8-24 上午11:49:36
	 *
	 */
	public final static class CheckMyCouponOfCommerceProtocolHandler extends ProtocolHandlerBase{
		private final static String TAG = "CheckMyCouponOfCommerceProtocolHandler";
		private long mZMObjectId;
		private boolean mHasCoupon = false;
		
		public CheckMyCouponOfCommerceProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public void checkMyCouponOf(long remoteId) {
			mSubUrl = "space/business/%d/activity/coupon/check";
			String url = mBaseUrl + String.format(mSubUrl, remoteId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.CHECK_MYCOUPON_OF_COMMERCE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		public boolean hasCoupon() {
			return mHasCoupon;
		}
		@Override
		public boolean parse() {
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mHasCoupon = objArray.getBoolean(0);
							return mHasCoupon;
						} else {
							// 没有数据
						}
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return false;
		}
		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}
	}
	/**
	* @ClassName: GetMyCouponListProtocol
	* @Description: 获取拥有的来自该商户的优惠券列表协议
	* @author liubingsr
	* @date 2012-8-22 下午2:36:48
	*
	*/
	public final static class GetMyCouponOfCommerceListProtocol extends ListProtocolHandlerBase<UserCoupon> {
		private final static String TAG = "GetMyCouponOfCommerceListProtocol";
		
		private long mCommerceId;
		
		public GetMyCouponOfCommerceListProtocol(Context context, boolean refreshed, RefreshListData<UserCoupon> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			mCommerceId = 0;
		}
		/**
		* @Title: getMyCouponList
		* @Description: 获取优惠券列表
		* @param commerceId id
		* @return void
		*/
		public void getMyCouponList(long commerceId) {
			mCommerceId = commerceId;
			mSubUrl = "space/business/%d/activity/coupon?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mCommerceId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_MYCOUPON_OF_COMMERCE_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseCoupon();
			return true;
		}

		/**
		* @Title: parseCoupon
		* @Description: 解析数据包得到优惠券信息
		* @param json
		* @return
		*/
		private ArrayList<UserCoupon> parseCoupon() {
			ArrayList<UserCoupon> couponList = new ArrayList<UserCoupon>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								UserCoupon coupon = UserCoupon.parse(item);
								if (coupon != null) {
									coupon.setCommerceId(mCommerceId);
									couponList.add(coupon);
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
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return couponList;
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
	* @ClassName: GetCommercePromotionProtocol
	* @Description: 获取商户活动详情信息协议
	* @author liubingsr
	* @date 2012-8-23 上午11:52:44
	*
	*/
	public final static class GetCommercePromotionProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetCommercePromotionProtocol";
		private long mCommerceId;
		private CommercePromotion mCommercePromotion;

		public GetCommercePromotionProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mCommercePromotion = null;
		}
		/**
		* @Title: getProduct
		* @Description: 服务端返回的格子铺物品
		* @return null 没有数据
		*/
		public CommercePromotion getProduct() {
			return mCommercePromotion;
		}
		
		public void getPromotion(long commerceId, long promotionId) {
			mCommerceId = commerceId;
			mSubUrl = "space/business/activity/%d";
			String url = String.format(mBaseUrl + mSubUrl, promotionId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_COMMERCE_PROMOTION_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mCommercePromotion = CommercePromotion.parse(objArray.getJSONObject(0));
							if (mCommercePromotion != null && mCommerceId > 0) {
								mCommercePromotion.setCommerceId(mCommerceId);
								return true;
							}
						} else {
							// 没有数据
						}						
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			CommerceService.getInstance(mContext).addCommercePromotion(mCommercePromotion);		
		}		
	}
	/**
	* @ClassName: UseCouponProtocolHandler
	* @Description: 使用优惠券协议
	* @author liubingsr
	* @date 2012-8-22 下午4:06:22
	*
	*/
	public final static class UseCouponProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "UseCouponProtocolHandler";
		private long mCouponId = 0;
		private CommerceObject mCommerce;
		
		public UseCouponProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getRemainCount
		* @Description: 剩余优惠券数量
		* @return
		*/
		public long getRemainCount() {
			return mCouponId;
		}
		/**
		* @Title: useCoupon
		* @Description: 使用优惠券
		* @param commerce 商户
		* @param couponId 优惠卡id
		* @param password 密码
		* @return void
		*/
		public void useCoupon(CommerceObject commerce, long couponId, String password) {
			mCommerce = commerce;
			mCouponId = couponId;
			mSubUrl = "space/business/activity/coupon/%d/use?passcode=%s";
			String url = mBaseUrl + String.format(mSubUrl, couponId, HttpUtils.urlEncode(password, "utf-8"));
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.PUT);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.USE_COUPON_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							return true;
						}
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			CommerceService.getInstance(mContext).updateCouponStatus(mCommerce, mCouponId);
		}
	}
}
