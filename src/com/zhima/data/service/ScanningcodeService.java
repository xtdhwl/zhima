/* 
 * @Title: ScanningcodeService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.AddContactProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.AddFavoriteProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DeleteFavoriteProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetJokeListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetJokeProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNearZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNoticeDigestListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetRecommendedZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.PostCorrectionProtocolHandler;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Joke;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.model.ZMProductObject;
import com.zhima.db.dbcontroller.ZMObjectDbController;
//获取特定类型的周边空间
/**
 * @ClassName: ScanningcodeService
 * @Description: 扫码
 * @author liubingsr
 * @date 2012-7-13 上午9:54:27
 * 
 */
public class ScanningcodeService extends BaseService {
	private final static String TAG = "ScanningcodeService";
	private static ScanningcodeService mInstance = null;

	private ZMObjectDbController mDbController;

	private ArrayList<ZMObject> mZMObjectList;
	private TreeMap<Long, ZMObject> mZMObjectMapById;

	private TreeMap<String, ZMObject> mZMObjectMap;
	/**
	 * 缓存的相册图片
	 */
	private TreeMap<Long, RefreshListData<ZMObjectImage>> mZMObjectAlbumMap;
	/**
	 * 缓存的官方公告
	 */
	private TreeMap<Long, RefreshListData<Notice>> mOfficialNoticeMap;
	/**
	 * 缓存的公告摘要
	 */
	private RefreshListData<Notice> mNoticeDigestList;
	/**
	 * 
	 */
	private RefreshListData<ZMObject> mNearZMObjectList;
	/**
	 * 缓存推荐的zmobject列表
	 */
	private TreeMap<Integer, RefreshListData<ZMObject>> mRecommendedZMObjectMap;
	/**
	 * 缓存的知趣信息
	 */
	private RefreshListData<Joke> mJokeList;

	private ScanningcodeService(Context context) {
		super(context);
		mDbController = new ZMObjectDbController(mContext);
		mZMObjectMap = new TreeMap<String, ZMObject>();
		mZMObjectMapById = new TreeMap<Long, ZMObject>();

		mZMObjectAlbumMap = new TreeMap<Long, RefreshListData<ZMObjectImage>>();

		mNoticeDigestList = new RefreshListData<Notice>();
		mNearZMObjectList = new RefreshListData<ZMObject>();
		mJokeList = new RefreshListData<Joke>(); 
		mOfficialNoticeMap = new TreeMap<Long, RefreshListData<Notice>>();

		mRecommendedZMObjectMap = new TreeMap<Integer, RefreshListData<ZMObject>>();

		onCreate();
	}

	public static ScanningcodeService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ScanningcodeService(context);
		}
