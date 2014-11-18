/* 
 * @Title: UserProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.service.FriendService;

/**
 * @ClassName FriendsProtocolHandler
 * @Description 好友协议处理
 * @author jiang
 * @date 2013-1-17 上午11:29:20
 */
public final class FriendsProtocolHandler {

	/**
	 * @ClassName GetFriendsListProtocol
	 * @Description 获取好友列表（分页加载）
	 * @author jiang
	 * @date 2013-1-17 上午11:51:53
	 */
	public final static class GetFriendsListProtocol extends ListProtocolHandlerBase<User> {
		private final static String TAG = "GetFriendsListProtocol";

		public GetFriendsListProtocol(Context context, boolean refreshed, RefreshListData<User> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			// TODO Auto-generated constructor stub
		}

		public void getFriendsList() {
			mSubUrl = "friend/by_user/own?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mPageSize, mLastId);
			doGetFriendsList(url);
		}

		public void getFriendsList(long userId) {
			mSubUrl = "friend/by_user/%d?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, userId, mPageSize, mLastId);
			doGetFriendsList(url);
		}

		private void doGetFriendsList(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_FRIENDS_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub\
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parFriendsList(json);
			return true;
		}

		private ArrayList<User> parFriendsList(String json) {
			ArrayList<User> friendsList = new ArrayList<User>();

			try {
				JSONTokener jsonParser = new JSONTokener(json);
				JSONObject vo = (JSONObject) jsonParser.nextValue();
				ZMResponseHeader responseHeader = ZMResponseHeader.parse(vo);
				int status = responseHeader.getStatus();
				setProtocolStatus(status);
				if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!vo.isNull("items")) {
						JSONArray objArray = vo.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								User user = User.parse(item);
								if (user != null) {
									friendsList.add(user);
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
				} else if (status == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}

			return friendsList;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub
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
	 * @ClassName ApplicationFriendProtocol
	 * @Description 申请好友
	 * @author jiang
	 * @date 2013-1-17 下午01:59:49
	 */
	public final static class ApplicationFriendProtocol extends ProtocolHandlerBase {
		private final static String TAG = "ApplicationFriendProtocol";
		private long mId;

		public ApplicationFriendProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public void applicationFriend(long friendId) {
			mSubUrl = "friend/application/%d";
			String url = mBaseUrl + String.format(mSubUrl, friendId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.APPLICATION_FRIEND_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public long getResult() {
			return mId;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							 mId = array.getLong(0);
							return true;
						}
					} else {

					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", e);
			}

			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * @ClassName DeleteFriendProtocol
	 * @Description 删除好友
	 * @author jiang
	 * @date 2013-1-17 下午02:22:58
	 */
	public final static class DeleteFriendProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteFriendProtocol";
		private boolean isFriendDeleted = false;
		private long mFriendId;

		public DeleteFriendProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public void deleteFriend(long friendId) {
			mFriendId = friendId;
			mSubUrl = "friend/%d";
			String url = mBaseUrl + String.format(mSubUrl, friendId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_FRIEND_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isFriendDeleted() {
			return isFriendDeleted;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						isFriendDeleted = true;
//						JSONArray array = mResponeVO.getJSONArray("items");
//						if (array != null && array.length() > 0) {
//							isFriendDeleted = array.getBoolean(0);
//						}
						return true;
					} else {

					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", e);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub
			FriendService.getInstance(mContext).deleteFriendCache(mFriendId);
		}

	}

	/**
	 * @ClassName CheckFriendProtocol
	 * @Description 查询是否有好友关系
	 * @author jiang
	 * @date 2013-1-17 下午05:15:31
	 */
	public final static class CheckFriendProtocol extends ProtocolHandlerBase {
		private final static String TAG = "CheckFriendProtocol";
		private boolean isFriend = false;

		public CheckFriendProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public void checkFriend(long friendId) {
			mSubUrl = "friend/check/%d";
			String url = mBaseUrl + String.format(mSubUrl, friendId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.CHECK_FRIEND_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isFriend() {
			return isFriend;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isFriend = array.getBoolean(0);
						}
						return true;
					} else {

					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", e);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub

		}

	}
}
