/* 
 * @Title: ScanningcodeProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpUtils;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoContact;
import com.zhima.base.protocol.vo.VoCorrection;
import com.zhima.base.protocol.vo.VoFavorite;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Joke;
import com.zhima.data.model.Notice;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UnknownObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectFactory;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.model.ZMProductObject;
import com.zhima.data.service.ScanningcodeService;

/**
* @ClassName: ScanningcodeProtocolHandler
* @Description: 扫码协议
* @author liubingsr
* @date 2012-7-12 下午2:27:34
*
*/
public final class ScanningcodeProtocolHandler {
	private static Gson gson = null;    
    static {  
        if (gson == null) {  
            gson = new Gson();  
        }  
    }
	public final static class GetZMObjectProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetZMObjectProtocol";
		private ZMObject mZMObject = null;
		private String mZMCode = null;
		
		public GetZMObjectProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		public ZMObject getZMObject() {
			return mZMObject;
		}
		
		public void getZMObjectByCode(String code) {
			mZMCode = code;
			mSubUrl = "space/scan?code=%s";
			String url = mBaseUrl + String.format(mSubUrl, HttpUtils.urlEncode(code,"utf-8"));
			getZMObject(url);
		}
		
		public void getZMObjectById(long remoteId, int zmObjectType) {
			mSubUrl = mBaseUrl + String.format("space/%s/%d", ZMObjectKind.getZMObjectType(zmObjectType), remoteId);
			getZMObject(mSubUrl);
		}
		
