/* 
 * @Title: ZMIdolService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;


import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ZMIdolProtocolHandler.AddAcqierementCommentProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetAcqierementCommentListProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetAcqierementListProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetChannelListByKindProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetMultimediaListProtocol;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.WeddingComment;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;

/**
* @ClassName: ZMIdolService
* @Description: 知天使
* @author liubingsr
* @date 2012-7-25 下午2:58:48
*
*/
public class ZMIdolService extends BaseService {
	private final static String TAG = "ZMIdolService";
	private static ZMIdolService mInstance = null;
	/**
	 * 频道
	 */
	private TreeMap<Long, RefreshListData<IdolAcqierement>> mAcqierementMap;
	/**
	 * 指定类型的频道内容列表
	 */
	private TreeMap<Long, RefreshListData<IdolAcqierement>> mChannelListOfKindMap;
	/**
	 * 知天使影音
	 */
	private TreeMap<Long, RefreshListData<IdolAcqierement>> mMultimediaMap;
	/**
	 * 才艺评论
	 */
	private TreeMap<Long, RefreshListData<WeddingComment>> mAcqierementCommentMap;
	
	private ZMIdolService(Context context) {
		super(context);		
		mAcqierementMap = new TreeMap<Long, RefreshListData<IdolAcqierement>>();
		mMultimediaMap = new TreeMap<Long, RefreshListData<IdolAcqierement>>();
		mAcqierementCommentMap = new TreeMap<Long, RefreshListData<WeddingComment>>();
		mChannelListOfKindMap = new TreeMap<Long, RefreshListData<IdolAcqierement>>();
		onCreate();
	}

	public static ZMIdolService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ZMIdolService(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
	}

