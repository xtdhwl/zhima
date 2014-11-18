/* 
 * @Title: ZMCouplesProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.TargetType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoWeddingComment;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.WeddingComment;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ZMIdolService;

/**
* @ClassName: ZMCouplesProtocolHandler
* @Description: 情侣知天使协议处理
* @author liubingsr
* @date 2012-7-25 下午3:39:13
*
*/
public final class ZMCouplesProtocolHandler {	
	/**
	* @ClassName: GetAcqierementListProtocol
	* @Description: 获取日志列表协议
	* @author liubingsr
	* @date 2012-7-25 下午3:43:07
	*
	*/
	public final static class GetJournalListProtocol extends ListProtocolHandlerBase<IdolAcqierement> {
		private final static String TAG = "GetJournalListProtocol";	
		private long mIdolId = 0;
		
		public GetJournalListProtocol(Context context, boolean refreshed, RefreshListData<IdolAcqierement> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getJournalList
		* @Description: 获取日志列表
		* @param idolRemoteId 知天使id
		* @return void
		*/
		public void getJournalList(long idolRemoteId, int zmObjectType) {
			mIdolId = idolRemoteId;
			mSubUrl = "space//%s/%d/channel?pageSize=%d&startIndex=%d";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObjectType), mIdolId, mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_COUPLES_JOURNAL_CHANNEL_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseAcqierement();
			return true;
		}

		/**
		* @Title: parseAcqierement
		* @Description: 解析数据包得到才艺信息
		* @param json
		* @return ArrayList<ExternalContent>
		*/
		private ArrayList<IdolAcqierement> parseAcqierement() {
			ArrayList<IdolAcqierement> contentList = new ArrayList<IdolAcqierement>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								IdolAcqierement content = IdolAcqierement.parse(item);
								if (content != null) {
									content.setTargetId(mIdolId);
									content.setTargetType(TargetType.TARGET_TYPE_WEDDING_SPACE);
									contentList.add(content);
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
			return contentList;
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
//				for (IdolAcqierement content : mReceiveDataList) {
//					// 存入缓存
//					ZMIdolService.getInstance(mContext).addAcqierement(content);
//				}
			}			
		}		
	}
	/**
	* @ClassName: GetMultimediaListProtocol
	* @Description: 获取知天使影像协议
	* @author liubingsr
	* @date 2012-9-20 下午5:05:16
	*
	*/
	public final static class GetMultimediaListProtocol extends ListProtocolHandlerBase<IdolAcqierement> {
		private final static String TAG = "GetMultimediaListProtocol";	
		private long mIdolId;
		
		public GetMultimediaListProtocol(Context context, boolean refreshed, RefreshListData<IdolAcqierement> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getMultimediaList
		* @Description: 获取影像列表
		* @param idolRemoteId 知天使id
		* @return void
		*/
		public void getMultimediaList(long idolRemoteId, int zmObjectType) {
			mIdolId = idolRemoteId;
			mSubUrl = "space/%s/%d/content?pageSize=%d&startIndex=%d";			
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObjectType), mIdolId, mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_COUPLES_MULTIMEDIA_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseAcqierement();
			return true;
		}

		/**
		* @Title: parseAcqierement
		* @Description: 解析数据包得到信息
		* @param json
		* @return ArrayList<ExternalContent>
		*/
		private ArrayList<IdolAcqierement> parseAcqierement() {
			ArrayList<IdolAcqierement> contentList = new ArrayList<IdolAcqierement>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								IdolAcqierement content = IdolAcqierement.parse(item);
								if (content != null) {
									content.setTargetId(mIdolId);
									content.setTargetType(TargetType.TARGET_TYPE_WEDDING_SPACE);
									contentList.add(content);
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
			return contentList;
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
//				for (IdolAcqierement content : mReceiveDataList) {
//					// 存入缓存
//					ZMIdolService.getInstance(mContext).addAcqierement(content);
//				}
			}			
		}		
	}
	/**
	* @ClassName: AddAcqierementCommentProtocol
	* @Description: 发表评论协议
	* @author liubingsr
	* @date 2012-8-31 上午11:21:50
	*
	*/
	public final static class AddWeddingCommentProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddWeddingCommentProtocol";
		private static Gson gson = null;    
	    static {  
	        if (gson == null) {  
	            gson = new Gson();  
	        }  
	    }
		private long mRemoteId = 0;
		private String mContent = "";
		private WeddingComment mComment = null;
		private String mNickname = null;

		public AddWeddingCommentProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: addComment
		* @Description: 添加评论
		* @param zmRemoteId 喜印空间id
		* @param nickname 昵称 
		* @param content 评论内容		
		* @return void
		*/
		public void addComment(long zmRemoteId, int zmObjectType, String nickname, String content) {
			mSubUrl = "space/%s/%d/comment";
			mRemoteId = zmRemoteId;
			mNickname  = nickname;
			mContent = content;			
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObjectType), zmRemoteId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			VoWeddingComment vo = new VoWeddingComment();
			vo.setContent(content);
			vo.setCreatedByName(mNickname);
			String json = gson.toJson(vo);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_COUPLES_COMMENT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray ids = mResponeVO.getJSONArray("items");
						if (ids != null && ids.length() > 0) {
							if (!ids.isNull(0)) {
								long commentId = ids.getLong(0);
								mComment = new WeddingComment();
								mComment.setId(commentId);
								mComment.setNickname(mNickname);
								mComment.setContent(mContent);
								mComment.setSender(AccountService.getInstance(mContext).getMyself());
								mComment.setPostTime(System.currentTimeMillis());
								return true;
							}
						}
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			if (mComment != null) {
				// 加入缓存
				ZMIdolService.getInstance(mContext).addComment(mRemoteId, mComment);
			}
		}		
	}
	/**
	* @ClassName: GetWeddingCommentListProtocol
	* @Description: 获取情侣知天使评论列表协议
	* @author liubingsr
	* @date 2012-8-31 上午10:19:08
	*
	*/
	public final static class GetWeddingCommentListProtocol extends ListProtocolHandlerBase<WeddingComment> {
		private final static String TAG = "GetWeddingCommentListProtocol";	
		private long mRemoteId;
		
		public GetWeddingCommentListProtocol(Context context, boolean refreshed, RefreshListData<WeddingComment> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getCommentList
		* @Description: 获取评论列表
		* @param remoteId 知天使id
		* @return void
		*/
		public void getCommentList(long remoteId,  int zmObjectType) {
			mRemoteId = remoteId;
			mSubUrl = "space//%s/%d/comment?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObjectType), remoteId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_COUPLES_COMMENT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseComment();
			return true;
		}

		/**
		* @Title: parseComment
		* @Description: 解析数据包得到评论信息
		* @param json
		* @return ArrayList<AcqierementComment>
		*/
		private ArrayList<WeddingComment> parseComment() {
			ArrayList<WeddingComment> commentList = new ArrayList<WeddingComment>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								WeddingComment comment = WeddingComment.parse(item);
								if (comment != null) {
									commentList.add(comment);
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
			return commentList;
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
