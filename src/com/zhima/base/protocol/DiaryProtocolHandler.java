/* 
 * @Title: DiaryProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.network.uploadfile.FormFile;
import com.zhima.base.protocol.vo.VoDiary;
import com.zhima.base.protocol.vo.VoForwardDiary;
import com.zhima.base.protocol.vo.VoReplyDiaryBody;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMDiaryReply;
import com.zhima.data.service.DiaryService;
import com.zhima.data.service.UserService;
import com.zhima.ui.usercenter.activity.PersonalDataActivity;

/**
 * @ClassName: DiaryProtocolHandler
 * @Description: 用户日记协议
 * @author liubingsr
 * @date 2012-6-12 下午2:06:28
 * 
 */
public final class DiaryProtocolHandler {
	private final static String TAG = "DiaryProtocolHandler";

	private static Gson gson = null;
	static {
		if (gson == null) {
			gson = new Gson();
		}
	}

	public final static class NewDiaryProtocol extends ProtocolHandlerBase {
		private long mDiaryId;

		public NewDiaryProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mDiaryId = ZMConsts.INVALID_ID;
		}

		/**
		 * @Title: getDiaryId
		 * @Description: 发送成功，返回diaryId
		 * @return long
		 */
		public long getDiaryId() {
			return mDiaryId;
		}

		/**
		 * @Title: addDiary
		 * @Description: 发表带图片的日志
		 * @param title 标题
		 * @param content 内容
		 * @param imagePath 图片本地地址
		 * @param privateStatus 隐私设置：1(隐私) | 2(好友可见)
		 * @param syncList 需要同步到的空间,list中的值为空间分类id#芝麻空间id
		 */
		public void addDiary(String title, String content, String imagePath, int privacyStatus, List<String> syncList) {
			doAddDiary(title, content, imagePath, privacyStatus, syncList);
		}

