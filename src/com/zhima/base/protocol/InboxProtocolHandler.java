package com.zhima.base.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoContent;
import com.zhima.base.protocol.vo.VoIdsList;
import com.zhima.data.model.MessageDigest;
import com.zhima.data.model.MessageEntry;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.InboxService;

/**
 * @ClassName InboxProtocolHandler
 * @Description inbox相关的协议
 * @author jiang
 * @date 2013-1-22 下午10:16:14
 */
public final class InboxProtocolHandler {

	private static Gson gson = null;
	static {
		if (gson == null) {
			gson = new Gson();
		}
	}

	/**
	 * @ClassName GetMessageListProtocol
	 * @Description 获取消息列表
	 * @author jiang
	 * @date 2013-1-22 下午08:09:41
	 */
	public final static class GetMessageListProtocol extends ListProtocolHandlerBase<MessageDigest> {
		private final static String TAG = "GetMessageListProtocol";

		public GetMessageListProtocol(Context context, boolean refreshed, RefreshListData<MessageDigest> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			// TODO Auto-generated constructor stub
		}

		public void getMessageList() {
			mSubUrl = "inbox/message?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mPageSize, mLastId);
			doGetMessageList(url);
		}

		private void doGetMessageList(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_MESSAGE_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub\
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseMessageList(json);
			return true;
		}

