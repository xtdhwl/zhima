package com.zhima.data.service;

import java.util.ArrayList;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.AddSpaceVisiteMessageProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetBindSpacesProtocolHandler;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetBottomRecommendedListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficeNoticeProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSelfRecommendedZMObjectListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceFocusImagesProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceImagePhotoListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceImageVideoListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpacePraiseCountProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetVisitedUserListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetVisitorMessageListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.SpaceDoPraiseProtocol;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.VisitorMessage;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.model.ZMObjectVisitedEntry;
import com.zhima.data.model.ZMSpace;
import com.zhima.data.model.ZMSpaceVideo;

/**
 * 芝麻空间绑定管理Service
 * 
 * @ClassName: ZMSpaceService
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-21 下午3:45:39
 */
public class ZMSpaceService extends BaseService {
	private final static String TAG = "ZMSpaceService";
	private static ZMSpaceService mInstance = null;
	private RefreshListData<ZMSpace> mZMSpaceResultList;
	/**
	 * 缓存的空间焦点图片
	 */
	private TreeMap<Long, RefreshListData<ZMObjectImage>> mFocusImagesMap;
	/**
	 * 缓存的影像照片
	 */
	private TreeMap<Long, RefreshListData<ZMObjectImage>> mImagePhotoMap;
	/**
	 * 缓存的影像视频
	 */
	private TreeMap<Long, RefreshListData<ZMSpaceVideo>> mImageVideoMap;
	/**
	 * 缓存的用户自推荐zmobject列表
	 */
	private TreeMap<Long, RefreshListData<ZMObject>> mSelfRecommendedZMObjectMap;
	/**
	 * 缓存的广场推荐zmobject列表
	 */
	private TreeMap<Long, RefreshListData<ZMObject>> mSquareRecommendedZMObjectMap;
	/**
	 * 缓存系统推荐的zmobject列表
	 */
	private TreeMap<Integer, RefreshListData<ZMObject>> mRecommendedZMObjectMap;

	/**
	 * 空间列表缓存
	 */
	private TreeMap<Long, RefreshListData<ZMSpace>> mZMSpaceObjectMap;
	/**
	 * 缓存 看看谁来过 记录
	 */
	private TreeMap<Long, RefreshListData<ZMObjectVisitedEntry>> mZMObjectVisitedEntryMap;
	private TreeMap<Long, RefreshListData<VisitorMessage>> mVisitorMessageMap;

	private ZMSpaceService(Context c) {
		super(c);
	}

	public static ZMSpaceService getInstance(Context c) {
		if (mInstance == null) {
			mInstance = new ZMSpaceService(c);
			mInstance.onCreate();
		}
		
		return mInstance;
	}

	@Override
	public void onCreate() {
		mFocusImagesMap = new TreeMap<Long, RefreshListData<ZMObjectImage>>();
		mImagePhotoMap = new TreeMap<Long, RefreshListData<ZMObjectImage>>();
		mImageVideoMap = new TreeMap<Long, RefreshListData<ZMSpaceVideo>>();
		mSelfRecommendedZMObjectMap = new TreeMap<Long, RefreshListData<ZMObject>>();
		mSquareRecommendedZMObjectMap = new TreeMap<Long, RefreshListData<ZMObject>>();
		mRecommendedZMObjectMap = new TreeMap<Integer, RefreshListData<ZMObject>>();
		mZMObjectVisitedEntryMap = new TreeMap<Long, RefreshListData<ZMObjectVisitedEntry>>();
		mVisitorMessageMap = new TreeMap<Long, RefreshListData<VisitorMessage>>();
		mZMSpaceObjectMap = new TreeMap<Long, RefreshListData<ZMSpace>>();
	}
	
	/**
	 * @Title: getUserBundlingSpaces
	 * @Description: 获取用户绑定的空间列表 (自己)
	 * @param callBack
	 * @return void
	 */
	public void getUserBundlingSpaces(IHttpRequestCallback callBack) {
		GetBindSpacesProtocolHandler handler = new GetBindSpacesProtocolHandler(mContext, callBack);
		handler.getSpaces();
	}
	/**
	 * @Title: getUserBundlingSpaces
	 * @Description: 获取用户绑定的空间列表 
	 * @param userId
	 * @param callBack
	 * @return void
	 */
	public void getBundlingSpaces(long userId,IHttpRequestCallback callBack) {
		GetBindSpacesProtocolHandler handler = new GetBindSpacesProtocolHandler(mContext, callBack);
		handler.getSpaces(userId);
	}
	
