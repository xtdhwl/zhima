/* 
 * @Title: ZMIdolProtocolHandler.java
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
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoContent;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.WeddingComment;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ZMIdolService;

/**
* @ClassName: ZMIdolProtocolHandler
* @Description: 知天使协议处理
* @author liubingsr
* @date 2012-7-25 下午3:39:13
*
*/
public final class ZMIdolProtocolHandler {	
	/**
	* @ClassName: GetAcqierementListProtocol
	* @Description: 获取频道列表协议
	* @author liubingsr
	* @date 2012-7-25 下午3:43:07
	*
	*/
	public final static class GetAcqierementListProtocol extends ListProtocolHandlerBase<IdolAcqierement> {
		private final static String TAG = "GetAcqierementListProtocol";	
		private long mIdolId = 0;
		
		public GetAcqierementListProtocol(Context context, boolean refreshed, RefreshListData<IdolAcqierement> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getAcqierementList
		* @Description: 获取频道列表
		* @param idolId 知天使id
		* @return void
		*/
		public void getAcqierementList(long idolId) {
			mIdolId = idolId;
			mSubUrl = "external_content/%d/%d?pageSize=%d&startIndex=%d";
			String url = mBaseUrl + String.format(mSubUrl, TargetType.TARGET_TYPE_IDOL_SPACE, mIdolId, mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_IDOL_CHANNEL_LIST_PROTOCOL;
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
		private long mIdolId = 0;
		
		public GetMultimediaListProtocol(Context context, boolean refreshed, RefreshListData<IdolAcqierement> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getMultimediaList
		* @Description: 获取影像列表
		* @param idolId 知天使id
		* @return void
		*/
		public void getMultimediaList(long idolId) {
			mIdolId = idolId;
			mSubUrl = "space/idol/%d/content?pageSize=%d&startIndex=%d";			
			String url = mBaseUrl + String.format(mSubUrl, mIdolId, mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_IDOL_MULTIMEDIA_LIST_PROTOCOL;
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
	
	public final static class GetChannelListByKindProtocol extends ListProtocolHandlerBase<IdolAcqierement> {
		private final static String TAG = "GetChannelListByKindProtocol";	
		private long mChannleKind = 0;
		
		public GetChannelListByKindProtocol(Context context, boolean refreshed, RefreshListData<IdolAcqierement> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getChannleList
		* @Description: 获取频道列表
		* @param channleKind 频道类型
		* @return void
		*/
		public void getChannleList(long channleKind) {
			mChannleKind = channleKind;
			mSubUrl = "external_content/by_kind/%d?pageSize=%d&startIndex=%d";
			String url = mBaseUrl + String.format(mSubUrl, mChannleKind, mPageSize, mStartIndex);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_IDOL_CHANNEL_LIST_BY_KIND_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseAcqierement();
			return true;
		}

		/**
		* @Title: parseAcqierement
		* @Description: 解析数据包得到频道内容信息
		* @param json
		* @return ArrayList<IdolAcqierement>
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
			}			
		}		
	}
	/**
	* @ClassName: AddAcqierementCommentProtocol
	* @Description: 发表才艺评论协议
	* @author liubingsr
	* @date 2012-8-31 上午11:21:50
	*
	*/
	public final static class AddAcqierementCommentProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddAcqierementCommentProtocol";
		private static Gson gson = null;    
	    static {  
	        if (gson == null) {  
	            gson = new Gson();  
	        }  
	    }
		private long mAcqierementId = 0;
		private String mContent = "";
		private WeddingComment mComment = null;

		public AddAcqierementCommentProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: addComment
		* @Description: 添加评论
		* @param acqierementId 才艺id
		* @param content 评论内容
		* @return void
		*/
		public void addComment(long acqierementId, String content) {
			mSubUrl = "external_content/%d/comment";
			mAcqierementId = acqierementId;
			String url = mBaseUrl + String.format(mSubUrl, acqierementId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			VoContent vo = new VoContent();
			vo.setContent(content);			
			String json = gson.toJson(vo);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_IDOL_ACQIEREMENT_COMMENT_PROTOCOL;
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
				ZMIdolService.getInstance(mContext).addComment(mAcqierementId, mComment);
			}
		}		
	}
	/**
	* @ClassName: GetAcqierementCommentListProtocol
	* @Description: 获取知天使才艺评论列表协议
	* @author liubingsr
	* @date 2012-8-31 上午10:19:08
	*
	*/
	public final static class GetAcqierementCommentListProtocol extends ListProtocolHandlerBase<WeddingComment> {
		private final static String TAG = "GetAcqierementCommentListProtocol";	
		private long mAcqierementId = 0;
		
		public GetAcqierementCommentListProtocol(Context context, boolean refreshed, RefreshListData<WeddingComment> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getCommentList
		* @Description: 获取评论列表
		* @param acqierementId 才艺id
		* @return void
		*/
		public void getCommentList(long acqierementId) {
			mAcqierementId = acqierementId;
			mSubUrl = "external_content/%d/comment";
			String url = mBaseUrl + String.format(mSubUrl, acqierementId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_IDOL_ACQIEREMENT_COMMENT_LIST_PROTOCOL;
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