		private void doAddDiary(String title, String content, String imagePath, int privacyStatus, List<String> syncList) {
			boolean hasImage = !TextUtils.isEmpty(imagePath);
			mSubUrl = "blog?privateStatus=%d";//"http://172.16.16.61:8080/rest/service/" 
			String url = mBaseUrl + String.format(mSubUrl, privacyStatus);
			RequestInfo info = new RequestInfo(url);
			mProtocolType = ProtocolType.DIARY_NEW_PROTOCOL;

			// 准备json数据
			VoDiary vo = new VoDiary();
			vo.setTitle(title);
			vo.setContent(content);
			vo.setPrivateStatus(privacyStatus);
			vo.setSyncList(syncList);

			String json = gson.toJson(vo);
			// 组织post数据包
			info.setCharset("utf-8");
			info.addFormFieldParam(JSON_FIELD, json);
			if (hasImage) {				
				FormFile formFile = new FormFile(imagePath, "");
				info.addUploadFile(formFile);				
			}
			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			Logger.getInstance(TAG).debug("正在发送日记数据：<" + json + ">");
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			boolean result = false;
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mDiaryId = objArray.getLong(0);
							result = true;
						}
					}
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return result;
		}

		@Override
		public void afterParse() {
			//TODO
			PersonalDataActivity.PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = true;
		}
	}

	/**
	 * @ClassName: getDiaryProtocol
	 * @Description: 通过日志id获取日志
	 * @author luqilong
	 * @date 2013-1-16 下午4:48:50
	 */
	public final static class GetDiaryProtocol extends ProtocolHandlerBase {
		private ZMDiary zmDiary = null;
		private boolean mIsSpace = false;
		private long mKeyId = 0;

		public GetDiaryProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public ZMDiary getDiary() {
			return zmDiary;
		}

		public void requestDiary(ZMDiary diary, long keyId) {
			mKeyId = keyId;
			mIsSpace = diary.getSyncId() > 0;
			if (mIsSpace) {
				mSubUrl = String.format("blog/zms/%d",diary.getSyncId());
			} else {
				mSubUrl = String.format("blog/%d",diary.getDiaryId());
			}
			String url = mBaseUrl + mSubUrl;
			doRequestDiary(url);
		}
//		public void requestZMSpaceDiary(long syncId) {
//			mSubUrl = "/blog/zms/%d";
//			String url = mBaseUrl + String.format(mSubUrl, syncId);
//			doRequestDiary(url);			
//		}		
		private void doRequestDiary(String url) {
			RequestInfo info = new RequestInfo(url);
			mProtocolType = ProtocolType.GET_DIARY_PROTOCOL;
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			boolean result = false;
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_DIARY_DELETED
						|| mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item = objArray.getJSONObject(0);
							zmDiary = ZMDiary.parse(item);
							result = true;
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return result;
		}

		@Override
		public void afterParse() {
			DiaryService.getInstance(mContext).addDiaryDetail2Cache(zmDiary, mKeyId);
		}
	}

	/**
	 * @ClassName: ReplyDiaryProtocol
	 * @Description: 日记回复和评论协议
	 * @author liubingsr
	 * @date 2012-7-6 上午9:20:05
	 * 
	 */
	public final static class ReplyDiaryProtocol extends ProtocolHandlerBase {
		private final static String TAG = "ReplyDiaryProtocol";

		public ReplyDiaryProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: replyDiary
		* @Description: 回复个人空间日志
		* @param diaryId 日志id
		* @param repliedUserId 被回复的人
		* @param content 回复内容
		* @return void
		*/
		public void replyDiary(long diaryId, long repliedUserId, String content, boolean isSpace) {
			doPost(isSpace ? "zms/" : "", repliedUserId, diaryId, content, false);
		}
		/**
		* @Title: commentDiary
		* @Description: 评论日志
		* @param diaryId 日志id
		* @param content 评论内容
		* @return void
		*/
		public void commentDiary(long diaryId, String content, boolean isSpace) {
			doPost(isSpace ? "zms/" : "", ZMConsts.INVALID_ID, diaryId, content, true);
		}
//		/**
//		* @Title: replyZMSpaceDiary
//		* @Description: 回复芝麻空间日志
//		* @param zmSpaceDiaryId 芝麻空间日志id
//		* @param repliedUserId 被回复的人
//		* @param content 回复内容
//		* @return void
//		*/
//		public void replyZMSpaceDiary(long zmSpaceDiaryId, long repliedUserId, String content) {
//			doPost("zms/", repliedUserId, zmSpaceDiaryId, content, false);
//		}
//		/**
//		* @Title: commentZMSpaceDiary
//		* @Description: 评论芝麻空间日志
//		* @param zmSpaceDiaryId 芝麻空间日志id
//		* @param content 评论内容
//		* @return void
//		*/
//		public void commentZMSpaceDiary(long zmSpaceDiaryId, String content) {
//			doPost("zms/", ZMConsts.INVALID_ID, zmSpaceDiaryId, content, true);
//		}
		/**
		 * @Title: doPost
		 * @Description: 发表评论或回复
		 * @param repliedId 被回复者id
		 * @param blogId 被回复日志id
		 * @param content 回复内容
		 * @param isComment 是否是评论(如果为true表示评论日志;如果为false则表示回复到人)
		 */
		private void doPost(String path, long repliedId, long diaryId, String content, boolean isComment) {
			mSubUrl = "blog/%s%d/reply?replyOrginal=%s";
			String url = mBaseUrl + String.format(mSubUrl, path, diaryId, isComment ? "true" : "false");
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			
			VoReplyDiaryBody vo = new VoReplyDiaryBody();
			vo.setContent(content);
			vo.setRepliedId(repliedId);
			//如果为true表示评论日志;如果为false则表示回复到人
			String json = gson.toJson(vo);
			info.setCharset("utf-8");
			info.addFormFieldParam(JSON_FIELD, json);
			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.COMMENT_DIARY_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						return true;
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			//TODO 
			PersonalDataActivity.PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = true;
		}
	}

	/**
	 * @ClassName: ForwardDirayProtocol
	 * @Description: 转发日志
	 * @author luqilong
	 * @date 2013-1-17 上午10:17:46
	 * 
	 */
	public final static class ForwardDirayProtocol extends ProtocolHandlerBase {
		private final static String TAG = "ForwardDirayProtocol";
		private long mDiaryId = ZMConsts.INVALID_ID;

		public ForwardDirayProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		public long getDiaryId() {
			return mDiaryId;
		}

		/**
		 * @Title: forward
		 * @Description: 转发日志
		 * @param blogId 被转发日志的id。如果被转发日志为原创，则blogId与rawBlogId相同，
		 *            如果被转发日志为转发日志则blogId与rawBlogId不同
		 * @param rawBlogId 原始日志id
		 * @param privateStatus 日志私密状态。1为未公开，即私密；2为公开
		 * @param title 标题
		 * @param syncList 需要同步到的空间,list中的值为空间分类id#芝麻空间id
		 */
		public void forward(long blogId, long rawBlogId, int privateStatus, String title, List<String> syncList, boolean isSpace) {
			if (isSpace) {
				mSubUrl = "blog/zms/quote/%d?privateStatus=%d";
			} else {
				mSubUrl = "blog/quote/%d?privateStatus=%d";
			}
			String url = mBaseUrl + String.format(mSubUrl, blogId, privateStatus);
			doForward(url, rawBlogId, title, syncList);
		}
//		
//		public void forwardZMSpace(long syncId, long rawBlogId, int privateStatus, String title, List<String> syncList) {
//			mSubUrl = "blog/zms/quote/%d?privateStatus=%d";
//			String url = mBaseUrl + String.format(mSubUrl, syncId, privateStatus);
//			doForward(url, rawBlogId, title, syncList);
//		}
		/**
		* @Title: doForward
		* @Description: 转发
		* @param url
		* @param rawBlogId
		* @param title
		* @param syncList
		* @return void
		*/
		private void doForward(String url, long rawBlogId, String title, List<String> syncList) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			
			VoForwardDiary vo = new VoForwardDiary();
			vo.setTitle(title);
			vo.setRawBlogId(rawBlogId);
			vo.setSyncList(syncList);

			String json = gson.toJson(vo);
//			info.setBody(json);
			info.setCharset("utf-8");
			info.addFormFieldParam(JSON_FIELD, json);
			info.setRequestType(RequestType.UPLOAD);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.FORWARD_DIARY_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			boolean result = false;
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mDiaryId = objArray.getLong(0);
							int diaryStatus = objArray.getInt(0);
							switch (diaryStatus) {
							//失败
							case ProtocolStatus.RESULT_DIARY_FORWARD_EMPTY_FAIL:
							case ProtocolStatus.RESULT_DIARY_FORWARD_MATCH_FAIL:
							case ProtocolStatus.RESULT_DIARY_INVALID_DELETED:
								result = false;
								break;
							default:
								//成功  新日志记录的主键ID
								result = true;
								break;
							}
						}
					}
				} else {

				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return result;
		}

		@Override
		public void afterParse() {
			//TODO 
			PersonalDataActivity.PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = true;
		}
	}

	/**
	 * @ClassName: DeleteDiaryProtocol
	 * @Description: 删除日记协议
	 * @author liubingsr
	 * @date 2012-7-22 下午12:14:25
	 */
	public final static class DeleteDiaryProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteDiaryProtocol";
		private long mDiaryId = 0;
		private boolean mIsZMSpace = false;

		public DeleteDiaryProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: deleteDiary
		 * @Description: 删除日记
		 * @param diaryId 日记id
		 * @return void
		 */
		public void deleteDiary(long id, boolean isSpace) {
			mIsZMSpace = isSpace;
			if (mIsZMSpace) {
				mSubUrl = "blog/zms/%d";		
			} else {
				mSubUrl = "blog/%d";		
			}
			mDiaryId = id;
			String url = mBaseUrl + String.format(mSubUrl, id);
			doDeleteDiary(url);
		}