	/**
	 * 获取缓存的绑定空间列表 
	 * @Title: getCacheUserSpaceList 
	 * @Description: TODO 
	 * @param id 
	 * @return 绑定空间列表
	 */ 
	public RefreshListData<ZMSpace> getCacheUserSpaceList(long userId){
		if (mZMSpaceObjectMap.containsKey(userId)) {
			return mZMSpaceObjectMap.get(userId);
		} else {
			return new RefreshListData<ZMSpace>();
		}
	}
	
	/**
	 * 添加缓存绑定空间
	 * @Title: addSpace2Cache
	 * @Description: TODO
	 * @param userId
	 * @param spaceList
	 */
	public void addSpace2Cache(long userId, ArrayList<ZMSpace> spaceList) {
		RefreshListData<ZMSpace> spaceRefreshData = new RefreshListData<ZMSpace>();
		spaceRefreshData.add(spaceList);
		mZMSpaceObjectMap.put(userId, spaceRefreshData);
	}

	/**
	 * @Title: getCacheFocusImages
	 * @Description: 得到缓存的空间焦点图列表
	 * @param zmObject
	 * @returngetUserBundlingSpaces RefreshListData<ZMObjectImage>
	 */
	public RefreshListData<ZMObjectImage> getCacheFocusImages(ZMObject zmObject) {
		if (mFocusImagesMap.containsKey(zmObject.getId())) {
			return mFocusImagesMap.get(zmObject.getId());
		} else {
			return new RefreshListData<ZMObjectImage>();
		}
	}

	/**
	 * @Title: getFocusImages
	 * @Description: 得到空间焦点图列表
	 * @param zmObject 对象
	 * @param refreshed 是否刷新缓存的数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getFocusImages(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMObjectImage> list = null;
		if (mFocusImagesMap.containsKey(zmObject.getId())) {
			list = mFocusImagesMap.get(zmObject.getId());
		} else {
			list = new RefreshListData<ZMObjectImage>();
			mFocusImagesMap.put(zmObject.getId(), list);
		}
		GetSpaceFocusImagesProtocol protocol = new GetSpaceFocusImagesProtocol(mContext, refreshed, list, callBack);
		protocol.getFocusImages(zmObject);
	}

	/**
	 * @Title: getCacheImagePhotoList
	 * @Description: 得到缓存的影像照片列表
	 * @param zmObject
	 * @return RefreshListData<ZMObjectImage>
	 */
	public RefreshListData<ZMObjectImage> getCacheImagePhotoList(ZMObject zmObject) {
		return getCacheImagePhotoList(zmObject.getId());
	}
	
	public RefreshListData<ZMObjectImage> getCacheImagePhotoList(long zmObjectId) {
		if (mImagePhotoMap.containsKey(zmObjectId)) {
			return mImagePhotoMap.get(zmObjectId);
		} else {
			return new RefreshListData<ZMObjectImage>();
		}
	}

	/**
	 * @Title: getImagePhotoList
	 * @Description: 获取影像照片列表
	 * @param zmObject 对象
	 * @param refreshed 是否刷新缓存的数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getImagePhotoList(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMObjectImage> list = null;
		if (mImagePhotoMap.containsKey(zmObject.getId())) {
			list = mImagePhotoMap.get(zmObject.getId());
		} else {
			list = new RefreshListData<ZMObjectImage>();
			mImagePhotoMap.put(zmObject.getId(), list);
		}
		GetSpaceImagePhotoListProtocol protocol = new GetSpaceImagePhotoListProtocol(mContext, refreshed, list,
				callBack);
		protocol.getImagePhotoList(zmObject);
	}

	/**
	 * @Title: getImageVideoList
	 * @Description: 获取影像视频列表
	 * @param zmObject 对象
	 * @param refreshed 是否刷新缓存的数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getImageVideoList(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMSpaceVideo> list = null;
		if (mImageVideoMap.containsKey(zmObject.getId())) {
			list = mImageVideoMap.get(zmObject.getId());
		} else {
			list = new RefreshListData<ZMSpaceVideo>();
			mImageVideoMap.put(zmObject.getId(), list);
		}
		GetSpaceImageVideoListProtocol protocol = new GetSpaceImageVideoListProtocol(mContext, refreshed, list,
				callBack);
		protocol.getImageVideoList(zmObject);
	}

	/**
	 * @Title: getCacheSelfRecommendedSpaceList
	 * @Description: 取缓存的自定义文字推荐空间
	 * @param zmObject
	 * @return RefreshListData<ZMObject>
	 */
	public RefreshListData<ZMObject> getCacheSelfRecommendedSpaceList(ZMObject zmObject) {
		if (mSelfRecommendedZMObjectMap.containsKey(zmObject.getId())) {
			return mSelfRecommendedZMObjectMap.get(zmObject.getId());
		} else {
			return new RefreshListData<ZMObject>();
		}
	}