	/**
	* @Title: addAcqierement
	* @Description: 才艺展示加入本地缓存
	* @param content 展示内容
	* @return void
	*/
	public void addAcqierement(IdolAcqierement content) {
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
	* @Title: getCacheAcqierement
	* @Description: 从本地缓存中获取IdolAcqierement信息
	* @param contentId 才艺Id
	* @return null 本地无数据
	*/
	public IdolAcqierement getCacheAcqierement(long contentId) {
		Set<Long> keys = mAcqierementMap.keySet();
		RefreshListData<IdolAcqierement> list;
		for (Long key : keys) {
			list = mAcqierementMap.get(key);
			if (list.getMap().containsKey(contentId)) {
				return list.getMap().get(contentId);
			}
		}
		return null;
	}
	/**
	* @Title: getCacheAcqierementList
	* @Description: 得到缓存的知天使频道列表
	* @param idol
	* @return
	* RefreshListData<IdolAcqierement>
	*/
	public RefreshListData<IdolAcqierement> getCacheAcqierementList(ZMIdolObject idol) {
		if (mAcqierementMap.containsKey(idol.getId())) {
			return mAcqierementMap.get(idol.getId());
		} else {
			return new RefreshListData<IdolAcqierement>();
		}
	}
	/**
	* @Title: getAcqierementList
	* @Description: 得到频道列表
	* @param idol 知天使
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getAcqierementList(ZMIdolObject idol, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mAcqierementMap.clear();
//		}
		RefreshListData<IdolAcqierement> list = null;
		if (!mAcqierementMap.containsKey(idol.getId())) {
			list = new RefreshListData<IdolAcqierement>();
			mAcqierementMap.put(idol.getId(), list);
		} else {
			list = mAcqierementMap.get(idol.getId());
		}
		GetAcqierementListProtocol protocol = new GetAcqierementListProtocol(mContext, refreshed, list, callBack);
		protocol.getAcqierementList(idol.getRemoteId());
	}
	/**
	* @Title: getCacheMultimediaList
	* @Description: 得到缓存的知天使影像列表
	* @param idol 知天使
	* @return
	* RefreshListData<IdolAcqierement>
	*/
	public RefreshListData<IdolAcqierement> getCacheMultimediaList(ZMIdolObject idol) {
		if (mMultimediaMap.containsKey(idol.getId())) {
			return mMultimediaMap.get(idol.getId());
		} else {
			return new RefreshListData<IdolAcqierement>();
		}
	}
	/**
	* @Title: getMultimediaList
	* @Description: 知天使影像列表
	* @param idol 知天使
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getMultimediaList(ZMIdolObject idol, boolean refreshed, IHttpRequestCallback callBack) {
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
		protocol.getMultimediaList(idol.getRemoteId());
	}
	/**
	* @Title: getChannelListByKind
	* @Description: 根据知天使频道类型查询频道列表 
	* @param channleKind 频道类型
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getChannelListByKind(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<IdolAcqierement> list = null;
		long channleKind = zmObject.getSpaceKind() == null ? zmObject.getKindId() : zmObject.getSpaceKind().getId();
		if (!mChannelListOfKindMap.containsKey(channleKind)) {
			list = new RefreshListData<IdolAcqierement>();
			mChannelListOfKindMap.put(channleKind, list);
		} else {
			list = mChannelListOfKindMap.get(channleKind);
		}
		GetChannelListByKindProtocol protocol = new GetChannelListByKindProtocol(mContext, refreshed, list, callBack);
		protocol.getChannleList(channleKind);
	}
	/**
	* @Title: addComment
	* @Description: 新评论加入缓存
	* @param acqierementId 才艺id
	* @param comment 评论
	* @return void
	*/
	public void addComment(long acqierementId, WeddingComment comment) {
		RefreshListData<WeddingComment> list = null;
		if (!mAcqierementCommentMap.containsKey(acqierementId)) {
			list = new RefreshListData<WeddingComment>();
			mAcqierementCommentMap.put(acqierementId, list);
		} else {
			list = mAcqierementCommentMap.get(acqierementId);
		}
		list.add(comment);
	}
	/**
	* @Title: addAcqierementComment
	* @Description: 评论知天使才艺
	* @param acqierementId 才艺id
	* @param content 评论内容
	* @param callBack 结果通知回调
	* @return void
	*/
	public void addAcqierementComment(long acqierementId, String content, IHttpRequestCallback callBack) {
		AddAcqierementCommentProtocol protocol = new AddAcqierementCommentProtocol(mContext, callBack);
		protocol.addComment(acqierementId, content);
	}
	/**
	* @Title: getCacheAcqierementCommentList
	* @Description: 得到缓存的才艺评论列表
	* @param acqierementId 才艺id
	* @return
	*/
	public RefreshListData<WeddingComment> getCacheAcqierementCommentList(long acqierementId) {
		if (mAcqierementCommentMap.containsKey(acqierementId)) {
			return mAcqierementCommentMap.get(acqierementId);
		} else {
			return new RefreshListData<WeddingComment>();
		}
	}
	/**
	* @Title: getAcqierementCommentList
	* @Description: 获取才艺评论列表
	* @param acqierementId 才艺id
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getAcqierementCommentList(long acqierementId, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mAcqierementCommentMap.clear();
//		}
		RefreshListData<WeddingComment> list = null;
		if (!mAcqierementCommentMap.containsKey(acqierementId)) {
			list = new RefreshListData<WeddingComment>();
			mAcqierementCommentMap.put(acqierementId, list);
		} else {
			list = mAcqierementCommentMap.get(acqierementId);
		}
		GetAcqierementCommentListProtocol protocol = new GetAcqierementCommentListProtocol(mContext, refreshed, list, callBack);
		protocol.getCommentList(acqierementId);
	}
	
	@Override
	public void onDestroy() {		
		clear();
		mAcqierementMap = null;
		mMultimediaMap = null;
		mAcqierementCommentMap = null;
		mChannelListOfKindMap = null;
		System.gc();
	}

	@Override
	public void clear() {
		if (mAcqierementMap != null) {
			mAcqierementMap.clear();			
		}
		if (mMultimediaMap != null) {
			mMultimediaMap.clear();
		}
		if (mAcqierementCommentMap != null) {
			mAcqierementCommentMap.clear();		
		}
		if (mChannelListOfKindMap != null) {
			mChannelListOfKindMap.clear();
		}
	}
}
