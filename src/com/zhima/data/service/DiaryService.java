/* 
 * @Title: DiaryService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.DiaryProtocolHandler.DeleteDiaryProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.DeleteDiaryReplyProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.ForwardDirayProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryListProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryReplyListProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.NewDiaryProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.ReplyDiaryProtocol;
import com.zhima.data.model.LatticeProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMDiaryReply;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMSpace;

/**
 * @ClassName: DiaryService
 * @Description: 用户日记
 * @author liubingsr
 * @date 2012-6-13 上午10:27:58
 * 
 */
public class DiaryService extends BaseService {
	private final static String TAG = "DiaryService";
	private static DiaryService mInstance = null;
	/**
	 * 日志缓存k：日志id，v：日志
	 */
	private TreeMap<Long, RefreshListData<ZMDiary>> mZMDiaryMap;
	/**
	 * 回复缓存:k:回复id，v：回复
	 */
	private TreeMap<Long, RefreshListData<ZMDiaryReply>> mZMDiaryReplyMap;

	/** 芝麻空间日志 */
	private TreeMap<Long, RefreshListData<ZMDiary>> mZMSpaceDiaryMap;
	/** 芝麻空间日志评论 */
	private TreeMap<Long, RefreshListData<ZMDiaryReply>> mZMSpaceDiaryReplyMap;

	private DiaryService(Context context) {
		super(context);
//		onCreate();
	}

	public static DiaryService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DiaryService(context);
			mInstance.onCreate();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		mZMDiaryMap = new TreeMap<Long, RefreshListData<ZMDiary>>();
		mZMDiaryReplyMap = new TreeMap<Long, RefreshListData<ZMDiaryReply>>();

		mZMSpaceDiaryMap = new TreeMap<Long, RefreshListData<ZMDiary>>();
		mZMSpaceDiaryReplyMap = new TreeMap<Long, RefreshListData<ZMDiaryReply>>();
	}

	//---------------------添加日志-------------------------------
	//发表日志 | 回复留言 | 评论日志 | 转发日志
	/**
	 * @Title: addDiary
	 * @Description: 发表日志.如果getImageUrl（）不为空。上传带图片的日志
	 * @param zmDiary
	 * @param callBack
	 * @return void
	 */
//	public void newDiary(ZMDiary zmDiary, IHttpRequestCallback callBack) {
//		newDiary(zmDiary.getTitle(), zmDiary.getContent(), zmDiary.getImageUrl(), zmDiary.getPrivacyStatus(),
//				zmDiary.getSpaceList(), callBack);
//	}
	/**
	 * @Title: newDiary
	 * @Description: 发表日志
	 * @param title 标题
	 * @param Content 内容
	 * @param imgerUrl 图片。没有图片为null
	 * @param privacyStatus 日志私密状态1为未公开，即私密；2为公开
	 * @param spaceList 同步到的空间
	 * @param callBack
	 * @return void
	 */
	public void newDiary(String title, String Content, String imgerUrl, int privacyStatus, List<ZMSpace> spaceList,
			IHttpRequestCallback callBack) {
		NewDiaryProtocol protocol = new NewDiaryProtocol(mContext, callBack);
		List<String> syncList = converSpaceId(spaceList);
		protocol.addDiary(title, Content, imgerUrl, privacyStatus, syncList);
	}

	/**
	 * @Title: forwardDiary
	 * @Description: 转发一条日志
	 * @param rawDiary 被转发日志
	 * @param zmDiary 日志
	 * @param callBack
	 * @return void
	 */
	public void forwardDiary(ZMDiary rawDiary, ZMDiary zmDiary, boolean isSpace, IHttpRequestCallback callBack) {
		long id = rawDiary.getSyncId() > 0 ? rawDiary.getSyncId() : rawDiary.getDiaryId();
		long orginalDiaryId = rawDiary.isOriginal() ? id : rawDiary.getOrginalDiaryId();
		forwardDiary(id, orginalDiaryId, zmDiary.getTitle(), zmDiary.getPrivacyStatus(), zmDiary.getSpaceList(),
				isSpace, callBack);
	}

	/**
	 * @Title: forwardDiary
	 * @Description: 转发一条日志
	 * @param diaryId 被转发的日志id或者芝麻空间里的syncId
	 * @param orginalDiaryId 原始日志id
	 * @param title 标题
	 * @param privateStatus 是否公开1为未公开，即私密；2为公开
	 * @param spaceList 需要同步到的空间
	 * @param isSpace 是否是芝麻空间日志
	 * @param callBack
	 * @return void
	 */
	public void forwardDiary(long diaryId, long orginalDiaryId, String title, int privateStatus,
			List<ZMSpace> spaceList, boolean isSpace, IHttpRequestCallback callBack) {
		ForwardDirayProtocol protocol = new ForwardDirayProtocol(mContext, callBack);
		List<String> syncList = converSpaceId(spaceList);
		protocol.forward(diaryId, orginalDiaryId, privateStatus, title, syncList, isSpace);
	}

	/**
	 * @Title: commentDiary
	 * @Description: 评论日志
	 * @param diaryId 被评论日志Id
	 * @param content 评论内容
	 * @param isSpace 是否是芝麻空间日志
	 * @param callBack
	 * @return void
	 */
	public void commentDiary(long diaryId, String content, boolean isSpace, IHttpRequestCallback callBack) {
		ReplyDiaryProtocol protocol = new ReplyDiaryProtocol(mContext, callBack);
		protocol.commentDiary(diaryId, content, isSpace);
	}

	/**
	 * @Title: replyDiary
	 * @Description: 添加个人空间日志回复
	 * @param repliedUserId 被回复者
	 * @param diaryId 被回复日志id
	 * @param content 回复内容
	 * @param isSpace 是否是芝麻空间日志
	 * @param callBack
	 * @return void
	 */
	public void replyDiary(long diaryId, long repliedUserId, String content, boolean isSpace,
			IHttpRequestCallback callBack) {
		ReplyDiaryProtocol protocol = new ReplyDiaryProtocol(mContext, callBack);
		protocol.replyDiary(diaryId, repliedUserId, content, isSpace);
	}

