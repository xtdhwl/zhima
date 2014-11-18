/* 
 * @Title: UserProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.network.uploadfile.FormFile;
import com.zhima.base.protocol.vo.VoAlbumImage;
import com.zhima.base.protocol.vo.VoLatticeProduct;
import com.zhima.base.protocol.vo.VoUserInfo;
import com.zhima.data.model.LatticeProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.UserAlbumImage;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.model.UserHavings;
import com.zhima.data.model.UserRecordEntry;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.UserService;

/**
 * @ClassName: UserProtocolHandler
 * @Description: 用户协议处理
 * @author liubingsr
 * @date 2012-7-25 下午3:39:13
 * 
 */
public final class UserProtocolHandler {
	/**
	 * @ClassName: GetMyCouponListProtocol
	 * @Description: 获取我的优惠券列表协议
	 * @author liubingsr
	 * @date 2012-8-22 下午4:50:08
	 * 
	 */
	public final static class GetMyCouponListProtocol extends ListProtocolHandlerBase<UserCoupon> {
		private final static String TAG = "GetMyCouponListProtocol";

		public GetMyCouponListProtocol(Context context, boolean refreshed, RefreshListData<UserCoupon> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getMyCouponList
		 * @Description: 获取优惠券列表
		 * @return void
		 */
		public void getMyCouponList() {
			mSubUrl = "inbox/card?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_MYCOUPON_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseCoupon(json);
			return true;
		}

		/**
		 * @Title: parseCoupon
		 * @Description: 解析数据包得到优惠券信息
		 * @param json
		 * @return
		 */
		private ArrayList<UserCoupon> parseCoupon(String json) {
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
	 * @ClassName SearchCardProtocol
	 * @Description 以关键字搜索卡片
	 * @author jiang
	 * @date 2013-1-26 下午08:09:19
	 */
	public final static class SearchCardProtocol extends ListProtocolHandlerBase<UserCoupon>{

		private final static String TAG = "SearchCardProtocol";
		public SearchCardProtocol(Context context, boolean refreshed, RefreshListData<UserCoupon> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			// TODO Auto-generated constructor stub
		}
		
		public void getSearchCard(String keyword){
			mSubUrl = "inbox/card/search?keyword=%s&pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, keyword, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SEARCH_CARD_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseCoupon(json);
			return true;
		}
		
		/**
		 * @Title: parseCoupon
		 * @Description: 解析数据包得到优惠券信息
		 * @param json
		 * @return
		 */
		private ArrayList<UserCoupon> parseCoupon(String json) {
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
	 * @ClassName SearchUserRecordProtocol
	 * @Description 以关键字搜索个人管理记录
	 * @author jiang
	 * @date 2013-1-26 下午07:53:53
	 */
	public final static class SearchUserRecordProtocol extends ListProtocolHandlerBase<UserRecordEntry> {
		private final static String TAG = "SearchUserRecordProtocol";

		public SearchUserRecordProtocol(Context context, boolean refreshed, RefreshListData<UserRecordEntry> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			// TODO Auto-generated constructor stub
		}

		public void searchUserRecord(int recordType, String keyword, long lastTime) {

			mSubUrl = "user/record/search/%d?keyword=%s&pageSize=%d";
			String url = mBaseUrl + String.format(mSubUrl, recordType, keyword, mPageSize);
			if (lastTime > 0) {
				url += "&lastTime=" + lastTime;
			}
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SEARCH_USER_RECORD_PROTOCOL;
			mRequestService.sendRequest(this);

		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseResult(json);
			return true;
		}

		private ArrayList<UserRecordEntry> parseResult(String json) {
			ArrayList<UserRecordEntry> recordEntryList = new ArrayList<UserRecordEntry>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								UserRecordEntry recordEntry = UserRecordEntry.parse(item);
								if (recordEntry != null) {
									recordEntryList.add(recordEntry);
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
			return recordEntryList;
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
	 * @ClassName: DeleteMyCouponProtocol
	 * @Description: 删除优惠券协议
	 * @author liubingsr
	 * @date 2012-8-22 下午5:11:24
	 * 
	 */
	public final static class DeleteMyCouponProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteMyCouponProtocol";
		private long mCouponId = 0;

		public DeleteMyCouponProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: deleteMyCoupon
		 * @Description: 删除优惠券
		 * @param couponId
		 *            要删除的优惠券id
		 * @return void
		 */
		public void deleteMyCoupon(long couponId) {
			mSubUrl = "inbox/card/%d";
			mCouponId = couponId;
			String url = mBaseUrl + String.format(mSubUrl, mCouponId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_MYCOUPON_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
						}
						return true;
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// 删除本地缓存数据
			UserService.getInstance(mContext).removeCacheCoupon(mCouponId);
			CommerceService.getInstance(mContext).removeCacheCoupon(mCouponId);
		}
	}

	/**
	 * @ClassName: DeleteMyAllCouponProtocol
	 * @Description: 删除所有优惠券协议
	 * @author liubingsr
	 * @date 2012-9-13 下午2:52:56
	 * 
	 */
	public final static class DeleteMyAllCouponProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteMyAllCouponProtocol";

		public DeleteMyAllCouponProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: deleteAllCoupon
		 * @Description: 删除所有优惠券
		 * @param couponId
		 *            要删除的优惠券id
		 * @return void
		 */
		public void deleteAllCoupon() {
			mSubUrl = "inbox/card";
			String url = mBaseUrl + String.format(mSubUrl);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_MY_ALL_COUPON_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
						}
						return true;
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// 删除本地缓存数据
			UserService.getInstance(mContext).clearCacheCoupon();
		}
	}

	/**
	 * @ClassName: GetUserRecordListProtocol
	 * @Description: 获取个人管理记录列表协议
	 * @author liubingsr
	 * @date 2012-9-4 上午11:12:28
	 * 
	 */
	public final static class GetUserRecordListProtocol extends ListProtocolHandlerBase<UserRecordEntry> {
		private final static String TAG = "GetUserRecordListProtocol";

		public GetUserRecordListProtocol(Context context, boolean refreshed, RefreshListData<UserRecordEntry> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getUserRecordList
		 * @Description: 获取列表
		 * @param recordType
		 *            类型
		 * @param begingTime
		 *            开始时间
		 * @param endTime
		 *            结束时间
		 * @param lastTimestamp
		 *            上一页最后一项的创建时间
		 * @return void
		 */
		public void getUserRecordList(int recordType, long begingTime, long endTime, long lastTimestamp) {
			mSubUrl = "user/record/%d?beginTime=%d&endTime=%d&pageSize=%d";
			String url = mBaseUrl + String.format(mSubUrl, recordType, begingTime, endTime, mPageSize);
			if (lastTimestamp > 0) {
				url += "&lastTime=" + lastTimestamp;
			}
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USER_RECORD_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseResult(json);
			return true;
		}

		/**
		 * @Title: parseResult
		 * @Description: 解析数据包
		 * @param json
		 * @return
		 */
		private ArrayList<UserRecordEntry> parseResult(String json) {
			ArrayList<UserRecordEntry> recordEntryList = new ArrayList<UserRecordEntry>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								UserRecordEntry recordEntry = UserRecordEntry.parse(item);
								if (recordEntry != null) {
									recordEntryList.add(recordEntry);
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
			return recordEntryList;
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

	public final static class DeleteUserRecordProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteUserRecordProtocol";
		private boolean mIsDeleteAll = false;
		private int mRecordType = -1;
		private long mRecordId = 0;
		private boolean mSuccess = false;

		public DeleteUserRecordProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public boolean isSuccess() {
			return mSuccess;
		}

		/**
		 * @Title: deleteUserRecord
		 * @Description: 删除一条记录
		 * @param entryId
		 *            条目id
		 * @return void
		 */
		public void deleteUserRecord(int recordType, long entryId) {
			mRecordType = recordType;
			mRecordId = entryId;
			mIsDeleteAll = false;
			mSubUrl = "user/record/%d/%d";
			String url = mBaseUrl + String.format(mSubUrl, recordType, entryId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_USER_RECORD_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public void deleteAllRecords(int recordType) {
			mIsDeleteAll = true;
			mSubUrl = "user/record/%d";
			String url = mBaseUrl + String.format(mSubUrl, recordType);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_USER_RECORD_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public void deleteContact(ArrayList<Long> idList) {
			mSubUrl = "space_contact/batch_delete";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = IdListToJson(idList);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_USER_RECORD_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							if (!mIsDeleteAll) {
								mSuccess = objArray.getBoolean(0);
							} else {
								mSuccess = true;
							}
							return mSuccess;
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (!mIsDeleteAll) {
				UserService.getInstance(mContext).deleteCacheUserRecord(mRecordType, mRecordId);
			} else {
				UserService.getInstance(mContext).deleteCacheUserRecord(mRecordType);
			}
		}
	}

	/**
	 * @ClassName: GetUserInfoProtocol
	 * @Description: 获取用户详细信息协议
	 * @author liubingsr
	 * @date 2013-1-17 下午5:25:48
	 * 
	 */
	public final static class GetUserInfoProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetUserInfoProtocol";
		private User mUserInfo = null;

		public GetUserInfoProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public User getUser() {
			return mUserInfo;
		}

		public void getUserInfo(long userId) {
			mSubUrl = "user/%d/info";
			String url = mBaseUrl + String.format(mSubUrl, userId);
			getUserInfo(url);
		}

		public void getMyself() {
			mSubUrl = "user/own/info";
			getUserInfo(mBaseUrl + mSubUrl);
		}

		private void getUserInfo(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USERINFO_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							JSONObject userJson = array.getJSONObject(0);
							mUserInfo = User.parse(userJson);
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			if (mUserInfo != null) {
				// 保存进本地sqlite
				UserService.getInstance(mContext).addUser(mUserInfo);
			}
		}
	}

	/**
	 * @ClassName: UpdateUserInfoProtocol
	 * @Description: 更新用户信息
	 * @author liubingsr
	 * @date 2012-8-18 下午2:19:41
	 * 
	 */
	public final static class UpdateUserInfoProtocol extends ProtocolHandlerBase {
		private final static String TAG = "UpdateUserInfoProtocol";
		private User mUser = null;
		private static Gson gson = null;
		static {
			if (gson == null) {
				gson = new Gson();
			}
		}

		public UpdateUserInfoProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: updateMyselfInfo
		 * @Description: 更新当前登录用户信息
		 * @param user
		 * @return void
		 */
		public void updateMyselfInfo(User user) {
			mUser = user;
			mSubUrl = "user/own/info";
			String url = mBaseUrl + String.format(mSubUrl);
			// 准备json数据
			VoUserInfo vo = new VoUserInfo();
			vo.setDisplayName(mUser.getNickname());
			vo.setGender(mUser.getGender());
			vo.setDateOfBirth(mUser.getBirthday());
			vo.setBloodType(mUser.getBloodType());
			vo.setSignature(mUser.getSignature());
			vo.setCityId(mUser.getCityId());
			vo.setBoardId(mUser.getBoardId());
			vo.setSchoolId(mUser.getSchoolId());

			String json = gson.toJson(vo);
			if (!TextUtils.isEmpty(json)) {
				RequestInfo info = new RequestInfo(url);
				info.setCharset("utf-8");
				// if (!TextUtils.isEmpty(mUser.getImageUrl())) {
				// FormFile formFile = new FormFile(mUser.getImageUrl(), "");
				// info.addUploadFile(formFile);
				// info.setRequestType(RequestType.UPLOAD);
				// } else {
				// info.setRequestType(RequestType.PUT);
				// }
				info.setRequestType(RequestType.PUT);
				mProtocolType = ProtocolType.UPDATE_USERINFO_PROTOCOL;
				info.setBody(json);
				this.setRequestInfo(info);
				mRequestService.sendRequest(this);
			} else {
				Logger.getInstance(TAG).debug("发送的用户数据：<" + json + ">失败");
			}
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						return true;
						// if (objArray != null && objArray.length() > 0) {
						// // mNewAvatarUrl = objArray.getString(0);
						// return true;
						// }
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (mUser != null) {
				UserService.getInstance(mContext).addUser(mUser);
			}
		}
	}

	/**
	 * @ClassName: UpdateUserAvatarProtocol
	 * @Description: 更新用户头像协议
	 * @author liubingsr
	 * @date 2012-8-21 上午10:00:22
	 * 
	 */
	public final static class UpdateUserAvatarProtocol extends ProtocolHandlerBase {
		private final static String TAG = "UpdateUserAvatarProtocol";
		private String mNewAvatarUrl;

		public UpdateUserAvatarProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mNewAvatarUrl = null;
		}

		/**
		 * @Title: updateAvatar
		 * @Description: 更新头像
		 * @param imagePath
		 *            图片路径
		 * @return void
		 */
		public void updateAvatar(String imagePath) {
			mSubUrl = "user/own/info/with_img";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setCharset("utf-8");
			FormFile formFile = new FormFile(imagePath, "");
			info.addUploadFile(formFile);
			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.UPDATE_USER_AVATAR_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mNewAvatarUrl = objArray.getString(0);
						}
					}
					return true;
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (mNewAvatarUrl != null) {
				// 更新本地缓存的头像
				UserService.getInstance(mContext).updateAvatar(mNewAvatarUrl);
			}
		}
	}

	/**
	 * @ClassName: GetLatticeProductListProtocol
	 * @Description: 获取用户格子铺物品协议
	 * @author liubingsr
	 * @date 2012-8-21 上午10:37:30
	 * 
	 */
	public final static class GetLatticeProductListProtocol extends ListProtocolHandlerBase<LatticeProduct> {
		private final static String TAG = "GetLatticeProductListProtocol";
		private long mUserId;

		public GetLatticeProductListProtocol(Context context, boolean refreshed, RefreshListData<LatticeProduct> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			mUserId = 0;
		}

		public void getProducts() {
			mUserId = AccountService.getInstance(mContext).getUserId();
			mSubUrl = "user/product/user/own";
			String url = mBaseUrl + String.format(mSubUrl + "?pageSize=%d&lastId=%d", mPageSize, mLastId);
			doGetProducts(url);
		}

		public void getProducts(long userId) {
			mUserId = userId;
			mSubUrl = "user/product/user/%d";
			String url = mBaseUrl + String.format(mSubUrl + "?pageSize=%d&lastId=%d", userId, mPageSize, mLastId);
			doGetProducts(url);
		}

		public void doGetProducts(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USER_LATTICE_PRODUCT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseProducts(json);
			return true;
		}

		/**
		 * @Title: parseProducts
		 * @Description: 解析数据包得到格子铺物品信息
		 * @param json
		 * @return
		 */
		private ArrayList<LatticeProduct> parseProducts(String json) {
			ArrayList<LatticeProduct> productList = new ArrayList<LatticeProduct>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								LatticeProduct product = LatticeProduct.parse(item);
								if (product != null) {
									productList.add(product);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (JSONException ex) {
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
			}
		}
	}

	/**
	 * @ClassName: GetLatticeProductProtocol
	 * @Description: 获取格子铺物品信息协议
	 * @author liubingsr
	 * @date 2012-8-21 下午3:29:38
	 * 
	 */
	public final static class GetLatticeProductProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetLatticeProductProtocol";
		private long mProductId;
		private LatticeProduct mProduct;

		public GetLatticeProductProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mProduct = null;
		}

		/**
		 * @Title: getProduct
		 * @Description: 服务端返回的格子铺物品
		 * @return null 没有数据
		 */
		public LatticeProduct getProduct() {
			return mProduct;
		}

		public void getLatticeProduct(long productId) {
			mProductId = productId;
			mSubUrl = "user/product/%d";
			String url = mBaseUrl + String.format(mSubUrl, mProductId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USER_LATTICE_PRODUCT_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mProduct = LatticeProduct.parse(objArray.getJSONObject(0));
							if (mProduct != null) {
								mProduct.setId(mProductId);
								return true;
							}
						} else {
							// 没有数据
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * @ClassName: DeleteLatticeProductProtocol
	 * @Description: 删除格子铺物品协议
	 * @author liubingsr
	 * @date 2012-8-21 下午3:00:24
	 * 
	 */
	public final static class DeleteLatticeProductProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteLatticeProductProtocol";
		private long mProductId = 0;

		public DeleteLatticeProductProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: deleteProduct
		 * @Description: 删除物品
		 * @param productId
		 *            要删除的物品id
		 * @return void
		 */
		public void deleteProduct(long productId) {
			mSubUrl = "user/product/%d";
			mProductId = productId;
			String url = mBaseUrl + String.format(mSubUrl, mProductId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_USER_LATTICE_PRODUCT_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
						}
						return true;
					}
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// 删除本地缓存数据
			UserService.getInstance(mContext).deleteCahceLatticeProduct(mProductId);
		}
	}

	/**
	 * @ClassName: AddLatticeProductProtocol
	 * @Description: 添加格子铺物品协议
	 * @author liubingsr
	 * @date 2012-8-21 下午2:38:45
	 * 
	 */
	public final static class AddLatticeProductProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddLatticeProductProtocol";
		private LatticeProduct mProduct;
		private static Gson gson = null;
		static {
			if (gson == null) {
				gson = new Gson();
			}
		}

		public AddLatticeProductProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mProduct = null;
		}

		/**
		 * @Title: addProduct
		 * @Description: 添加格子铺物品
		 * @param product
		 *            物品
		 * @return void
		 */
		public void addProduct(LatticeProduct product) {
			mProduct = product;
			mSubUrl = "user/product";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setCharset("utf-8");

			FormFile formFile = new FormFile(product.getImageUrl(), "");
			info.addUploadFile(formFile);

			VoLatticeProduct vo = new VoLatticeProduct();
			vo.setName(product.getTitle());
			vo.setDescription(product.getDescription());
			vo.setPrice(Double.parseDouble(product.getPrice()));
			vo.setCount(product.getAmount());
			vo.setDealType(product.getTradeMode());

			String json = gson.toJson(vo);
			info.addFormFieldParam(JSON_FIELD, json);

			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_USER_LATTICE_PRODUCT_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject productJson = objArray.getJSONObject(0);
							mProduct.setId(productJson.getLong("id"));
							mProduct.setImageUrl(productJson.getString("imageUrl"));
							return true;
						}
					}
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (mProduct != null) {
				// 更新本地缓存
				UserService.getInstance(mContext).addCacheProduct(mProduct);
			}
		}
	}

	public final static class UpdateLatticeProductProtocol extends ProtocolHandlerBase {
		private final static String TAG = "UpdateLatticeProductProtocol";
		private LatticeProduct mProduct;
		private static Gson gson = null;
		static {
			if (gson == null) {
				gson = new Gson();
			}
		}

		public UpdateLatticeProductProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mProduct = null;
		}

		/**
		 * @Title: updateProduct
		 * @Description: 修改格子铺物品
		 * @param product
		 *            物品
		 * @return void
		 */
		public void updateProduct(LatticeProduct product) {
			mProduct = product;
			mSubUrl = "user/product/put";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setCharset("utf-8");

			if (!TextUtils.isEmpty(product.getImageUrl())) {
				FormFile formFile = new FormFile(product.getImageUrl(), "");
				info.addUploadFile(formFile);
			}

			VoLatticeProduct vo = new VoLatticeProduct();
			vo.setId(product.getId());
			vo.setName(product.getTitle());
			vo.setDescription(product.getDescription());
			vo.setPrice(Double.parseDouble(product.getPrice()));
			vo.setCount(product.getAmount());
			vo.setDealType(product.getTradeMode());

			String json = gson.toJson(vo);
			info.addFormFieldParam(JSON_FIELD, json);

			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.UPDATE_USER_LATTICE_PRODUCT_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject productJson = objArray.getJSONObject(0);
//							mProduct.setId(productJson.getLong("id"));
							mProduct.setImageUrl(productJson.getString("imageUrl"));
							return true;
						}
					}
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (mProduct != null) {
				// 更新本地缓存
				UserService.getInstance(mContext).addCacheProduct(mProduct);
			}
		}
	}