		private void getZMObject(String url) {
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_ZMOBJECT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray array = mResponeVO.getJSONArray("items");
						if (array != null && array.length() > 0) {
							JSONObject zmJson = array.getJSONObject(0);
							int objectgType = ZMObjectKind.getZMObjectType(mResponseHeader.getKind());
							mZMObject = ZMObjectFactory.create(zmJson);
							if (mZMObject != null && TextUtils.isEmpty(mZMObject.getZMCode())) {
								mZMObject.setZMCode(mZMCode);
							}
						}
					} else {
						// 没有数据
					}
				} else {
					// 本地解码
					mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_ERROR;
					mZMObject = new UnknownObject();
					mZMObject.setZMCode(mZMCode);
					mZMObject.setZMObjectType(ZMObjectKind.UNKNOWN_OBJECT);
					Logger.getInstance(TAG).debug("没有zmobject。数据包:<" + mJson + ">");
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			if (mZMObject != null && !TextUtils.isEmpty(mZMObject.getZMCode())) {
				// 保存进本地sqlite  TODO
				ScanningcodeService.getInstance(mContext).addZMObject(mZMObject);
			}
		}
	}
	/**
	* @ClassName: GetZMObjectAlbumListProtocol
	* @Description: 获取相册协议
	* @author liubingsr
	* @date 2012-7-13 下午3:16:33
	*
	*/
	public final static class GetZMObjectAlbumListProtocol extends ListProtocolHandlerBase<ZMObjectImage> {
		private final static String TAG = "GetZMObjectAlbumListProtocol";
		private ZMObject mZMObject;
		
		public GetZMObjectAlbumListProtocol(Context context, boolean refreshed,
				RefreshListData<ZMObjectImage> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		
		public void getZMObjectAlbumList(ZMObject zmObject) {
			mZMObject = zmObject;
			mSubUrl = "space/%s/%d/image?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()), zmObject.getRemoteId(), mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseImages(mJson);
			return true;
		}

		/**
		* @Title: parseImage
		* @Description: 解析数据包得到图片信息
		* @param json
		* @return ArrayList<ZMObjectImage>
		*/
		private ArrayList<ZMObjectImage> parseImages(String json) {
			ArrayList<ZMObjectImage> imageList = new ArrayList<ZMObjectImage>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ZMObjectImage image = ZMObjectImage.parse(item);
								if (image != null) {
									image.setZMObjectId(mZMObject.getId());
									imageList.add(image);
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
			return imageList;
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
//				for (ZMObjectImage image : mReceiveDataList) {
//					// 存入本地缓存
//					ScanningcodeService.getInstance(mContext).addZMObjectImage(image);
//				}
			}			
		}		
	}
	/**
	* @ClassName: GetPraiseCountProtocol
	* @Description: 从服务器获取赞次数协议
	* @author liubingsr
	* @date 2012-7-13 下午4:54:29
	*
	*/
	public final static class GetPraiseCountProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetPraiseCountProtocol";
		private ZMObject mZMObject;
		private PraiseInfo mPraiseInfo = null;
		/**
		* <p>Title: </p>
		* <p>Description: </p>
		* @param context
		* @param callBack 结果通知回调
		*/
		public GetPraiseCountProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public PraiseInfo getPraiseInfo() {
			return mPraiseInfo;
		}
		
		public void getPraiseCount(ZMObject zmObject) {
			mZMObject = zmObject;
			mSubUrl = "praise/%s/%d";//"space/%d/image";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()), zmObject.getRemoteId());
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_ZMOBJECT_PRAISE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mPraiseInfo = PraiseInfo.parse(objArray.getJSONObject(0).toString());
							mPraiseInfo.setZMObjectId(mZMObject.getId());
						}						
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: DoPraiseProtocol
	* @Description: 发起赞操作
	* @author liubingsr
	* @date 2012-7-16 上午11:46:26
	*
	*/
	public final static class DoPraiseProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DoPraiseProtocol";
		private ZMObject mZMObject;
		private int mPraiseType;
		private PraiseInfo mPraiseInfo = null;

		public DoPraiseProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getPraiseInfo
		* @Description: 服务端返回新的赞信息
		* @return
		* PraiseInfo
		*/
		public PraiseInfo getPraiseInfo() {
			return mPraiseInfo;
		}
		
		public void doPraise(ZMObject zmObject, int praiseType) {
			mZMObject = zmObject;
			mPraiseType = praiseType;
			//space/{spaceKind}/{spaceId}/praise/{praiseType}
			mSubUrl = "praise/%s/%d/%s";//"space/%d/image";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()), zmObject.getRemoteId(), PraiseKind.getPraiseType(praiseType));
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							int newCount = objArray.getInt(0);
							mPraiseInfo = new PraiseInfo();
							mPraiseInfo.setPraise(mPraiseType, newCount);
							mPraiseInfo.setZMObjectId(mZMObject.getId());
						}						
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: GetNoticeDigestListProtocol
	* @Description: 获取公告摘要列表协议
	* @author liubingsr
	* @date 2012-8-25 下午2:09:57
	*
	*/
	public final static class GetNoticeDigestListProtocol extends ListProtocolHandlerBase<Notice> {
		private final static String TAG = "GetNoticeDigestListProtocol";
		private long mRegionId;
		
		public GetNoticeDigestListProtocol(Context context,boolean refreshed,
				RefreshListData<Notice> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		public void getDigestList(GeoCoordinate geo) {
			getDigestList(null, 0, geo);
		}
		/**
		* @Title: getDigestList
		* @Description: 获取公告摘要列表
		* @param zmObject
		* @param cityId
		* @param geo gps坐标
		* @return void
		*/
		public void getDigestList(ZMObject zmObject, long cityId, GeoCoordinate geo) {
//			mSubUrl = "space/zmcode/%s/notice?cityId=%d&latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f";
			mSubUrl = "space/notice?latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f";//cityId=%d& cityId, 
			String url = mBaseUrl + String.format(mSubUrl, geo.getLatitude(), geo.getLongitude(), geo.getGdLatitude(), geo.getGdLongitude());
			if (cityId > 0) {
				url += String.format("&cityId=%d", cityId);
			}
			if (zmObject != null) {
				url += String.format("&zmCode=%s", HttpUtils.urlEncode(zmObject.getZMCode(), "utf-8"));
			}
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_NOTICE_DIGEST_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseNotice(mJson);
			return true;
		}

		/**
		* @Title: parseNotice
		* @Description: 解析数据包得到公告信息
		* @param json
		* @return ArrayList<Notice>
		*/
		private ArrayList<Notice> parseNotice(String json) {
			ArrayList<Notice> noticeList = new ArrayList<Notice>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								Notice notice = Notice.parse(item);
								if (notice != null) {
									noticeList.add(notice);
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
			return noticeList;
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
	* @ClassName: GetOfficialNoticeListProtocol
	* @Description: 官方公告列表协议
	* @author liubingsr
	* @date 2012-7-16 下午2:59:19
	*
	*/
	public final static class GetOfficialNoticeListProtocol extends ListProtocolHandlerBase<Notice> {
		private final static String TAG = "GetOfficialNoticeListProtocol";
		private long mRegionId;
		
		public GetOfficialNoticeListProtocol(Context context,boolean refreshed,
				RefreshListData<Notice> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getOfficialNoticeList
		* @Description: 获取官方公告列表
		* @param regionId 区域id
		* @return void
		*/
		public void getOfficialNoticeList(long regionId) {
			mRegionId = regionId;
			mSubUrl = "notice/official/by_city/%d?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, regionId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_OFFICIAL_NOTICE_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseNotice();
			return true;
		}

		/**
		* @Title: parseNotice
		* @Description: 解析数据包得到公告信息
		* @param json
		* @return ArrayList<Notice>
		*/
		private ArrayList<Notice> parseNotice() {
			ArrayList<Notice> noticeList = new ArrayList<Notice>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								Notice notice = Notice.parse(item);
								if (notice != null) {
									noticeList.add(notice);
								}
							}
							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
							mDataList.setLastPage(true);
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
			return noticeList;
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
//				for (Notice notice : mReceiveDataList) {
//					// 存入本地缓存
//					ScanningcodeService.getInstance(mContext).addOfficialNotice(notice);
//				}
			}			
		}		
	}	
	/**
	* @ClassName: GetRecommendedZMObjectListProtocol
	* @Description: 推荐ZMObject列表协议
	* @author liubingsr
	* @date 2012-7-20 下午12:07:44
	*
	*/
	public final static class GetRecommendedZMObjectListProtocol extends ListProtocolHandlerBase<ZMObject> {
		private final static String TAG = "GetRecommendedZMObjectListProtocol";
		private int mNeedObjectType = 0;
		
		public GetRecommendedZMObjectListProtocol(Context context, boolean refreshed,RefreshListData<ZMObject> data, IHttpRequestCallback callBack) {
			super(context, refreshed,data, callBack);
		}
		/**
		* @Title: getRecommendedZMObjectList
		* @Description: 获取推荐对象列表
		* @param zmObjectType 需要获取的推荐对象类型
		* @param count 需要获取的推荐对象数目
		* @param sourceSpaceKind 查看的空间类型名称
		* @param sourceSpaceId 正在查看的空间ID
		* @param geo 调用者的gps坐标值值
		* @param cityId 调用者所在行政区划的ID。如忽略则默认使用登录用户绑定的区域ID
		* @return void
		*/
		public void getRecommendedZMObjectList(int zmObjectType, int count, String sourceSpaceKind, long sourceSpaceId, GeoCoordinate geo, long cityId) {
			mNeedObjectType = zmObjectType;
			String tempUrl = "space/%s/recommended?size=%d";			
			mSubUrl = mBaseUrl + String.format(tempUrl, ZMObjectKind.getZMObjectType(zmObjectType), count);
			if (!TextUtils.isEmpty(sourceSpaceKind) && sourceSpaceId > 0) {
				mSubUrl += String.format("&sourceSpaceKind=%s", sourceSpaceKind);
				mSubUrl += String.format("&sourceSpaceId=%d", sourceSpaceId);
			}			
			if (geo != null) {
				mSubUrl += String.format("&latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f",
						geo.getLatitude(), geo.getLongitude(), geo.getGdLatitude(), geo.getGdLongitude());
			}
			if (cityId > 0) {
				mSubUrl += String.format("&cityId=%d", cityId);
			}			
			RequestInfo info = new RequestInfo(mSubUrl);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseZMObject();
			return true;
		}

		/**
		* @Title: parseZMObject
		* @Description: 解析数据包得到推荐zmobject信息
		* @param json
		* @return ArrayList<ZMObject>
		*/
		private ArrayList<ZMObject> parseZMObject() {
			ArrayList<ZMObject> zmObjectList = new ArrayList<ZMObject>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							int objectgType = ZMObjectKind.getZMObjectType(mResponseHeader.getKind());
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								ZMObject zmObject = null;
								item = objArray.getJSONObject(index);
								objectgType = mNeedObjectType;
								zmObject = ZMObjectFactory.create(item);
								if (zmObject != null) {
//									zmObject.setZMObjectType(mNeedObjectType);
									zmObjectList.add(zmObject);
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
			return zmObjectList;
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
//				for (ZMObject zmObject : mReceiveDataList) {
//					// 存入缓存
//					ScanningcodeService.getInstance(mContext).addRecommendedZMObject(zmObject);
//				}
			}			
		}		
	}
	/**
	* @ClassName: GetNearbySpaceListProtocol
	* @Description: 获取周边空间协议
	* @author liubingsr
	* @date 2012-9-26 下午2:49:47
	*
	*/
	public final static class GetNearZMObjectListProtocol extends ListProtocolHandlerBase<ZMObject> {
		private final static String TAG = "GetNearZMObjectListProtocol";
		private int mNeedObjectType = 0;
		private final static int DEFAULT_COUNT = 3;
		
		public GetNearZMObjectListProtocol(Context context, boolean refreshed, RefreshListData<ZMObject> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getNearZMObjectList
		* @Description: 获取周边推荐空间列表
		* @param zmObjectType
		* @param zmObject
		* @param geo
		* @param cityId
		* @return void
		*/
		public void getNearZMObjectList(int zmObjectType, ZMObject zmObject, long cityId, GeoCoordinate geo) {
			mNeedObjectType  = zmObjectType;
			mSubUrl = "space/%s/nearby?sourceSpaceKind=%s&sourceSpaceId=%d&latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f&pageSize=%d";
			String url = mBaseUrl + String.format(mSubUrl, ZMObjectKind.getZMObjectType(zmObjectType), ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()),
					zmObject.getRemoteId(), geo.getLatitude(), geo.getLongitude(), geo.getGdLatitude(), geo.getGdLongitude(), DEFAULT_COUNT);
			if (cityId > 0) {
				url += String.format("&cityId=%d", cityId);
			}
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_NEAR_ZMOBJECT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			mReceiveDataList = parseZMObject();
			return true;
		}

		/**
		* @Title: parseZMObject
		* @Description: 解析数据包得到信息
		* @param json
		* @return ArrayList<ZMObject>
		*/
		private ArrayList<ZMObject> parseZMObject() {
			ArrayList<ZMObject> zmObjectList = new ArrayList<ZMObject>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							int objectgType = ZMObjectKind.getZMObjectType(mResponseHeader.getKind());
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								ZMObject zmObject = null;
								item = objArray.getJSONObject(index);
								objectgType = mNeedObjectType;
								zmObject = ZMObjectFactory.create(item);
								if (zmObject != null) {
//									zmObject.setZMObjectType(mNeedObjectType);
									zmObjectList.add(zmObject);
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
			return zmObjectList;
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
	* @ClassName: GetRecommendedJokeListProtocol
	* @Description: 获得推荐知趣协议
	* @author liubingsr
	* @date 2012-9-19 下午4:26:36
	*
	*/
	public final static class GetJokeListProtocol extends ListProtocolHandlerBase<Joke> {
		private final static String TAG = "GetJokeListProtocol";
		
		public GetJokeListProtocol(Context context, boolean refreshed, RefreshListData<Joke> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getJokeList
		* @Description: 获取推荐知趣
		* @return void
		*/
		public void getJokeList() {
			mSubUrl = "joke?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_JOKE_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseJoke();
			return true;
		}

		/**
		* @Title: parseJoke
		* @Description: 解析数据包得到知趣信息
		* @param json
		*/
		private ArrayList<Joke> parseJoke() {
			ArrayList<Joke> jokeList = new ArrayList<Joke>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								Joke joke = Joke.parse(item);
								if (joke != null) {
									jokeList.add(joke);
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
			return jokeList;
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
	* @ClassName: GetJokeProtocol
	* @Description: 趣闻详情协议
	* @author liubingsr
	* @date 2012-10-14 上午11:15:17
	*
	*/
	public final static class GetJokeProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetJokeProtocol";
		private long mJokeId;
		private Joke mJoke;

		public GetJokeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mJoke = null;
			mJokeId = 0;
		}
		/**
		* @Title: getJoke
		* @Description: 服务端返回的趣闻信息
		* @return
		* Joke
		*/
		public Joke getJoke() {
			return mJoke;
		}
		
		public void getJoke(long jokeId) {
			mJokeId = jokeId;
			mSubUrl = "joke/%d";
			String url = mBaseUrl + String.format(mSubUrl, jokeId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_JOKE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mJoke = Joke.parse(objArray.getJSONObject(0));
							if (mJoke != null) {
								mJoke.setId(mJokeId);
							}
						}						
					} else {
						// 没有数据
					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: AddFavoriteProtocol
	* @Description: 添加收藏协议 TODO
	* @author liubingsr
	* @date 2012-8-13 下午3:59:37
	*
	*/
	public final static class AddFavoriteProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddFavoriteProtocol";
		private long mFavoriteId = 0;

		public AddFavoriteProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: addFavorite
		* @Description: 添加一条收藏记录
		* @param entry 收藏项目
		* @return void
		*/
		public void addFavorite(FavoriteEntry entry) {
			mSubUrl = "favorite";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			
			VoFavorite vo = new VoFavorite();
			vo.setTitle(entry.getTitle());
			vo.setContent(entry.getContent());
			vo.setTargetType(entry.getObjectType());
			vo.setTargetId(entry.getObjectId());
			vo.setImageUrl(entry.getImageUrl());
			
			String json = gson.toJson(vo);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_FAVORITE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {				
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mFavoriteId = objArray.getLong(0);
							return true;
						}
					}					
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return true;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: DeleteFavoriteProtocol
	* @Description: 删除收藏协议
	* @author liubingsr
	* @date 2012-8-13 下午4:06:51
	*
	*/
	public final static class DeleteFavoriteProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteFavoriteProtocol";

		public DeleteFavoriteProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: deleteFavorite
		* @Description: 删除一条收藏记录
		* @param favoriteId 收藏条目id
		* @return void
		*/
		public void deleteFavorite(long favoriteId) {
			mSubUrl = "visited";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = "{\"userId\":" + favoriteId + "\"}";
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_FAVORITE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		public void deleteFavorite(ArrayList<Long> favoriteIdList) {
			mSubUrl = "visited";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = "{\"userId\":" + favoriteIdList.toString() + "\"}";
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_FAVORITE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			return true;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: AddContactProtocol
	* @Description: 保存通讯录协议
	* @author liubingsr
	* @date 2012-9-19 下午10:44:30
	*
	*/
	public final static class AddContactProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddContactProtocol";
		private long mContactEntryId = 0;

		public AddContactProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: addFavorite
		* @Description: 添加一条收藏记录
		* @param entry 收藏项目
		* @return void
		*/
		public void addContact(ContactEntry entry) {
			mSubUrl = "space_contact";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			
			VoContact vo = new VoContact();
			vo.setTitle(entry.getTitle());
			vo.setTelephone(entry.getTelephone());
			vo.setTargetType(entry.getObjectType());
			vo.setTargetId(entry.getObjectId());
			vo.setImageUrl(entry.getImageUrl());
			
			String json = gson.toJson(vo);
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_CONTACT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {			
			// 解析服务器返回的json数据包
			try {				
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							mContactEntryId = objArray.getLong(0);
							return true;
						}
					}					
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: PostCorrectionProtocolHandler
	* @Description: 商品纠错协议
	* @author liubingsr
	* @date 2012-11-13 下午3:43:22
	*
	*/
	public final static class PostCorrectionProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "PostCorrectionProtocolHandler";
		private static Gson gson = null;    
	    static {  
	        if (gson == null) {  
	            gson = new Gson();
	        }  
	    }
		
		public PostCorrectionProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: postCorrection
		* @Description: 意见反馈
		* @param content 内容
		* @param zmProduct 商品对象
		* @return void
		*/
		public void postCorrection(String content, ZMProductObject zmProduct) {
			mSubUrl = "global_feedback";
			String url = mBaseUrl + mSubUrl;
			RequestInfo reqInfo = new RequestInfo(url);
			reqInfo.setRequestType(RequestType.POST);
			
			VoCorrection vo = new VoCorrection();
			vo.setContent(content);
			vo.setTargetId(zmProduct.getRemoteId());
			vo.setTargetType(zmProduct.getZMObjectType());
			vo.setTargetName(zmProduct.getName() +";" + zmProduct.getBarcode());
			String json = gson.toJson(vo);
			reqInfo.setBody(json);
			this.setRequestInfo(reqInfo);
			mProtocolType = ProtocolType.POST_CORRECTION_PROTOCOL;
			mRequestService.sendRequest(this);
		}		
		@Override
		public boolean parse() {
			boolean result = false;
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					result = true;
//					if (!mResponeVO.isNull("items")) {
//						JSONArray ids = mResponeVO.getJSONArray("items");
//						if (ids != null && ids.length() > 0) {
//							if (!ids.isNull(0)) {
//								result = true;
//							}
//						}
//					}
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + mJson + ">", ex);				
			}
			return result;
		}

		@Override
		public void afterParse() {
			// TODO 成功处理
		}
	}
}
