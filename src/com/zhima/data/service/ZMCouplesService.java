/* 
 * @Title: ZMCouplesService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;


import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.AddWeddingCommentProtocol;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetJournalListProtocol;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetMultimediaListProtocol;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetWeddingCommentListProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.WeddingComment;
import com.zhima.data.model.ZMObject;

/**
* @ClassName: ZMCouplesService
* @Description: 情侣知天使
* @author liubingsr
* @date 2012-7-25 下午2:58:48
*
*/
public class ZMCouplesService extends BaseService {
	private final static String TAG = "ZMCouplesService";
	private static ZMCouplesService mInstance = null;
	/**
	 * 情侣知天使日志
	 */
	private TreeMap<Long, RefreshListData<IdolAcqierement>> mCouplesJournalMap;
	/**
	 * 情侣知天使影像
	 */
	private TreeMap<Long, RefreshListData<IdolAcqierement>> mMultimediaMap;
	/**
	 * 情侣知天使评论
	 */
	private TreeMap<Long, RefreshListData<WeddingComment>> mWeddingCommentMap;
	
	private ZMCouplesService(Context context) {
		super(context);		
		mCouplesJournalMap = new TreeMap<Long, RefreshListData<IdolAcqierement>>();
		mMultimediaMap = new TreeMap<Long, RefreshListData<IdolAcqierement>>();
		mWeddingCommentMap = new TreeMap<Long, RefreshListData<WeddingComment>>();
		onCreate();
	}

	public static ZMCouplesService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ZMCouplesService(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
	}

