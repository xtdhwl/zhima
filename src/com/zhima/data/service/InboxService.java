package com.zhima.data.service;

import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.InboxProtocolHandler.AddFriendTalkProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.DeleteAllFriendTalkByUserProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.DeleteAllMessageByTypeProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.DeleteFriendTalkByUserProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.DeleteMessageByTypeProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.GetFriendTalkListByUserProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.GetMessageCountProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.GetMessageListByTypeProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.GetMessageListProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.HandleFriendRelationshipProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.MarkReadAllMessageByTypeProtocol;
import com.zhima.base.protocol.InboxProtocolHandler.MarkReadLLFriendTalkByUserProtocol;
import com.zhima.data.model.MessageDigest;
import com.zhima.data.model.MessageEntry;
import com.zhima.data.model.RefreshListData;

/**
 * @ClassName InboxService
 * @Description inbox相关的service
 * @author jiang
 * @date 2013-1-24 上午11:34:39
 */
public class InboxService extends BaseService {

	private static InboxService mInstance = null;

	private TreeMap<Long, RefreshListData<MessageDigest>> mMessageListMap;
	private TreeMap<Long, RefreshListData<MessageEntry>> mFriendTalkListByUserMap;
	private TreeMap<String, RefreshListData<MessageEntry>> mMessageListMapByType;

	public InboxService(Context context) {
		super(context);
		onCreate();
	}