	public final static class GetAlbumImageListProtocol extends ListProtocolHandlerBase<UserAlbumImage> {
		private final static String TAG = "GetAlbumImageListProtocol";
		private long mUserId = 0;

		public GetAlbumImageListProtocol(Context context, boolean refreshed, RefreshListData<UserAlbumImage> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getImageList
		 * @Description: 获取图片列表
		 * @param userId
		 *            用户id
		 * @return void
		 */
		public void getImageList(long userId) {
			mUserId = userId;
			mSubUrl = "user/album/image/user/%d?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, userId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USER_ALBUM_IMAGE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseAlbumImage(json);
			return true;
		}

		/**
		 * @Title: parseAlbumImage
		 * @Description: 解析数据包得到图片信息
		 * @param json
		 * @return ArrayList<UserAlbumImage>
		 */
		private ArrayList<UserAlbumImage> parseAlbumImage(String json) {
			ArrayList<UserAlbumImage> imageList = new ArrayList<UserAlbumImage>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								UserAlbumImage image = UserAlbumImage.parse(item);
								if (image != null) {
									imageList.add(image);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return imageList;
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
	 * @ClassName: UploadAlbumImageProtocol
	 * @Description: 上传相册图片协议
	 * @author liubingsr
	 * @date 2012-8-18 下午4:46:45
	 * 
	 */
	public final static class UploadAlbumImageProtocol extends ProtocolHandlerBase {
		private final static String TAG = "UploadAlbumImageProtocol";
		private long mUserId = 0;
		private UserAlbumImage mUserAlbumImage;
		private static Gson gson = null;
		static {
			if (gson == null) {
				gson = new Gson();
			}
		}

		public UploadAlbumImageProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mUserAlbumImage = null;
		}

		/**
		 * @Title: uploadImage
		 * @Description: 上传相册图片
		 * @param userId
		 *            用户id
		 * @param imagePath
		 *            图片路径
		 * @return void
		 */
		public void uploadImage(long userId, UserAlbumImage image) {
			mSubUrl = "user/album/image";
			mUserId = userId;
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setCharset("utf-8");

			VoAlbumImage vo = new VoAlbumImage();
			vo.setDescription(image.getTitle());
			String json = gson.toJson(vo);
			info.addFormFieldParam(JSON_FIELD, json);

			FormFile formFile = new FormFile(image.getImageUrl(), "");
			info.addUploadFile(formFile);
			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.UPLOAD_USER_ALBUM_IMAGE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject imageJson = objArray.getJSONObject(0);
							if (imageJson != null) {
								mUserAlbumImage = new UserAlbumImage();
								long imageId = imageJson.getLong("id");
								String imageUrl = imageJson.getString("imageUrl");
								mUserAlbumImage.setId(imageId);
								mUserAlbumImage.setUserId(mUserId);
								mUserAlbumImage.setTitle(imageJson.getString("description"));
								mUserAlbumImage.setImageUrl(imageUrl);
							}
						}
					}
					return true;
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (mUserAlbumImage != null) {
				// 加入本地缓存
				UserService.getInstance(mContext).addAlbumImage(mUserId, mUserAlbumImage);
			}
		}
	}