	/**
	 * @Title: getSelfRecommendedSpaceList
	 * @Description: 获取用户自推荐空间(上栏纯文字广告)(新空间模型调用)
	 * @param currentZMObject 当前ZMObject对象
	 * @param refreshed 是否刷新数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getSelfRecommendedSpaceList(ZMObject currentZMObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMObject> list = null;
		int objectType = currentZMObject.getZMObjectType();
		if (!mSelfRecommendedZMObjectMap.containsKey(currentZMObject.getId())) {
			list = new RefreshListData<ZMObject>();
			mSelfRecommendedZMObjectMap.put(currentZMObject.getId(), list);
		} else {
			list = mSelfRecommendedZMObjectMap.get(currentZMObject.getId());
		}
		GetSelfRecommendedZMObjectListProtocol protocol = new GetSelfRecommendedZMObjectListProtocol(mContext,
				refreshed, list, callBack);
		protocol.getSelfRecommendedList(objectType, currentZMObject.getRemoteId(), SystemConfig.RECOMMEND_COUNT);
	}

	/**
	 * @Title: getSquareRecommendedSpaceList
	 * @Description: 获取广场推荐空间(新空间模型调用)
	 * @param currentZMObject 当前ZMObject对象
	 * @param refreshed 是否刷新数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getSquareRecommendedSpaceList(ZMObject currentZMObject, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMObject> list = null;
		int objectType = currentZMObject.getZMObjectType();
		if (!mSquareRecommendedZMObjectMap.containsKey(currentZMObject.getId())) {
			list = new RefreshListData<ZMObject>();
			mSquareRecommendedZMObjectMap.put(currentZMObject.getId(), list);
		} else {
			list = mSquareRecommendedZMObjectMap.get(currentZMObject.getId());
		}
		GetSelfRecommendedZMObjectListProtocol protocol = new GetSelfRecommendedZMObjectListProtocol(mContext,
				refreshed, list, callBack);
		protocol.getSquareRecommendedList(objectType, currentZMObject.getRemoteId());
	}

	/**
	 * @Title: getBottomRecommendedList
	 * @Description: 从服务器获取推荐ZMObject列表
	 * @param needObjectType 需要获取的ZMObject对象类型
	 * @param currentZMObject 当前ZMObject对象
	 * @param count 需要获取的推荐对象数目
	 * @param cityId 调用者所在行政区划的ID。如忽略则默认使用登录用户绑定的区域ID
	 * @param refreshed 是否刷新数据
	 * @param geo gps
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getBottomRecommendedList(int needObjectType, ZMObject currentZMObject, int count, long cityId,
			GeoCoordinate geo, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<ZMObject> list = null;
		if (!mRecommendedZMObjectMap.containsKey(needObjectType)) {
			list = new RefreshListData<ZMObject>();
			mRecommendedZMObjectMap.put(needObjectType, list);
		} else {
			list = mRecommendedZMObjectMap.get(needObjectType);
		}
		GetBottomRecommendedListProtocol protocol = new GetBottomRecommendedListProtocol(mContext, refreshed, list,
				callBack);
		protocol.getRecommendedList(needObjectType, count,
				ZMObjectKind.getZMObjectType(currentZMObject.getZMObjectType()), currentZMObject.getRemoteId(), geo,
				cityId);
	}

	/**
	 * @Title: getOfficialNotice
	 * @Description: 获取官方公告详情
	 * @param noticeId 公告Id
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getOfficialNotice(long noticeId, IHttpRequestCallback callBack) {
		GetOfficeNoticeProtocol protocol = new GetOfficeNoticeProtocol(mContext, callBack);
		protocol.getOfficeNotice(noticeId);
	}

	/**
	 * @Title: getOfficialNoticeList
	 * @Description: 获取官方公告列表
	 * @param regionId cityId
	 * @param objectType 空间类型
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getOfficialNoticeList(long regionId, int objectType, RefreshListData<Notice> list,
			IHttpRequestCallback callBack) {
		GetOfficialNoticeListProtocol protocol = new GetOfficialNoticeListProtocol(mContext, list, callBack);
		protocol.getOfficialNoticeList(regionId, objectType);
	}

	/**
	 * @Title: getCacheZMObjectVisitedEntryList
	 * @Description: 从本地缓存获取 看看谁来过 访客记录
	 * @param zmObject
	 * @return
	 */
	public RefreshListData<ZMObjectVisitedEntry> getCacheVisitedUserList(ZMObject zmObject) {
		if (mZMObjectVisitedEntryMap.containsKey(zmObject.getId())) {
			return mZMObjectVisitedEntryMap.get(zmObject.getId());
		} else {
			return new RefreshListData<ZMObjectVisitedEntry>();
		}
	}