//	/**
//	* @Title: forwardZMSpaceDiary
//	* @Description: 芝麻空间日志转发
//	* @param syncId
//	* @param orginalDiaryId
//	* @param title
//	* @param privateStatus
//	* @param spaceList
//	* @param callBack
//	* @return void
//	*/
//	public void forwardZMSpaceDiary(long syncId, long orginalDiaryId, String title, int privateStatus,
//			List<ZMSpace> spaceList, IHttpRequestCallback callBack) {
//		ForwardDirayProtocol protocol = new ForwardDirayProtocol(mContext, callBack);
//		List<String> syncList = converSpaceId(spaceList);
//		protocol.forwardZMSpace(syncId, orginalDiaryId, privateStatus, title, syncList);
//	}
//	/**
//	* @Title: replyZMSpaceDiary
//	* @Description: 添加芝麻空间日志回复 
//	* @param repliedUserId 被回复者
//	* @param zmSpaceDiaryId 被回复芝麻空间日志id
//	* @param content 回复内容
//	* @param callBack
//	* @return void
//	*/
//	public void replyZMSpaceDiary(long zmSpaceDiaryId, long repliedUserId, String content, IHttpRequestCallback callBack) {
//		ReplyDiaryProtocol protocol = new ReplyDiaryProtocol(mContext, callBack);
//		protocol.replyZMSpaceDiary(zmSpaceDiaryId, repliedUserId, content);
//	}		
//	/**
//	 * @Title: commentZMSpaceDiary
//	 * @Description: 添加芝麻空间日志评论
//	 * @param zmSpaceDiaryId 被评论芝麻空间日志Id
//	 * @param content 评论内容
//	 * @param callBack
//	 * @return void
//	 */
//	public void commentZMSpaceDiary(long zmSpaceDiaryId, String content, IHttpRequestCallback callBack) {
//		ReplyDiaryProtocol protocol = new ReplyDiaryProtocol(mContext, callBack);
//		protocol.commentZMSpaceDiary(zmSpaceDiaryId, content);
//	}
//	/**
//	 * @Title: getZMSpaceDiary
//	 * @Description: 获取芝麻空间日志详情 
//	 * @param syncId 芝麻空间日志id
//	 * @param callBack
//	 * @return void
//	 */
//	public void getZMSpaceDiary(long syncId, IHttpRequestCallback callBack) {
//		GetDiaryProtocol protocol = new GetDiaryProtocol(mContext, callBack);
//		protocol.requestDiary(syncId);
//	}
	//---------------------获取日志------------------------------
	//获取一条日志 | 获取日志列表 | 获取留言列表
	/**
	 * @Title: addDiary2Cache
	 * @Description: 缓存日志
	 * @param diary
	 * @param keyId
	 * @param id keyid
	 * @param isSpace void
	 */
	public void addDiaryDetail2Cache(ZMDiary diary, long keyId) {
		RefreshListData<ZMDiary> list = null;
		boolean isSpace = diary.getSyncId() > 0;
		long id = 0;
		if (isSpace) {
			id = diary.getSyncId();
			if (mZMSpaceDiaryMap.containsKey(keyId)) {
				list = mZMSpaceDiaryMap.get(keyId);
			} else {
				list = new RefreshListData<ZMDiary>();
			}
			list.add(diary);
			// 由于底层使用getId()函数的返回值作为map的key，所以必须重新赋值
			list.getMap().put(id, diary);
			mZMSpaceDiaryMap.put(keyId, list);
		} else {
			id = diary.getDiaryId();
			if (mZMDiaryMap.containsKey(keyId)) {
				list = mZMDiaryMap.get(keyId);
			} else {
				list = new RefreshListData<ZMDiary>();
			}
			list.add(diary);
			list.getMap().put(id, diary);
			mZMDiaryMap.put(id, list);
		}
//		list.getMap().put(id, diary);
	}

	/**
	 * @Title: getDiaryDetail
	 * @Description: 获取日志详情
	 * @param diaryId 日志id
	 * @param id 用户id或者芝麻空间id
	 * @param isSpace 是否是芝麻空间日志
	 * @param callBack
	 * @return void
	 */
	public void getDiaryDetail(ZMDiary diary, long keyId, IHttpRequestCallback callBack) {
		GetDiaryProtocol protocol = new GetDiaryProtocol(mContext, callBack);
		protocol.requestDiary(diary, keyId);
	}

	/**
	 * @Title: getCacheDiaryList
	 * @Description:从缓存获取用户日志列表
	 * @param id 用户id或者芝麻空间id
	 * @return
	 */
	public RefreshListData<ZMDiary> getCacheDiaryList(long id, boolean isSpace) {
		if (isSpace) {
			if (mZMSpaceDiaryMap != null && mZMSpaceDiaryMap.containsKey(id)) {
				return mZMSpaceDiaryMap.get(id);
			} else {
				return new RefreshListData<ZMDiary>();
			}
		} else {
			if (mZMDiaryMap != null && mZMDiaryMap.containsKey(id)) {
				return mZMDiaryMap.get(id);
			} else {
				return new RefreshListData<ZMDiary>();
			}
		}
	}

	/**
	 * @Title: getDiaryList
	 * @Description: 获取日志列表
	 * @param userId 用户id
	 * @param refreshed 是否刷新缓存的数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getDiaryList(long userId, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMDiary> list = null;
//		mZMDiaryMap = null;
//		if(mZMDiaryMap == null){
//			mZMDiaryMap = new TreeMap<Long, RefreshListData<ZMDiary>>();
//			list = new RefreshListData<ZMDiary>();
//			mZMDiaryMap.put(userId, list);
//		}else {
		if (mZMDiaryMap.containsKey(userId)) {
			list = mZMDiaryMap.get(userId);
		} else {
			list = new RefreshListData<ZMDiary>();
			mZMDiaryMap.put(userId, list);
		}
//		}
		GetDiaryListProtocol protocol = new GetDiaryListProtocol(mContext, refreshed, list, callBack);
		protocol.getDiaryList(userId);
	}

	/**
	 * @Title: getCacheZMSpaceDiaryList
	 * @Description: 从缓存获取芝麻空间日志列表
	 * @param zmObject 芝麻空间对象
	 * @return RefreshListData<ZMDiary>
	 */