		private ArrayList<MessageDigest> parseMessageList(String json) {
			ArrayList<MessageDigest> messageList = new ArrayList<MessageDigest>();

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
								MessageDigest messageDigest = MessageDigest.parse(item);
								if (messageDigest != null) {
									messageList.add(messageDigest);
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

			return messageList;
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
	 * @ClassName GetMessageCountProtocol
	 * @Description 获取未读消息个数
	 * @author jiang
	 * @date 2013-1-22 下午08:32:07
	 */
	public final static class GetMessageCountProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetMessageCountProtocol";
		/** 是否有未读消息 */
		private boolean isHasNew;
		/** 未读消息数量 */
		private int unReadCount;

		public GetMessageCountProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public void getMessageCount(long timestamp) {
			mSubUrl = "inbox/message/count?timestamp=%d";
			String url = mBaseUrl + String.format(mSubUrl, timestamp);
			doGetMessageCount(url);
		}
		
		public void getPingMessageCount(long timestamp) {
			mSubUrl = "system/ping?timestamp=%d";
			String url = mBaseUrl + String.format(mSubUrl, timestamp);
			doGetMessageCount(url);
		}
		
		private void doGetMessageCount(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_MESSAGE_COUNT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		public int getUnReadCount() {
			return unReadCount;
		}

		public boolean isHasNew() {
			return isHasNew;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							// mId = array.getJSONObject(0).getInt("id");
							isHasNew = array.getJSONObject(0).getBoolean("hasNew");
							unReadCount = array.getJSONObject(0).getInt("unreadCount");
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
	 * @ClassName GetMessageListByTypeProtocol
	 * @Description 获取特定类型的消息列表
	 * @author jiang
	 * @date 2013-1-22 下午08:55:41
	 */
	public final static class GetMessageListByTypeProtocol extends ListProtocolHandlerBase<MessageEntry> {
		private final static String TAG = "GetMessageListByTypeProtocol";

		public GetMessageListByTypeProtocol(Context context, boolean refreshed, RefreshListData<MessageEntry> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
			// TODO Auto-generated constructor stub
		}

		public void getMessageListByType(String messageType) {
			mSubUrl = "inbox/message/%s?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, messageType, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_MESSAGE_LIST_BY_TYPE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub\
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseMessageListByType(json);
			return true;
		}

		private ArrayList<MessageEntry> parseMessageListByType(String json) {
			ArrayList<MessageEntry> messageList = new ArrayList<MessageEntry>();

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
								MessageEntry messageEntry = MessageEntry.parse(item);
								if (messageEntry != null) {
									messageList.add(messageEntry);
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

			return messageList;
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
	 * @ClassName GetFriendTalkListByUserProtocol
	 * @Description 查询当前用户与某好友（用户）的对话列表
	 * @author jiang
	 * @date 2013-1-22 下午09:13:54
	 */
	public final static class GetFriendTalkListByUserProtocol extends ListProtocolHandlerBase<MessageEntry> {
		private final static String TAG = "GetFriendTalkListByUserProtocol";

		public GetFriendTalkListByUserProtocol(Context context, boolean refreshed, RefreshListData<MessageEntry> data,
				IHttpRequestCallback callBack) {			
			super(context, refreshed, data, callBack);
			mDataList.setType(RefreshListData.TYPE_GET_PREVIOUS_PAGE);
			// TODO Auto-generated constructor stub
		}

		public void getFriendTalkListByUser(long userId, long timestamp) {
			mSubUrl = "inbox/message/friend/%d?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, userId, mPageSize, mLastId);
			doGetFriendTalkListByUser(url);
		}

		private void doGetFriendTalkListByUser(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_FRIENDTALK_LIST_BY_USER_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub\
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseMessageList(json);
			return true;
		}

		private ArrayList<MessageEntry> parseMessageList(String json) {
			ArrayList<MessageEntry> messageList = new ArrayList<MessageEntry>();

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
								MessageEntry messageEntry = MessageEntry.parse(item);
								if (messageEntry != null) {
									messageList.add(messageEntry);
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

			return messageList;
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
	 * @ClassName AddFriendTalkProtocol
	 * @Description 向好友（用户）发送站内信
	 * @author jiang
	 * @date 2013-1-22 下午09:22:30
	 */
	public final static class AddFriendTalkProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddFriendTalkProtocol";

		public AddFriendTalkProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public void addFriendTalk(long friendId, String content) {
			mSubUrl = "inbox/message/friend/%d";
			String url = mBaseUrl + String.format(mSubUrl, friendId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);

			VoContent vo = new VoContent();
			vo.setContent(content);

			String json = gson.toJson(vo);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_FRIEND_TALK_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {

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

	/**
	 * @ClassName HandleFriendRelationshipProtocol
	 * @Description 接受或忽略好友请求
	 * @author jiang handleFriendRelationship
	 * @date 2013-1-23 上午10:43:55
	 */
	public final static class HandleFriendRelationshipProtocol extends ProtocolHandlerBase {

		private final static String TAG = "HandleFriendRelationshipProtocol";
		private boolean isUpdated;

		public HandleFriendRelationshipProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void handleFriendRelationship(long requestId, long messageId, int targetStatus) {
			mSubUrl = "inbox/message/handle/%d?messageId=%d&targetStatus=%d";
			String url = mBaseUrl + String.format(mSubUrl, requestId, messageId, targetStatus);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);

//			VoHandleFriendRelation vo = new VoHandleFriendRelation();
//			vo.setMessageId(messageId);
//			vo.setTargetStatus(targetStatus);
//
//			String json = gson.toJson(vo);
//			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.HANDLE_FRIEND_RELATIONSHIP_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isUpdated() {
			return isUpdated;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isUpdated = array.getBoolean(0);
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

	/**
	 * @ClassName MarkReadMessageByTypeProtocol
	 * @Description 批量将特定类型的未读消息设为已读
	 * @author jiang
	 * @date 2013-1-23 上午11:06:46
	 */
	public final static class MarkReadMessageByTypeProtocol extends ProtocolHandlerBase {

		private final static String TAG = "MarkReadMessageByTypeProtocol";
		private boolean isMarked;

		public MarkReadMessageByTypeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void markReadMessageByType(String messageType, List<Long> idsList) {
			mSubUrl = "inbox/message/%s/batch_mark_read";
			String url = mBaseUrl + String.format(mSubUrl, messageType);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);

			VoIdsList vo = new VoIdsList();
			vo.setIds(idsList);

			String json = gson.toJson(vo);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.MARK_READ_ALLMESSAGE_BY_TYPE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isMarked() {
			return isMarked;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isMarked = array.getBoolean(0);
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

	/**
	 * @ClassName MarkReadAllMessageByTypeProtocol
	 * @Description 将特定类型的未读消息全部设为已读
	 * @author jiang
	 * @date 2013-1-23 上午11:06:46
	 */
	public final static class MarkReadAllMessageByTypeProtocol extends ProtocolHandlerBase {

		private final static String TAG = "MarkReadAllMessageByTypeProtocol";
		private boolean isMarked;

		public MarkReadAllMessageByTypeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void markReadAllMessageByType(String messageType) {
			mSubUrl = "inbox/message/%s/mark_read";
			String url = mBaseUrl + String.format(mSubUrl, messageType);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.PUT);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.MARK_READ_ALLMESSAGE_BY_TYPE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isMarked() {
			return isMarked;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isMarked = array.getBoolean(0);
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

	/**
	 * @ClassName MarkReadLLFriendTalkByUserProtocol
	 * @Description 将未读的某一好友的对话消息全部设为已读
	 * @author jiang
	 * @date 2013-1-24 上午10:40:14
	 */
	public final static class MarkReadLLFriendTalkByUserProtocol extends ProtocolHandlerBase {

		private final static String TAG = "MarkReadLLFriendTalkByUserProtocol";
		private boolean isMarked;

		public MarkReadLLFriendTalkByUserProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void markReadLLFriendTalkByUser(long userId) {
			mSubUrl = "inbox/message/friend/%d/mark_read";
			String url = mBaseUrl + String.format(mSubUrl, userId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.PUT);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.MARK_READ_LLFRIENDTALK_BY_USER_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isMarked() {
			return isMarked;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isMarked = array.getBoolean(0);
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

	/**
	 * @ClassName DeleteMessageByTypeProtocol
	 * @Description 单个删除特定类型的消息
	 * @author jiang
	 * @date 2013-1-24 上午10:53:06
	 */
	public final static class DeleteMessageByTypeProtocol extends ProtocolHandlerBase {

		private final static String TAG = "DeleteMessageByTypeProtocol";
		private boolean isMessageByTypeDeleted;
		private String mMessageType = "";

		public DeleteMessageByTypeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void deleteMessageByType(String messageType, long targetId) {
			mSubUrl = "inbox/message/%s/delete?targetId=%d";
			String url = mBaseUrl + String.format(mSubUrl, messageType, targetId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_MESSAGE_BY_TYPE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isMessageByTypeDeleted() {
			return isMessageByTypeDeleted;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isMessageByTypeDeleted = array.getBoolean(0);
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
			InboxService.getInstance(mContext).deleteMessageListByTypeCache(mMessageType);
		}

	}

	/**
	 * @ClassName DeleteAllMessageByTypeProtocol
	 * @Description 清空特定类型的消息
	 * @author jiang
	 * @date 2013-1-24 上午11:10:45
	 */
	public final static class DeleteAllMessageByTypeProtocol extends ProtocolHandlerBase {

		private final static String TAG = "DeleteAllMessageByTypeProtocol";
		private boolean isAllMessageByTypeDeleted;
		private String mMessageType = "";

		public DeleteAllMessageByTypeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void deleteAllMessageByType(String messageType) {
			mSubUrl = "inbox/message/%s";
			mMessageType = messageType;
			String url = mBaseUrl + String.format(mSubUrl, messageType);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_ALL_MESSAGE_BY_TYPE_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isAllMessageByTypeDeleted() {
			return isAllMessageByTypeDeleted;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isAllMessageByTypeDeleted = array.getBoolean(0);
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
			InboxService.getInstance(mContext).deleteMessageListByTypeCache(mMessageType);
		}

	}

	/**
	 * @ClassName DeleteFriendTalkByUserProtocol
	 * @Description 单个删除与某好友（用户）的对话消息
	 * @author jiang
	 * @date 2013-1-24 上午11:20:16
	 */
	public final static class DeleteFriendTalkByUserProtocol extends ProtocolHandlerBase {

		private final static String TAG = "DeleteFriendTalkByUserProtocol";
		private boolean isFriendTalkByUserDeleted;
		private long mUserId;

		public DeleteFriendTalkByUserProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void deleteFriendTalkByUser(long userId, long targetId) {
			mUserId = userId;
			mSubUrl = "inbox/message/friend/delete?userId=%d&targetId=%d";
			String url = mBaseUrl + String.format(mSubUrl, userId, targetId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_FRIEND_TALK_BY_USER_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isFriendTalkByUserDeleted() {
			return isFriendTalkByUserDeleted;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isFriendTalkByUserDeleted = array.getBoolean(0);
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
			InboxService.getInstance(mContext).deleteCacheFriendTalkListByUser(mUserId);
		}

	}

	/**
	 * @ClassName DeleteAllFriendTalkByUserProtocol
	 * @Description 清空与某好友（用户）的对话消息
	 * @author jiang
	 * @date 2013-1-24 上午11:28:26
	 */
	public final static class DeleteAllFriendTalkByUserProtocol extends ProtocolHandlerBase {

		private final static String TAG = "DeleteAllFriendTalkByUserProtocol";
		private boolean isAllFriendTalkDeleted;
		private long mUserId;

		public DeleteAllFriendTalkByUserProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			// TODO Auto-generated constructor stub
		}

		public void deleteAllFriendTalkByUser(long userId) {
			mUserId = userId;
			mSubUrl = "inbox/message/friend/%d";
			String url = mBaseUrl + String.format(mSubUrl, userId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_ALL_FRIEND_TALK_BY_USER_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		public boolean isAllFriendTalkDeleted() {
			return isAllFriendTalkDeleted;
		}

		@Override
		public boolean parse() {
			// TODO Auto-generated method stub
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							isAllFriendTalkDeleted = array.getBoolean(0);
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
			InboxService.getInstance(mContext).deleteCacheFriendTalkListByUser(mUserId);
		}

	}

}