//		mInstance.mContext = context;
		return mInstance;
	}

	@Override
	public void onCreate() {
		mZMObjectList = mDbController.reloadAllDataWithOrder();
		if (!mZMObjectList.isEmpty()) {
			for (ZMObject data : mZMObjectList) {
				mZMObjectMap.put(data.getZMCode(), data);
				mZMObjectMapById.put(data.getId(), data);
			}
		}
	}

	/**
	 * @Title: addZMObject
	 * @Description: zmobject对象保存
	 * @param zmObject
	 * @return void
	 */
	public void addZMObject(ZMObject zmObject) {
		mZMObjectMap.put(zmObject.getZMCode(), zmObject);
		mZMObjectMapById.put(zmObject.getId(), zmObject);
//
//		mDbController.updateData(zmObject);
	}

	/**
	 * @Title: getCacheZMObject
	 * @Description: 得到本地缓存的zmobject信息
	 * @param code 码内容
	 * @return ZMObject
	 */
	public ZMObject getCacheZMObject(String code) {
//		System.out.println("mZMObjectMap==null"+(mZMObjectMap==null));
//		System.out.println("code==null"+(code==null));
		if (mZMObjectMap.containsKey(code)) {
			return mZMObjectMap.get(code);
		} else {
			return null;
		}
	}

	/**
	 * @Title: getCacheZMObject
	 * @Description: 得到本地缓存的zmobject信息
	 * @param zmObjectId 对象id
	 * @return ZMObject
	 */
	public ZMObject getCacheZMObject(long zmObjectId) {
		if (mZMObjectMapById.containsKey(zmObjectId)) {
			return mZMObjectMapById.get(zmObjectId);
		} else {
			// for (ZMObject data : mZMObjectList) {
			// if (data.getId() == objectId) {
			// mZMObjectMapById.put(data.getId(), data);
			// return data;
			// }
			// }
			return null;
		}
	}

	/**
	 * @Title: getCacheZMObject
	 * @Description: 得到本地缓存的zmobject信息
	 * @param remoteId 服务器端的id值
	 * @param zmObjectType 类型
	 * @return ZMObject
	 */
	public ZMObject getCacheZMObject(long remoteId, int zmObjectType) {
		long zmObjectId = ZMObject.createLocalId(remoteId, zmObjectType);
		if (mZMObjectMapById.containsKey(zmObjectId)) {
			return mZMObjectMapById.get(zmObjectId);
		} else {
			return null;
		}
	}

	/**
	 * @Title: getScanningInfo
	 * @Description: 从服务器获取扫码结果信息
	 * @param code 码内容
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getScanningInfo(String code, IHttpRequestCallback callBack) {
		GetZMObjectProtocol protocol = new GetZMObjectProtocol(mContext, callBack);
		protocol.getZMObjectByCode(code);
	}

	/**
	 * @Title: getScanningInfo
	 * @Description: 从服务器获取扫码结果信息
	 * @param remoteId
	 * @param zmObjectType
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getScanningInfo(long remoteId, int zmObjectType, IHttpRequestCallback callBack) {
		GetZMObjectProtocol protocol = new GetZMObjectProtocol(mContext, callBack);
		protocol.getZMObjectById(remoteId, zmObjectType);
	}
	/**
	 * @Title: addRecommendedZMObject
	 * @Description: 推荐zmoObject加入缓存
	 * @param zmObject
	 * @return void
	 */
	public void addRecommendedZMObject(ZMObject zmObject) {
		
	}

	/**
	 * @Title: getCacheRecommendedZMObjectList
	 * @Description: 从缓存里获取推荐ZMObject
	 * @param zmObjectType
	 * @return
	 */
	public RefreshListData<ZMObject> getCacheRecommendedZMObjectList(int zmObjectType) {
		if (mRecommendedZMObjectMap.containsKey(zmObjectType)) {
			return mRecommendedZMObjectMap.get(zmObjectType);
		} else {
			return new RefreshListData<ZMObject>();
		}
	}
	/**
	 * @Title: getRecommendedZMObjectList
	 * @Description: 从服务器获取推荐ZMObject列表
	 * @param zmObjectType 需要获取的ZMObject对象类型
	 * @param currentZMObject 当前ZMObject对象
	 * @param count 需要获取的推荐对象数目
	 * @param refreshed 是否刷新数据
	 * @param cityId 调用者所在行政区划的ID。如忽略则默认使用登录用户绑定的区域ID
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getRecommendedZMObjectList(int zmObjectType, ZMObject currentZMObject, int count, boolean refreshed, long cityId, IHttpRequestCallback callBack) {
		RefreshListData<ZMObject> list = null;
		if (!mRecommendedZMObjectMap.containsKey(zmObjectType)) {
			list = new RefreshListData<ZMObject>();
			mRecommendedZMObjectMap.put(zmObjectType, list);
		} else {
			list = mRecommendedZMObjectMap.get(zmObjectType);
		}
		GeoCoordinate geo = new GeoCoordinate();
		GetRecommendedZMObjectListProtocol protocol = new GetRecommendedZMObjectListProtocol(mContext, refreshed, list, callBack);
		protocol.getRecommendedZMObjectList(
				zmObjectType,
				count,
				ZMObjectKind.getZMObjectType(currentZMObject.getZMObjectType()),
				currentZMObject.getRemoteId(), geo, cityId);
	}
//
//	/**
//	 * @Title: getCacheRecommendedZMIdolObjectList
//	 * @Description: 从缓存里获取推荐的知天使
//	 * @return
//	 */
//	public RefreshListData<ZMObject> getCacheRecommendedZMIdolObjectList() {
//		if (mRecommendedZMObjectMap.containsKey(ZMObjectKind.IDOL_OBJECT)) {
//			return mRecommendedZMObjectMap.get(ZMObjectKind.IDOL_OBJECT);
//		} else {
//			return new RefreshListData<ZMObject>();
//		}
//	}

	/**
	* @Title: getRecommendedZMIdolObjectList
	* @Description: 从服务器获取推荐的知天使列表
	* @param count 数量
	* @param sourceSpaceKind 源空间类型
	* @param sourceSpaceId 源空间id
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getRecommendedZMIdolObjectList(int count, int sourceSpaceKind, long sourceSpaceId, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mRecommendedZMObjectMap.remove(ZMObjectKind.IDOL_OBJECT);
//		}
		RefreshListData<ZMObject> list = null;
		if (!mRecommendedZMObjectMap.containsKey(ZMObjectKind.IDOL_OBJECT)) {
			list = new RefreshListData<ZMObject>();
			mRecommendedZMObjectMap.put(ZMObjectKind.IDOL_OBJECT, list);
		} else {
			list = mRecommendedZMObjectMap.get(ZMObjectKind.IDOL_OBJECT);
		}
		String spaceType = ZMObjectKind.getZMObjectType(sourceSpaceKind);
		if (TextUtils.isEmpty(spaceType)) {
			sourceSpaceId = -1;
		}
		GeoCoordinate geo = new GeoCoordinate();
		GetRecommendedZMObjectListProtocol protocol = new GetRecommendedZMObjectListProtocol(mContext, refreshed, list, callBack);
		protocol.getRecommendedZMObjectList(ZMObjectKind.IDOL_OBJECT, count, ZMObjectKind.getZMObjectType(sourceSpaceKind),
				sourceSpaceId, geo, 0);
	}
	/**
	* @Title: getRecommendedZMIdolObjectList
	* @Description: 从服务器获取推荐的知天使列表
	* @param count 数量
	* @param sourceSpaceKind 源空间类型
	* @param sourceSpaceId 源空间id
	* @param geo 坐标值
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getRecommendedZMIdolObjectList(int count, int sourceSpaceKind, long sourceSpaceId, GeoCoordinate geo, boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mRecommendedZMObjectMap.remove(ZMObjectKind.IDOL_OBJECT);
//		}
		RefreshListData<ZMObject> list = null;
		if (!mRecommendedZMObjectMap.containsKey(ZMObjectKind.IDOL_OBJECT)) {
			list = new RefreshListData<ZMObject>();
			mRecommendedZMObjectMap.put(ZMObjectKind.IDOL_OBJECT, list);
		} else {
			list = mRecommendedZMObjectMap.get(ZMObjectKind.IDOL_OBJECT);
		}
		String spaceType = ZMObjectKind.getZMObjectType(sourceSpaceKind);
		if (TextUtils.isEmpty(spaceType)) {
			sourceSpaceId = -1;
		}
		if (geo == null) {
			geo = new GeoCoordinate();
		}
		GetRecommendedZMObjectListProtocol protocol = new GetRecommendedZMObjectListProtocol(mContext, refreshed, list, callBack);
		protocol.getRecommendedZMObjectList(ZMObjectKind.IDOL_OBJECT, count, ZMObjectKind.getZMObjectType(sourceSpaceKind),
				sourceSpaceId, geo, 0);
	}
	/**
	* @Title: getNearZMObject
	* @Description: 获取特定类型的周边推荐空间
	* @param destObjectType 需要的推荐空间类型
	* @param currentZMObject 当前空间对象
	* @param cityId 调用者所在行政区划的ID。如忽略则默认使用登录用户绑定的区域ID
	* @param geo gps坐标值
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getNearZMObject(int destObjectType, ZMObject currentZMObject, long cityId, GeoCoordinate geo, IHttpRequestCallback callBack) {
//		mNearZMObjectList.clear();
		GetNearZMObjectListProtocol protocol = new GetNearZMObjectListProtocol(mContext, true, mNearZMObjectList, callBack);
		protocol.getNearZMObjectList(destObjectType, currentZMObject, cityId, geo);
	}
	/**
	 * @Title: addZMObjectImage
	 * @Description: 相册图片加入本地缓存
	 * @param image 相册图片
	 * @return void
	 */
	public void addZMObjectImage(ZMObjectImage image) {
//		RefreshListData<ZMObjectImage> array;
//		long zmObjectId = image.getZMObjectId();
//		if (mZMObjectAlbumMap.containsKey(zmObjectId)) {
//			array = mZMObjectAlbumMap.get(zmObjectId);
//		} else {
//			array = new RefreshListData<ZMObjectImage>();
//		}
//		array.getMap().put(image.getId(), image);
//		mZMObjectAlbumMap.put(zmObjectId, array);
	}

	/**
	 * @Title: getCacheZMObjectAlbum
	 * @Description: 从本地缓存中获取指定id值的图片
	 * @param imageId 图片id
	 * @return null 本地无数据
	 */
	public ZMObjectImage getCacheZMObjectAlbum(long imageId) {
		Set<Long> keys = mZMObjectAlbumMap.keySet();
		 RefreshListData<ZMObjectImage> images;
		 for (Long key : keys) {
			 images = mZMObjectAlbumMap.get(key);
			 if (images.getMap().containsKey(imageId)) {
				 return images.getMap().get(imageId);
			 }
		 }
		 return null;
	}

	/**
	 * @Title: getCacheZMObjectAlbumList
	 * @Description: 从本地缓存获取zmobject相册图片
	 * @param zmObjectId zmobject对象id
	 * @return
	 */
	public RefreshListData<ZMObjectImage> getCacheZMObjectAlbumList(long zmObjectId) {
		if (mZMObjectAlbumMap.containsKey(zmObjectId)) {
			return mZMObjectAlbumMap.get(zmObjectId);
		} else {
			return new RefreshListData<ZMObjectImage>();
		}
	}

	/**
	 * @Title: getZMObjectAlbumList
	 * @Description: 从服务器获取zmobject相册图片
	 * @param zmObject
	 * @param refreshed 是否刷新数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getZMObjectAlbumList(ZMObject zmObject, boolean refreshed, IHttpRequestCallback callBack) {
		if (zmObject == null) {
			return;
		}
//		if (refreshed) {
//			mZMObjectAlbumMap.remove(zmObject.getId());
//		}
		RefreshListData<ZMObjectImage> list = null;
		if (!mZMObjectAlbumMap.containsKey(zmObject.getId())) {
			list = new RefreshListData<ZMObjectImage>();
			mZMObjectAlbumMap.put(zmObject.getId(), list);
		} else {
			list = mZMObjectAlbumMap.get(zmObject.getId());
		}
		GetZMObjectAlbumListProtocol protocol = new GetZMObjectAlbumListProtocol(mContext, refreshed, list, callBack);
		protocol.getZMObjectAlbumList(zmObject);
	}

	/**
	 * @Title: getPraiseCount
	 * @Description: 从服务器获取赞数量
	 * @param zmObject
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getPraiseCount(ZMObject zmObject, IHttpRequestCallback callBack) {
		GetPraiseCountProtocol protocol = new GetPraiseCountProtocol(mContext, callBack);
		protocol.getPraiseCount(zmObject);
	}

	/**
	 * @Title: doPraise
	 * @Description: 加"赞"操作
	 * @param zmObject
	 * @param praiseType 赞类型
	 * @see com.zhima.base.consts.ZMConsts.PraiseKind
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void doPraise(ZMObject zmObject, int praiseType, IHttpRequestCallback callBack) {
		DoPraiseProtocol protocol = new DoPraiseProtocol(mContext, callBack);
		protocol.doPraise(zmObject, praiseType);
	}

	/**
	 * @Title: addOfficialNotice
	 * @Description: 官方公告加入本地缓存
	 * @param notice 公告
	 * @return void
	 */
	public void addOfficialNotice(Notice notice) {
//		RefreshListData<Notice> list;
//		long regionId = notice.getRegionId();
//		if (mOfficialNoticeMap.containsKey(regionId)) {
//			list = mOfficialNoticeMap.get(regionId);			
//		} else {
//			list = new RefreshListData<Notice>();
//			mOfficialNoticeMap.put(regionId, list);
//		}
//		if (!list.getMap().containsKey(notice.getId()) ) {
//			list.getMap().put(notice.getId(), notice);
//		}
	}

	/**
	 * @Title: getCacheOfficialNoticeList
	 * @Description: 从本地缓存获取官方公告列表
	 * @param regionId 区域id
	 * @return
	 */