	public static InboxService getInstance(Context mContext) {
		if (mInstance == null) {
			mInstance = new InboxService(mContext);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mMessageListMap = new TreeMap<Long, RefreshListData<MessageDigest>>();
		mMessageListMapByType = new TreeMap<String, RefreshListData<MessageEntry>>();
		mFriendTalkListByUserMap = new TreeMap<Long, RefreshListData<MessageEntry>>();
	}

	/**
	 * @Title: getCacheMessageList
	 * @Description:获取当前用户的综合消息列表缓存
	 * @param
	 * @return RefreshListData<MessageDigest>
	 */
	public RefreshListData<MessageDigest> getCacheMessageList() {
		long userId = AccountService.getInstance(mContext).getUserId();
		if (mMessageListMap.containsKey(userId)) {
			return mMessageListMap.get(userId);
		} else {
			return new RefreshListData<MessageDigest>();
		}
	}

	/**
	 * @Title: deleteMessageListCache
	 * @Description:删除登录用户的消息列表缓存
	 * @param
	 * @return void
	 */
	public void deleteMessageListCache() {
		long userId = AccountService.getInstance(mContext).getUserId();
		if (mMessageListMap != null) {
			mMessageListMap.remove(userId);
		}
	}

	/**
	 * @Title: getMessageList
	 * @Description:获取当前用户的综合消息列表
	 * @param
	 * @return void
	 */
	public void getMessageList(boolean isRefresh, IHttpRequestCallback callBack) {
		long userId = AccountService.getInstance(mContext).getUserId();
		RefreshListData<MessageDigest> list = null;
		if (!mMessageListMap.containsKey(userId)) {
			list = new RefreshListData<MessageDigest>();
			mMessageListMap.put(userId, list);
		} else {
			list = mMessageListMap.get(userId);
		}

		GetMessageListProtocol protocol = new GetMessageListProtocol(mContext, isRefresh, list, callBack);
		protocol.getMessageList();
	}

	/**
	 * @Title: getCacheMessageListByType
	 * @Description:获取特定类型消息列表缓存
	 * @param
	 * @return RefreshListData<MessageEntry>
	 */
	public RefreshListData<MessageEntry> getCacheMessageListByType(String messageType) {
		if (mMessageListMapByType.containsKey(messageType)) {
			return mMessageListMapByType.get(messageType);
		} else {
			return new RefreshListData<MessageEntry>();
		}
	}

	/**
	 * @Title: deleteMessageListCache
	 * @Description:删除特定类型消息列表缓存
	 * @param
	 * @return void
	 */
	public void deleteMessageListByTypeCache(String messageType) {
		if (mMessageListMapByType != null) {
			mMessageListMapByType.remove(messageType);
		}
	}

	/**
	 * @Title: getMessageListByType
	 * @Description:获取特定类型消息列表
	 * @param
	 * @return void
	 */
	public void getMessageListByType(String messageType, boolean isRefresh, IHttpRequestCallback callBack) {
		RefreshListData<MessageEntry> list = null;
		if (!mMessageListMapByType.containsKey(messageType)) {
			list = new RefreshListData<MessageEntry>();
			mMessageListMapByType.put(messageType, list);
		} else {
			list = mMessageListMapByType.get(messageType);
		}

		GetMessageListByTypeProtocol protocol = new GetMessageListByTypeProtocol(mContext, isRefresh, list, callBack);
		protocol.getMessageListByType(messageType);
	}

	/**
	 * @Title: getMessageCount
	 * @Description:获取未读消息个数
	 * @param
	 * @return void
	 */
	public void getMessageCount(long timestamp, IHttpRequestCallback callBack) {
		GetMessageCountProtocol protocol = new GetMessageCountProtocol(mContext, callBack);
		protocol.getMessageCount(timestamp);
	}
	/**
	* @Title: getPingMessageCount
	* @Description: 由ping服务获取未读消息条数
	* @param timestamp 上次请求接口时间
	* @param callBack
	* @return void
	*/
	public void getPingMessageCount(long timestamp, IHttpRequestCallback callBack) {
		GetMessageCountProtocol protocol = new GetMessageCountProtocol(mContext, callBack);
		protocol.getMessageCount(timestamp);
	}
	
	/**
	 * @Title: getCacheFriendTalkListByUser
	 * @Description:查询当前用户与某好友（用户）的对话列表缓存
	 * @param
	 * @return RefreshListData<MessageEntry>
	 */
	public RefreshListData<MessageEntry> getCacheFriendTalkListByUser(long userId) {
		if (mFriendTalkListByUserMap.containsKey(userId)) {
			return mFriendTalkListByUserMap.get(userId);
		} else {
			return new RefreshListData<MessageEntry>();
		}
	}

	/**
	 * @Title: deleteCacheFriendTalkListByUser
	 * @Description:delete当前用户与某好友（用户）的对话列表缓存
	 * @param
	 * @return void
	 */
	public void deleteCacheFriendTalkListByUser(long userId) {
		if (mFriendTalkListByUserMap != null) {
			mFriendTalkListByUserMap.remove(userId);
		}
	}

	/**
	 * @Title: getFriendTalkListByUser
	 * @Description:查询当前用户与某好友（用户）的对话列表
	 * @param
	 * @return void
	 */
	public void getFriendTalkListByUser(long userId, long timestamp, boolean isRefresh, IHttpRequestCallback callBack) {
		RefreshListData<MessageEntry> list = null;
		if (!mFriendTalkListByUserMap.containsKey(userId)) {
			list = new RefreshListData<MessageEntry>();
			mFriendTalkListByUserMap.put(userId, list);
		} else {
			list = mFriendTalkListByUserMap.get(userId);
		}

		GetFriendTalkListByUserProtocol protocol = new GetFriendTalkListByUserProtocol(mContext, isRefresh, list,
				callBack);
		protocol.getFriendTalkListByUser(userId, timestamp);
	}

	/**
	 * @Title: addFriendTalk
	 * @Description:向好友（用户）发送站内信
	 * @param
	 * @return void
	 */
	public void addFriendTalk(long friendId, String content, IHttpRequestCallback callBack) {
		AddFriendTalkProtocol protocal = new AddFriendTalkProtocol(mContext, callBack);
		protocal.addFriendTalk(friendId, content);
	}

	/**
	 * @Title: handleFriendRelationship
	 * @Description: 接受或忽略好友请求
	 * @param
	 * @return void
	 */
	public void handleFriendRelationship(long requestId, long messageId, int targetStatus, IHttpRequestCallback callBack) {
		HandleFriendRelationshipProtocol protocal = new HandleFriendRelationshipProtocol(mContext, callBack);
		protocal.handleFriendRelationship(requestId, messageId, targetStatus);
	}

	/**
	 * @Title: markReadAllMessageByType
	 * @Description:将特定类型的未读消息全部设为已读
	 * @param
	 * @return void
	 */
	public void markReadAllMessageByType(String messageType, IHttpRequestCallback callBack) {
		MarkReadAllMessageByTypeProtocol protocal = new MarkReadAllMessageByTypeProtocol(mContext, callBack);
		protocal.markReadAllMessageByType(messageType);
	}

	/**
	 * @Title: markReadLLFriendTalkByUser
	 * @Description:TODO将未读的某一好友的对话消息全部设为已读
	 * @param
	 * @return void
	 */
	public void markReadLLFriendTalkByUser(long userId, IHttpRequestCallback callBack) {
		MarkReadLLFriendTalkByUserProtocol protocal = new MarkReadLLFriendTalkByUserProtocol(mContext, callBack);
		protocal.markReadLLFriendTalkByUser(userId);
	}

	/**
	 * @Title: deleteMessageByType
	 * @Description:单个删除特定类型的消息
	 * @param
	 * @return void
	 */
	public void deleteMessageByType(String messageType, long targetId, IHttpRequestCallback callBack) {
		DeleteMessageByTypeProtocol protocal = new DeleteMessageByTypeProtocol(mContext, callBack);
		protocal.deleteMessageByType(messageType, targetId);
	}

	/**
	 * @Title: deleteAllMessageByType
	 * @Description:清空特定类型的消息
	 * @param
	 * @return void
	 */
	public void deleteAllMessageByType(String messageType, IHttpRequestCallback callBack) {
		DeleteAllMessageByTypeProtocol protocal = new DeleteAllMessageByTypeProtocol(mContext, callBack);
		protocal.deleteAllMessageByType(messageType);
	}

	/**
	 * @Title: deleteFriendTalkByUser
	 * @Description:单个删除与某好友（用户）的对话消息
	 * @param
	 * @return void
	 */
	public void deleteFriendTalkByUser(long userId, long targetId, IHttpRequestCallback callBack) {
		DeleteFriendTalkByUserProtocol protocal = new DeleteFriendTalkByUserProtocol(mContext, callBack);
		protocal.deleteFriendTalkByUser(userId, targetId);
	}

	/**
	 * @Title: deleteAllFriendTalkByUser
	 * @Description:清空与某好友（用户）的对话消息
	 * @param
	 * @return void
	 */
	public void deleteAllFriendTalkByUser(long userId, IHttpRequestCallback callBack) {
		DeleteAllFriendTalkByUserProtocol protocal = new DeleteAllFriendTalkByUserProtocol(mContext, callBack);
		protocal.deleteAllFriendTalkByUser(userId);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (mMessageListMap != null) {
			mMessageListMap.clear();
		}
		if (mFriendTalkListByUserMap != null) {
			mFriendTalkListByUserMap.clear();
		}
		if (mMessageListMapByType != null) {
			mMessageListMapByType.clear();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mMessageListMap = null;
		mFriendTalkListByUserMap = null;
		mMessageListMapByType = null;
	}

}