	/**
	 * @ClassName: DeleteAlbumImageProtocol
	 * @Description: 删除用户相册图片协议
	 * @author liubingsr
	 * @date 2012-8-18 下午3:48:56
	 * 
	 */
	public final static class DeleteAlbumImageProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteAlbumImageProtocol";
		private long mImageId = 0;

		public DeleteAlbumImageProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: deleteImage
		 * @Description: 删除相册图片
		 * @param imageId
		 *            要删除的图片id
		 * @return void
		 */
		public void deleteImage(long imageId) {
			mSubUrl = "user/album/image/%d";
			mImageId = imageId;
			String url = mBaseUrl + String.format(mSubUrl, mImageId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_USER_ALBUM_IMAGE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
						}
						return true;
					}
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// 删除本地缓存数据
			UserService.getInstance(mContext).deleteCahceImage(mImageId);
		}
	}

	public final static class GetUserHavingsProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetUserHavingsProtocol";
		private long mUserId;
		private UserHavings mUserHavings;

		public GetUserHavingsProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mUserHavings = null;
		}

		/**
		 * @Title: getUserHavings
		 * @Description: 服务端返回的统计信息
		 * @return null 没有数据
		 */
		public UserHavings getUserHavings() {
			return mUserHavings;
		}

		public void getUserHavings(long userId) {
			mUserId = userId;
			mSubUrl = "user/mixQuantity/%d";
			String url = mBaseUrl + String.format(mSubUrl, mUserId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USER_HAVINGS_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mUserHavings = UserHavings.parse(objArray.getJSONObject(0));
							if (mUserHavings != null) {
								return true;
							}
						} else {
							// 没有数据
						}
					} else {
						// 没有数据
					}
				} else {
					Logger.getInstance(TAG).debug("数据包:<" + json + ">");
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub
		}
	}
}