//	public RefreshListData<ZMDiary> getCacheZMSpaceDiaryList(ZMObject zmObject) {
//		if (mZMSpaceDiaryMap.containsKey(zmObject.getId())) {
//			return mZMSpaceDiaryMap.get(zmObject.getId());
//		} else {
//			return new RefreshListData<ZMDiary>();
//		}
//	}

	public RefreshListData<ZMDiaryReply> getCacheZMDiaryReplyList(long diaryId, boolean isSpaceDiary) {
		if (isSpaceDiary) {
			if (mZMSpaceDiaryReplyMap.containsKey(diaryId)) {
				return mZMSpaceDiaryReplyMap.get(diaryId);
			}
			return new RefreshListData<ZMDiaryReply>();
		} else {
			if (mZMDiaryReplyMap.containsKey(diaryId)) {
				return mZMDiaryReplyMap.get(diaryId);
			}
			return new RefreshListData<ZMDiaryReply>();
		}
	}

	/**
	 * @Title: getZMSpaceDiaryList
	 * @Description: 获取芝麻空间日志列表
	 * @param zmObject 芝麻空间对象
	 * @param refreshed 是否刷新缓存的数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getZMSpaceDiaryList(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMDiary> list = null;
		long id = zmObject.getRemoteId();
		if (mZMSpaceDiaryMap == null) {
			mZMSpaceDiaryMap = new TreeMap<Long, RefreshListData<ZMDiary>>();
			list = new RefreshListData<ZMDiary>();
			mZMSpaceDiaryMap.put(id, list);
		} else {
			if (mZMSpaceDiaryMap.containsKey(id)) {
				list = mZMSpaceDiaryMap.get(id);
			} else {
				list = new RefreshListData<ZMDiary>();
				mZMSpaceDiaryMap.put(id, list);
			}
		}
		GetDiaryListProtocol protocol = new GetDiaryListProtocol(mContext, refreshed, list, callBack);
		protocol.getZMSpaceDiaryList(zmObject.getZMObjectType(), zmObject.getRemoteId());
	}

	/**
	 * @Title: getDiaryReplyList
	 * @Description: 获取日志评论列表
	 * @param diaryId 日志id
	 * @param refreshed 是否刷新缓存的数据
	 * @param isSpace 是否是芝麻空间日志
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getDiaryReplyList(long diaryId, boolean refreshed, boolean isSpace, IHttpRequestCallback callBack) {
		RefreshListData<ZMDiaryReply> list = null;
		if (isSpace) {
			if (mZMSpaceDiaryReplyMap.containsKey(diaryId)) {
				list = mZMSpaceDiaryReplyMap.get(diaryId);
			} else {
				list = new RefreshListData<ZMDiaryReply>();
				mZMSpaceDiaryReplyMap.put(diaryId, list);
			}
		} else {
			if (mZMDiaryReplyMap.containsKey(diaryId)) {
				list = mZMDiaryReplyMap.get(diaryId);
			} else {
				list = new RefreshListData<ZMDiaryReply>();
				mZMDiaryReplyMap.put(diaryId, list);
			}
		}
		GetDiaryReplyListProtocol protocol = new GetDiaryReplyListProtocol(mContext, refreshed, list, callBack);
		protocol.getDiaryReplies(diaryId, isSpace);
	}

//	/**
//	* @Title: getZMSpaceDiaryReplyList
//	* @Description: 获取芝麻空间日志评论列表
//	* @param syncId 芝麻空间日志Id
//	* @param refreshed 是否刷新缓存的数据
//	* @param callBack 结果通知回调
//	* @return void
//	*/
//	public void getZMSpaceDiaryReplyList(long syncId, boolean refreshed, IHttpRequestCallback callBack) {
//		RefreshListData<ZMDiaryReply> list = null;
//		if (mZMSpaceDiaryReplyMap.containsKey(syncId)) {
//			list = mZMSpaceDiaryReplyMap.get(syncId);
//		} else {
//			list = new RefreshListData<ZMDiaryReply>();
//			mZMSpaceDiaryReplyMap.put(syncId, list);
//		}
//		GetDiaryReplyListProtocol protocol = new GetDiaryReplyListProtocol(mContext, refreshed, list, callBack);
//		protocol.getZMSpaceDiaryReplies(syncId);
//	}
	//---------------------删除日志-------------------------------
	//删除一条日记 | 删除一条回复