//	public RefreshListData<Notice> getCacheOfficialNoticeList(long regionId) {
//		if (mOfficialNoticeMap.containsKey(regionId)) {
//			return mOfficialNoticeMap.get(regionId);
//		} else {
//			return new RefreshListData<Notice>();
//		}
//	}
//	/**
//	* @Title: getCacheNoticeDigestList
//	* @Description: 获取缓存的公告摘要
//	* @return
//	*/
//	public RefreshListData<Notice> getCacheNoticeDigestList() {
//		return mNoticeDigestList;
//	}
	/**
	* @Title: getNoticeDigest
	* @Description: 扫码展示页面获取公告摘要
	* @param zmObject
	* @param cityId 区域ID
	* @param geo gps坐标
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
//	public void getNoticeDigestList(ZMObject zmObject, long cityId, GeoCoordinate geo, boolean refreshed, IHttpRequestCallback callBack) {
////		if (refreshed) {
////			mNoticeDigestList.clear();
////		}
//		GetNoticeDigestListProtocol protocol = new GetNoticeDigestListProtocol(mContext, refreshed, mNoticeDigestList, callBack);
//		protocol.getDigestList(zmObject, cityId, geo);
//	}
//	public void getNoticeDigestList(GeoCoordinate geo, boolean refreshed, IHttpRequestCallback callBack) {
////		if (refreshed) {
////			mNoticeDigestList.clear();
////		}
//		GetNoticeDigestListProtocol protocol = new GetNoticeDigestListProtocol(mContext, refreshed, mNoticeDigestList, callBack);
//		protocol.getDigestList(geo);
//	}
	/**
	 * @Title: getOfficialNoticeList
	 * @Description: 获取官方公告列表
	 * @param regionId 区域id
	 * @param refreshed 是否刷新数据
	 * @param callBack 结果通知回调
	 * @return void
	 */