	/**
	* @Title: addCouplesJournal
	* @Description: 日志加入本地缓存
	* @param content 内容
	* @return void
	*/
	public void addCouplesJournal(IdolAcqierement content) {
//		ArrayList<IdolAcqierement> array;
//		long idolId = content.getTargetId();
//		if (mExternalContentMap.containsKey(idolId)) {
//			array = mExternalContentMap.get(idolId);
//			for (int index = 0,count = array.size(); index < count; ++index) {
//				if (array.get(index).getId() == content.getId()) {
//					return;
//				}
//			}			
//		} else {
//			array = new ArrayList<IdolAcqierement>();
//		}
//		array.add(content);
//		mExternalContentMap.put(idolId, array);
	}
	/**
	* @Title: getCacheCouplesJournal
	* @Description: 从本地缓存中获取IdolAcqierement信息
	* @param contentId 日志Id
	* @return null 本地无数据
	*/
	public IdolAcqierement getCacheCouplesJournal(long contentId) {
		Set<Long> keys = mCouplesJournalMap.keySet();
		RefreshListData<IdolAcqierement> list;
		for (Long key : keys) {
			list = mCouplesJournalMap.get(key);
			if (list.getMap().containsKey(contentId)) {
				return list.getMap().get(contentId);
			}
		}
		return null;
	}
	/**
	* @Title: getCacheCouplesJournalList
	* @Description: 得到缓存的知天使日志列表
	* @param idol
	* @return
	* RefreshListData<IdolAcqierement>
	*/
	public RefreshListData<IdolAcqierement> getCacheCouplesJournalList(ZMObject idol) {
		if (mCouplesJournalMap.containsKey(idol.getId())) {
			return mCouplesJournalMap.get(idol.getId());
		} else {
			return new RefreshListData<IdolAcqierement>();
		}
	}
	/**
	* @Title: getCouplesJournalList
	* @Description: 得到日志列表
	* @param idol 知天使
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getCouplesJournalList(ZMObject idol, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mCouplesJournalMap.clear();
//		}
		RefreshListData<IdolAcqierement> list = null;
		if (!mCouplesJournalMap.containsKey(idol.getId())) {
			list = new RefreshListData<IdolAcqierement>();
			mCouplesJournalMap.put(idol.getId(), list);
		} else {
			list = mCouplesJournalMap.get(idol.getId());
		}
		GetJournalListProtocol protocol = new GetJournalListProtocol(mContext, refreshed, list, callBack);
		protocol.getJournalList(idol.getRemoteId(), idol.getZMObjectType());
	}
	/**
	* @Title: getCacheMultimediaList
	* @Description: 得到缓存的影像列表
	* @param idol 知天使
	* @return
	* RefreshListData<IdolAcqierement>
	*/
	public RefreshListData<IdolAcqierement> getCacheMultimediaList(ZMObject idol) {
		if (mMultimediaMap.containsKey(idol.getId())) {
			return mMultimediaMap.get(idol.getId());
		} else {
			return new RefreshListData<IdolAcqierement>();
		}
	}
	/**
	* @Title: getMultimediaList
	* @Description: 影像列表
	* @param idol 知天使
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getMultimediaList(ZMObject idol, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mMultimediaMap.clear();
//		}
		RefreshListData<IdolAcqierement> list = null;
		if (!mMultimediaMap.containsKey(idol.getId())) {
			list = new RefreshListData<IdolAcqierement>();
			mMultimediaMap.put(idol.getId(), list);
		} else {
			list = mMultimediaMap.get(idol.getId());
		}
		GetMultimediaListProtocol protocol = new GetMultimediaListProtocol(mContext, refreshed, list, callBack);
		protocol.getMultimediaList(idol.getRemoteId(), idol.getZMObjectType());
	}
	/**
	* @Title: addComment
	* @Description: 新评论加入缓存
	* @param acqierementId 才艺id
	* @param comment 评论
	* @return void
	*/
	public void addComment(long zmRemoteId, WeddingComment comment) {
		RefreshListData<WeddingComment> list = null;
		if (!mWeddingCommentMap.containsKey(zmRemoteId)) {
			list = new RefreshListData<WeddingComment>();
			mWeddingCommentMap.put(zmRemoteId, list);
		} else {
			list = mWeddingCommentMap.get(zmRemoteId);
		}
		list.add(comment);
	}
	/**
	* @Title: addWeddingComment
	* @Description: 喜印空间发表评论
	* @param zmRemoteId 喜印空间id
	* @param nickname 昵称
	* @param content 评论内容
	* @param callBack 结果通知回调
	* @return void
	*/
	public void addWeddingComment(long zmRemoteId, int zmObjectType, String nickname, String content, IHttpRequestCallback callBack) {
		AddWeddingCommentProtocol protocol = new AddWeddingCommentProtocol(mContext, callBack);
		protocol.addComment(zmRemoteId, zmObjectType, nickname, content);
	}
	/**
	* @Title: getCacheWeddingCommentList
	* @Description: 得到缓存的评论列表
	* @param zmRemoteId id
	* @return
	*/
	public RefreshListData<WeddingComment> getCacheWeddingCommentList(long zmRemoteId, int zmObjectType) {
		long id = ZMObject.createLocalId(zmRemoteId, zmObjectType);
		if (mWeddingCommentMap.containsKey(id)) {
			return mWeddingCommentMap.get(id);
		} else {
			return new RefreshListData<WeddingComment>();
		}
	}
	/**
	* @Title: getWeddingCommentList
	* @Description: 获取评论列表
	* @param zmRemoteId id
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getWeddingCommentList(long zmRemoteId, int zmObjectType, boolean refreshed, IHttpRequestCallback callBack) {
		long id = ZMObject.createLocalId(zmRemoteId, zmObjectType);
		RefreshListData<WeddingComment> list = null;
		if (!mWeddingCommentMap.containsKey(id)) {
			list = new RefreshListData<WeddingComment>();
			mWeddingCommentMap.put(id, list);
		} else {
			list = mWeddingCommentMap.get(id);
		}
		GetWeddingCommentListProtocol protocol = new GetWeddingCommentListProtocol(mContext, refreshed, list, callBack);
		protocol.getCommentList(zmRemoteId, zmObjectType);
	}
	
	@Override
	public void onDestroy() {		
		clear();
		mCouplesJournalMap = null;
		mMultimediaMap = null;
		mWeddingCommentMap = null;
		System.gc();
	}

	@Override
	public void clear() {
		if (mCouplesJournalMap != null) {
			mCouplesJournalMap.clear();			
		}
		if (mMultimediaMap != null) {
			mMultimediaMap.clear();
		}
		if (mWeddingCommentMap != null) {
			mWeddingCommentMap.clear();		
		}
	}
}