//	/**
//	 * @Title: deleteDiary
//	 * @Description: 删除一条日记
//	 * @param diaryId 要删除的日志id
//	 * @param callBack
//	 * @return void
//	 */
//	public void deleteDiary(long diaryId, IHttpRequestCallback callBack) {
//		DeleteDiaryProtocol protocol = new DeleteDiaryProtocol(mContext, callBack);
//		protocol.deleteDiary(diaryId);
//	}
	/**
	 * @Title: deleteZMSpaceDiary
	 * @Description: 删除芝麻空间的日志
	 * @param id 要删除的芝麻空间日志id或者日志id
	 * @param callBack
	 * @return void
	 */
	public void deleteDiary(long id, boolean isSpace, IHttpRequestCallback callBack) {
		DeleteDiaryProtocol protocol = new DeleteDiaryProtocol(mContext, callBack);
		protocol.deleteDiary(id, isSpace);
	}

	/**
	 * @Title: deleteDiaryReply
	 * @Description: 删除一条评论
	 * @param zmDiaryReply
	 * @param callBack
	 * @return void
	 */
	public void deleteDiaryReply(ZMDiaryReply zmDiaryReply, IHttpRequestCallback callBack) {
		deleteDiaryReply(zmDiaryReply.getId(), callBack);
	}

	/**
	 * @Title: deleteDiaryReply
	 * @Description: 删除一条日志评论。 芝麻空间与个人空间都调用这个函数
	 * @param replyId 要删除的评论id
	 * @param callBack
	 * @return void
	 */
	public void deleteDiaryReply(long replyId, IHttpRequestCallback callBack) {
		DeleteDiaryReplyProtocol protocol = new DeleteDiaryReplyProtocol(mContext, callBack);
		protocol.deleteDiaryReply(replyId);
	}

	//-----------------------Cache-------------------------------
	/**
	 * @Title: deleteCahceDiaryReply
	 * @Description: 删除缓存的日志
	 * @param diaryId 日志id
	 * @return void
	 */
	public void deleteCahceDiary(long diaryId, boolean isZMSpace) {
		if (isZMSpace) {
			Set<Long> keys = mZMSpaceDiaryMap.keySet();
			RefreshListData<ZMDiary> products;
			for (Long key : keys) {
				products = mZMSpaceDiaryMap.get(key);
				if (products.getMap().containsKey(diaryId)) {
					products.getMap().remove(diaryId);
					break;
				}
			}
		} else {
			Set<Long> keys = mZMDiaryMap.keySet();
			RefreshListData<ZMDiary> products;
			for (Long key : keys) {
				products = mZMDiaryMap.get(key);
				if (products.getMap().containsKey(diaryId)) {
					products.getMap().remove(diaryId);
					break;
				}
			}
		}
	}

	/**
	 * @Title: deleteCahceDiaryReply
	 * @Description: 删除本地日志回复
	 * @param replyId 回复id
	 * @return void
	 */
	public void deleteCahceDiaryReply(long replyId) {
		if (mZMDiaryReplyMap != null) {
			mZMDiaryReplyMap.remove(replyId);
		}
		if (mZMSpaceDiaryReplyMap != null) {
			mZMSpaceDiaryReplyMap.remove(replyId);
		}
	}

	//-----------------------Method-----------------------------
	/**
	 * space转化为String
	 */
	private List<String> converSpaceId(List<ZMSpace> spaces) {
		List<String> syncList = new ArrayList<String>();
		if (spaces != null) {
			for (ZMSpace space : spaces) {
				String value = space.getSpaceKind() + "#" + space.getId();
				syncList.add(value);
			}
		}
		return syncList;
	}

	//----------------------------------------------------------

	@Override
	public void onDestroy() {
		clear();
		mZMDiaryMap = null;
		mZMDiaryReplyMap = null;
		mZMSpaceDiaryMap = null;
		mZMSpaceDiaryReplyMap = null;
		System.gc();
	}

	@Override
	public void clear() {

		if (mZMDiaryMap != null) {
//			System.out.println("mZMDiaryMap----clear");
			mZMDiaryMap.clear();
		}
		if (mZMDiaryReplyMap != null) {
//			System.out.println("mZMDiaryReplyMap----clear");
			mZMDiaryReplyMap.clear();
		}

		if (mZMSpaceDiaryMap != null) {
//			System.out.println("mZMSpaceDiaryMap----clear");
			mZMSpaceDiaryMap.clear();
		}
		if (mZMSpaceDiaryReplyMap != null) {
//			System.out.println("mZMSpaceDiaryReplyMap----clear");
			mZMSpaceDiaryReplyMap.clear();
		}
	}
}