//		public void deleteZMSpaceDiary(long syncId) {
//			mSubUrl = "blog/zms/%d";
//			mDiaryId = syncId;
//			mIsZMSpace = true;
//			String url = mBaseUrl + String.format(mSubUrl, syncId);
//			doDeleteDiary(url);
//		}		
		private void doDeleteDiary(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_DIARY_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			boolean result = false;
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						result = true;
//						if (objArray != null && objArray.length() > 0) {
//							result = objArray.getBoolean(0);
//						}
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					result = true;
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return result;
		}

		@Override
		public void afterParse() {
			// 删除本地缓存数据
			DiaryService.getInstance(mContext).deleteCahceDiary(mDiaryId, mIsZMSpace);
			PersonalDataActivity.PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = true;
		}
	}

	/**
	 * @ClassName: DeleteDiaryReplyProtocol
	 * @Description: 删除日记回复协议
	 * @author liubingsr
	 * @date 2012-7-22 下午2:08:36
	 * 
	 */
	public final static class DeleteDiaryReplyProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteDiaryReplyProtocol";
		private long mReplyId = 0;

		public DeleteDiaryReplyProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}

		/**
		 * @Title: deleteDiaryReply
		 * @Description: 删除日记回复
		 * @param replyId 日记id
		 * @param userId 用户id
		 * @return void
		 */
		public void deleteDiaryReply(long replyId) {
			mSubUrl = "blog/reply/%d";
			mReplyId = replyId;
			String url = mBaseUrl + String.format(mSubUrl, replyId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_DIARY_REPLY_PROTOCOL;
			mRequestService.sendRequest(this);
		}

		@Override
		public boolean parse() {
			boolean result = false;
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						result = true;
//						if (objArray != null && objArray.length() > 0) {
//							result = objArray.getBoolean(0);
//						}
					}
				} else if(mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					result = true;
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return result;
		}

		@Override
		public void afterParse() {
			// 删除本地缓存数据
			DiaryService.getInstance(mContext).deleteCahceDiaryReply(mReplyId);
			PersonalDataActivity.PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = true;
		}
	}

	/**
	 * @ClassName: GetDiaryListProtocol
	 * @Description: 日记列表
	 * @author liubingsr
	 * @date 2012-7-3 上午11:12:32
	 * 
	 */
	public final static class GetDiaryListProtocol extends ListProtocolHandlerBase<ZMDiary> {
		public GetDiaryListProtocol(Context context, boolean refreshed, RefreshListData<ZMDiary> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getDiaryList
		 * @Description: 获取日志列表
		 * @param userId 用户Id
		 * @return void
		 */
		public void getDiaryList(long userId) {			
			if (UserService.getInstance(mContext).isMySelf(userId)) {
				mSubUrl = String.format("blog/by_user/%s", "own");
			} else {
				mSubUrl = String.format("blog/by_user/%d", userId);
			}
			String url = mBaseUrl + mSubUrl + String.format("?pageSize=%d&startIndex=%d", mPageSize, mStartIndex);
			
			Logger.getInstance(TAG).debug(TAG+":"+url);
			
			doGetList(url);
		}
		
		public void getZMSpaceDiaryList(long spaceKindId, long spaceId) {
			mSubUrl = "blog/zms/%d/%d?pageSize=%d&startIndex=%d";
			String url = mBaseUrl + String.format(mSubUrl, spaceKindId, spaceId, mPageSize, mStartIndex);
			doGetList(url);
		}
		
		private void doGetList(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_DIARY_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			mReceiveDataList = parseDiarys();
			return true;
		}

		private ArrayList<ZMDiary> parseDiarys() {
			ArrayList<ZMDiary> zmDiaryList = new ArrayList<ZMDiary>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ZMDiary zmDiary = ZMDiary.parse(item);
								if (zmDiary != null) {
									zmDiaryList.add(zmDiary);
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
			return zmDiaryList;
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
	 * @ClassName: GetDiaryReplyListProtocol
	 * @Description: 获取日记评论协议
	 * @author liubingsr
	 * @date 2012-7-10 下午3:15:53
	 * 
	 */
	public final static class GetDiaryReplyListProtocol extends ListProtocolHandlerBase<ZMDiaryReply> {
		public GetDiaryReplyListProtocol(Context context, boolean refreshed, RefreshListData<ZMDiaryReply> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getDiaryReplies
		 * @Description: 获取日记回复列表
		 * @param diaryId 日记Id
		 * @return void
		 */
		public void getDiaryReplies(long diaryId, boolean isSpace) {
			if (isSpace) {
				mSubUrl = "blog/zms/%d/reply?pageSize=%d&startIndex=%d";
			} else {
				mSubUrl = "blog/%d/reply?pageSize=%d&startIndex=%d";
			}
			String url = mBaseUrl + String.format(mSubUrl, diaryId, mPageSize, mStartIndex);
			doGetDiaryReplies(url);
		}		
//		public void getZMSpaceDiaryReplies(long syncId) {
//			mSubUrl = "/blog/zms/%d/reply?pageSize=%d&startIndex=%d";
//			String url = mBaseUrl + String.format(mSubUrl, syncId, mPageSize, mStartIndex);
//			doGetDiaryReplies(url);
//		}
		
		private void doGetDiaryReplies(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_DIARY_REPLY_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		private ArrayList<ZMDiaryReply> parseReplyDiarys() {
			ArrayList<ZMDiaryReply> zmDiaryList = new ArrayList<ZMDiaryReply>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ZMDiaryReply zmDiary = ZMDiaryReply.parse(item);
								if (zmDiary != null) {
									zmDiaryList.add(zmDiary);
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
			return zmDiaryList;
		}

		@Override
		public boolean parse() {
			mReceiveDataList = parseReplyDiarys();
			return true;
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