	/**
	 * @Title: getVisitedUserList
	 * @Description: 看看谁来过 访客记录
	 * @param zmObject zmObject
	 * @param refreshed 是否刷新数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getVisitedUserList(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		if (zmObject == null) {
			return;
		}
		RefreshListData<ZMObjectVisitedEntry> list = null;
		if (!mZMObjectVisitedEntryMap.containsKey(zmObject.getId())) {
			list = new RefreshListData<ZMObjectVisitedEntry>();
			mZMObjectVisitedEntryMap.put(zmObject.getId(), list);
		} else {
			list = mZMObjectVisitedEntryMap.get(zmObject.getId());
		}
		GetVisitedUserListProtocol protocol = new GetVisitedUserListProtocol(mContext, refreshed, list, callBack);
		protocol.getVisitedUserList(zmObject);
	}

	/**
	 * @Title: addVisiteMessage
	 * @Description: 看看谁来过 添加留言
	 * @param zmObject 对象
	 * @param content 留言内容
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void addVisiteMessage(ZMObject zmObject, String content, IHttpRequestCallback callBack) {
		AddSpaceVisiteMessageProtocol protocol = new AddSpaceVisiteMessageProtocol(mContext, callBack);
		protocol.addMessage(zmObject, content);
	}

	/**
	 * @Title: getVisitorMessageList
	 * @Description: 看看谁来过 访客留言
	 * @param visiteId 访问记录id
	 * @param refreshed 是否刷新数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getVisitorMessageList(long visiteId, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<VisitorMessage> list = null;
		if (mVisitorMessageMap.containsKey(visiteId)) {
			list = mVisitorMessageMap.get(visiteId);
		} else {
			list = new RefreshListData<VisitorMessage>();
			mVisitorMessageMap.put(visiteId, list);
		}
		GetVisitorMessageListProtocol protocol = new GetVisitorMessageListProtocol(mContext, refreshed, list, callBack);
		protocol.getVisitorMessages(visiteId);
	}
	
	/**
	 * @Title: getPraiseCount
	 * @Description: 从服务器获取赞数量(新空间模型调用)
	 * @param zmObject 对象
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getPraiseCount(ZMObject zmObject, IHttpRequestCallback callBack) {
		GetSpacePraiseCountProtocol protocol = new GetSpacePraiseCountProtocol(mContext, callBack);
		protocol.getPraiseCount(zmObject);
	}

	/**
	 * @Title: doPraise
	 * @Description: 加"赞"操作(新空间模型调用)
	 * @param zmObject
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void doPraise(ZMObject zmObject, IHttpRequestCallback callBack) {
		SpaceDoPraiseProtocol protocol = new SpaceDoPraiseProtocol(mContext, callBack);
		protocol.doPraise(zmObject);
	}
	
	@Override
	public void clear() {
		if (mFocusImagesMap != null) {
			mFocusImagesMap.clear();
		}
		if (mImagePhotoMap != null) {
			mImagePhotoMap.clear();
		}
		if (mImageVideoMap != null) {
			mImageVideoMap.clear();
		}
		if (mSelfRecommendedZMObjectMap != null) {
			mSelfRecommendedZMObjectMap.clear();
		}
		if (mSquareRecommendedZMObjectMap != null) {
			mSquareRecommendedZMObjectMap.clear();
		}
		if (mZMObjectVisitedEntryMap != null) {
			mZMObjectVisitedEntryMap.clear();
		}
		if (mRecommendedZMObjectMap != null) {
			mRecommendedZMObjectMap.clear();
		}
		if (mVisitorMessageMap != null) {
			mVisitorMessageMap.clear();
		}
	}

	@Override
	public void onDestroy() {
		Logger.getInstance(TAG).debug("ZMSpaceService onDestroy");
		clear();
		mFocusImagesMap = null;
		mImagePhotoMap = null;
		mImageVideoMap = null;
		mSelfRecommendedZMObjectMap = null;
		mSquareRecommendedZMObjectMap = null;
		mRecommendedZMObjectMap = null;
		mVisitorMessageMap = null;
		mZMObjectVisitedEntryMap = null;
	}
}
