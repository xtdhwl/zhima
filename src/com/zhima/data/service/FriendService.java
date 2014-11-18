package com.zhima.data.service;

import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.FriendsProtocolHandler.ApplicationFriendProtocol;
import com.zhima.base.protocol.FriendsProtocolHandler.CheckFriendProtocol;
import com.zhima.base.protocol.FriendsProtocolHandler.DeleteFriendProtocol;
import com.zhima.base.protocol.FriendsProtocolHandler.GetFriendsListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;

/**
 * @ClassName FriendService
 * @Description 好友相关的service
 * @author jiang
 * @date 2013-1-24 上午11:33:28
 */
public class FriendService extends BaseService {

	private static FriendService mInstance = null;

	private RefreshListData<User> mFriendsList;
	private TreeMap<Long, RefreshListData<User>> mFriendsListMap;

	public FriendService(Context context) {
		super(context);
		onCreate();
	}

	public static FriendService getInstance(Context mContext) {
		if (mInstance == null) {
			mInstance = new FriendService(mContext);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mFriendsList = new RefreshListData<User>();
		mFriendsListMap = new TreeMap<Long, RefreshListData<User>>();
	}

	/**
	 * @Title: getMyCacheFriendsList
	 * @Description:获取登陆用户的好友列表缓存
	 * @param
	 * @return RefreshListData<User>
	 */
	public RefreshListData<User> getCacheMyFriendsList() {
		long userId = AccountService.getInstance(mContext).getUserId();
		if (mFriendsListMap.containsKey(userId)) {
			return mFriendsListMap.get(userId);
		} else {
			return new RefreshListData<User>();
		}
	}

	/**
	 * @Title: getMyFriendsList
	 * @Description:获取登录用户的好友列表
	 * @param
	 * @return void
	 */
	public void getMyFriendsList(boolean isRefresh, IHttpRequestCallback callBack) {
		long userId = AccountService.getInstance(mContext).getUserId();
		RefreshListData<User> list = null;
		if (!mFriendsListMap.containsKey(userId)) {
			list = new RefreshListData<User>();
			mFriendsListMap.put(userId, list);
		} else {
			list = mFriendsListMap.get(userId);
		}

		GetFriendsListProtocol protocol = new GetFriendsListProtocol(mContext, isRefresh, list, callBack);
		protocol.getFriendsList();
	}

	/**
	 * @Title: getCacheFriendsList
	 * @Description:获取特定用户的好友列表的缓存
	 * @param
	 * @return RefreshListData<User>
	 */
	public RefreshListData<User> getCacheFriendsList(long userId) {
		if (mFriendsListMap.containsKey(userId)) {
			return mFriendsListMap.get(userId);
		} else {
			return new RefreshListData<User>();
		}
	}

	/**
	 * @Title: getFriendsList
	 * @Description:获取特定用户的好友列表
	 * @param
	 * @return void
	 */
	public void getFriendsList(long userId, boolean isRefresh, IHttpRequestCallback callBack) {
		RefreshListData<User> list = null;
		if (!mFriendsListMap.containsKey(userId)) {
			list = new RefreshListData<User>();
			mFriendsListMap.put(userId, list);
		} else {
			list = mFriendsListMap.get(userId);
		}

		GetFriendsListProtocol protocol = new GetFriendsListProtocol(mContext, isRefresh, list, callBack);
		protocol.getFriendsList(userId);
	}

	/**
	 * @Title: deleteFriendCache
	 * @Description:删除登录用户的好友列表缓存
	 * @param
	 * @return void
	 */
	public void deleteFriendCache(long friendUserId) {
		long userId = AccountService.getInstance(mContext).getUserId();
		if (mFriendsListMap != null && mFriendsListMap.containsKey(friendUserId)) {
			mFriendsListMap.get(userId).removeObject(friendUserId);
		}
	}

	/**
	 * @Title: applicationFriend
	 * @Description:申请好友
	 * @param
	 * @return void
	 */
	public void applicationFriend(long friendId, IHttpRequestCallback callBack) {
		ApplicationFriendProtocol protocol = new ApplicationFriendProtocol(mContext, callBack);
		protocol.applicationFriend(friendId);
	}

	/**
	 * @Title: deleteFriend
	 * @Description:删除好友
	 * @param
	 * @return void
	 */
	public void deleteFriend(long friendId, IHttpRequestCallback callBack) {
		DeleteFriendProtocol protocol = new DeleteFriendProtocol(mContext, callBack);
		protocol.deleteFriend(friendId);
	}

	/**
	 * @Title: checkFriend
	 * @Description:查询是否有好友关系
	 * @param
	 * @return void
	 */
	public void checkFriend(long friendId, IHttpRequestCallback callBack) {
		CheckFriendProtocol protocol = new CheckFriendProtocol(mContext, callBack);
		protocol.checkFriend(friendId);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (mFriendsList != null) {
			mFriendsList.clear();
		}
		if (mFriendsListMap != null) {
			mFriendsListMap.clear();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mFriendsList = null;
		mFriendsListMap = null;
	}

}