//	public void getOfficialNoticeList(long regionId, boolean refreshed, IHttpRequestCallback callBack) {
////		if (refreshed) {
////			mOfficialNoticeMap.remove(regionId);
////		}
//		RefreshListData<Notice> list = null;
//		if (!mOfficialNoticeMap.containsKey(regionId)) {
//			list = new RefreshListData<Notice>();
//			mOfficialNoticeMap.put(regionId, list);
//		} else {
//			list = mOfficialNoticeMap.get(regionId);
//		}
//		GetOfficialNoticeListProtocol protocol = new GetOfficialNoticeListProtocol(mContext, refreshed, list, callBack);
//		protocol.getOfficialNoticeList(regionId);
//	}

	/**
	 * @Title: getCacheOfficialNotice
	 * @Description: 从本地缓存中获取Notice信息
	 * @param noticeId 公告id
	 * @return null 本地无数据
	 */
	public Notice getCacheOfficialNotice(long noticeId) {
		 Set<Long> keys = mOfficialNoticeMap.keySet();
		 RefreshListData<Notice> notices;
		 for (Long key : keys) {
			 notices = mOfficialNoticeMap.get(key);
			 if (notices.getMap().containsKey(noticeId)) {
				 return notices.getMap().get(noticeId);
			 }
		 }
		 return null;
	}	
	
	/**
	* @Title: getCacheRecommendedJokeList
	* @Description: 获得本地缓存的推荐知趣
	* @return
	*/
	public RefreshListData<Joke> getCacheRecommendedJokeList() {
		return mJokeList;
	}
	/**
	* @Title: getRecommendedJokes
	* @Description: 获得推荐知趣
	* @param refreshed 是否刷新数据
	* @param callBack 结果通知回调
	* @return void
	*/
	public void getJokeList(boolean refreshed, IHttpRequestCallback callBack) {
//		if (refreshed) {
//			mJokeList.clear();
//		}
		GetJokeListProtocol protocol = new GetJokeListProtocol(mContext, refreshed, mJokeList, callBack);
		protocol.getJokeList();
	}
	/**
	 * @Title: getJoke
	 * @Description: 获取知趣详情
	 * @param jokeId 知趣Id
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getJoke(long jokeId, IHttpRequestCallback callBack) {
		GetJokeProtocol protocol = new GetJokeProtocol(mContext, callBack);
		protocol.getJoke(jokeId);
	}
	/**
	* @Title: postCorrection
	* @Description: 商品纠错
	* @param content 内容
	* @param zmProduct 商品对象
	* @param callBack 结果通知回调
	* @return void
	*/
	public void postCorrection(String content, ZMProductObject zmProduct, IHttpRequestCallback callBack) {
		PostCorrectionProtocolHandler protocol = new PostCorrectionProtocolHandler(mContext, callBack);
		protocol.postCorrection(content, zmProduct);
	}
	// --------------以下是收藏相关接口
	/**
	 * @Title: addFavorite
	 * @Description: 添加收藏(收藏目标是非自有码：3000件商品、非3000件商品)
	 * @param userId 用户id
	 * @param entry 收藏
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void addFavorite(FavoriteEntry entry, IHttpRequestCallback callBack) {
		AddFavoriteProtocol protocol = new AddFavoriteProtocol(mContext, callBack);
		protocol.addFavorite(entry);
	}

	/**
	 * @Title: deleteFavorite
	 * @Description: 删除指定的收藏项目
	 * @param favoriteId 要删除的项目id
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void deleteFavorite(long favoriteId, IHttpRequestCallback callBack) {
		DeleteFavoriteProtocol protocol = new DeleteFavoriteProtocol(mContext, callBack);
		protocol.deleteFavorite(favoriteId);
	}

	/**
	 * @Title: deleteFavorite
	 * @Description: 批量删除收藏项目
	 * @param favoriteIdList 要删除的项目id列表
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void deleteFavorite(ArrayList<Long> favoriteIdList, IHttpRequestCallback callBack) {
		DeleteFavoriteProtocol protocol = new DeleteFavoriteProtocol(mContext, callBack);
		protocol.deleteFavorite(favoriteIdList);
	}

	/**
	 * @Title: getFavoriteList
	 * @Description: 获取用户收藏列表
	 * @param userId 用户id
	 * @param dataList 暂存的收藏
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getFavoriteList(long userId, RefreshListData<FavoriteEntry> dataList, IHttpRequestCallback callBack) {
		// TODO
	}

	// -------------以下是通讯录相关接口
	/**
	 * @Title: addContactEntry
	 * @Description: 保存到通讯录(通讯录条目(通讯录目标类型是：除去3000件商品、非3000件商品之外其它类型的空间))
	 * @param entry 通讯录对象
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void addContact(ContactEntry entry, IHttpRequestCallback callBack) {
		AddContactProtocol protocol = new AddContactProtocol(mContext, callBack);
		protocol.addContact(entry);
	}

	/**
	 * @Title: deleteContactEntry
	 * @Description: 删除指定的通讯录
	 * @param entryId 要删除的条目id
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void deleteContact(long entryId, IHttpRequestCallback callBack) {
		// TODO
	}

	/**
	 * @Title: deleteContactEntry
	 * @Description: 批量删除通讯录
	 * @param entryIdList 要删除的项目id列表
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void deleteContactEntry(ArrayList<Long> entryIdList, IHttpRequestCallback callBack) {
		// TODO
	}

	/**
	 * @Title: getContactList
	 * @Description: 获取通讯录列表
	 * @param userId 用户id
	 * @param dataList 暂存的通讯录
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void getContactList(long userId, RefreshListData<ContactEntry> dataList, IHttpRequestCallback callBack) {
		// TODO
	}

	@Override
	public void onDestroy() {
		if (mZMObjectList != null) {
			mZMObjectList.clear();
			mZMObjectList = null;
		}
		if (mZMObjectMap != null) {
			mZMObjectMap.clear();
			mZMObjectMap = null;
		}
		if (mZMObjectMapById != null) {
			mZMObjectMapById.clear();
			mZMObjectMapById = null;
		}
		clear();
		System.gc();
	}

	@Override
	public void clear() {
		if (mZMObjectAlbumMap != null) {
			mZMObjectAlbumMap.clear();
		}

		if (mOfficialNoticeMap != null) {
			mOfficialNoticeMap.clear();
		}
		if (mNoticeDigestList != null) {
			mNoticeDigestList.clear();
		}
		if (mRecommendedZMObjectMap != null) {
			mRecommendedZMObjectMap.clear();
		}
		if (mNearZMObjectList != null) {
			mNearZMObjectList.clear();
		}
	}
}
