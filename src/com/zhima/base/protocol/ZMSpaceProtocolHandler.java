package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoContent;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.VisitorMessage;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectFactory;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.model.ZMObjectVisitedEntry;
import com.zhima.data.model.ZMSpace;
import com.zhima.data.model.ZMSpaceVideo;
import com.zhima.data.service.ZMSpaceService;

/**
 * 芝麻空间管理相关协议
 * @ClassName: ZMSpaceProtocolHandler
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-21 下午7:00:05
*/
public final class ZMSpaceProtocolHandler {
	
	public final static class GetBindSpacesProtocolHandler extends ProtocolHandlerBase {
		private final static String TAG = "GetBindSpacesProtocolHandler";
		private ArrayList<ZMSpace> mSpaceList;
		private long userId;
		
		public GetBindSpacesProtocolHandler(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public ArrayList<ZMSpace> getSpaceList() {
			return mSpaceList;
		}

		public void getSpaces() {
			mSubUrl = String.format("user/bundling/myspaces");
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_USER_BINDING_SPACES;
			mRequestService.sendRequest(this);
		}
		public void getSpaces(long userId) {
			getSpaces();
		}
//		public void getSpaces(long userId) {
//			mSubUrl = "user/bundling/%d/spaces";
//			String url = mBaseUrl + String.format(mSubUrl, userId);
//			RequestInfo info = new RequestInfo(url);
//			info.setRequestType(RequestType.GET);
//			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.GET_BINDING_SPACES;
//			mRequestService.sendRequest(this);
//		}
		
		@Override
		public boolean parse() {
			mSpaceList = parseResult();
			return true;			
		}
		
		/**
		* @Title: parseResult
		* @Description: 解析数据包
		* @param json
		* @return ArrayList<CouponQueryResult>
		*/
		private ArrayList<ZMSpace> parseResult() {
			ArrayList<ZMSpace> resultList = new ArrayList<ZMSpace>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ZMSpace result = ZMSpace.parse(item);
								if (result != null) {
									resultList.add(result);
								}
							} 
						} 
					} 
				} 
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return resultList;
		}

		@Override
		public void afterParse() {
			ZMSpaceService.getInstance(mContext).addSpace2Cache(userId, mSpaceList);
		}
	}
	
//	/**
//	 * 获取绑定控件列表
//	 * @ClassName: GetBindSpacesListProtocol
//	 * @Description: TODO
//	 * @author yusonglin
//	 * @date 2013-2-2 下午3:46:54
//	*/
//	public final static class GetBindSpacesListProtocol extends ListProtocolHandlerBase<ZMSpace> {
//		private final static String TAG = "GetSpaceFocusImagesProtocol";
//		
//		public GetBindSpacesListProtocol(Context context, boolean refreshed, RefreshListData<ZMSpace> data,
//				IHttpRequestCallback callBack) {
//			super(context, refreshed, data, callBack);
//		}
//		
//		public void getSpaces() {
//			mSubUrl = String.format("user/bundling/myspaces");
//			String url = mBaseUrl + mSubUrl;
//			RequestInfo info = new RequestInfo(url);
//			info.setRequestType(RequestType.GET);
//			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.GET_USER_BINDING_SPACES;
//			mRequestService.sendRequest(this);
//		}
//		public void getSpaces(long userId) {
//			mSubUrl = String.format("user/bundling/%d/spaces");
//			String url = mBaseUrl + String.format(mSubUrl, userId);
//			RequestInfo info = new RequestInfo(url);
//			info.setRequestType(RequestType.GET);
//			this.setRequestInfo(info);
//			mProtocolType = ProtocolType.GET_BINDING_SPACES;
//			mRequestService.sendRequest(this);
//		}
//		
//		@Override
//		public boolean parse() {
//			mReceiveDataList = parseImage();
//			return true;
//		}
//		
//		private ArrayList<ZMSpace> parseImage() {
//			ArrayList<ZMSpace> imageList = new ArrayList<ZMSpace>();
//			try {
//				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
//					if (!mResponeVO.isNull("items")) {
//						JSONArray objArray = mResponeVO.getJSONArray("items");
//						if (objArray != null && objArray.length() > 0) {
//							JSONObject item;
//							for (int index = 0, count = objArray.length(); index < count; ++index) {
//								item = objArray.getJSONObject(index);
//								ZMSpace image = ZMSpace.parse(item);
//								if (image != null) {
//									imageList.add(image);
//								}
//							}
//						} else {
//							// 没有结果集(最后一页)
//							mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
//							mDataList.setLastPage(true);
//						}
//					} else {
//						// 没有结果集(最后一页)
//						mProtocolStatus = ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY;
//						mDataList.setLastPage(true);
//					}
//				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
//					// 没有结果集(最后一页)
//					mDataList.setLastPage(true);
//				}
//			} catch (Exception ex) {
//				Logger.getInstance(TAG ).debug(ex.getMessage(), ex);
//			}
//			return imageList;
//		}
//		
//		@Override
//		public void afterParse() {
//			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
//				if (mRefreshed) {
//					mDataList.clear();
//				}
//				mDataList.setDataList(mReceiveDataList, null);
//				if (mReceiveDataList.size() < mPageSize) {
//					mDataList.setLastPage(true);
//				}
//			}
//		}
//	}
	public final static class GetSpaceFocusImagesProtocol extends ListProtocolHandlerBase<ZMObjectImage> {
		private final static String TAG = "GetSpaceFocusImagesProtocol";

		public GetSpaceFocusImagesProtocol(Context context, boolean refreshed, RefreshListData<ZMObjectImage> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getFocusImages
		 * @Description: 获取焦点图列表
		 * @param zmObject
		 * @return void
		 */
		public void getFocusImages(ZMObject zmObject) {			
			mSubUrl = String.format("space/%s/%d/image/focus", ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()),
					zmObject.getRemoteId());
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SPACE_FOCUS_IMAGES_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			mReceiveDataList = parseImage();
			return true;
		}

		private ArrayList<ZMObjectImage> parseImage() {
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
				Logger.getInstance(TAG ).debug(ex.getMessage(), ex);
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
			}
		}
	}
	
	public final static class GetSpaceImagePhotoListProtocol extends ListProtocolHandlerBase<ZMObjectImage> {
		private final static String TAG = "GetSpaceImagePhotoListProtocol";

		public GetSpaceImagePhotoListProtocol(Context context, boolean refreshed, RefreshListData<ZMObjectImage> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/**
		 * @Title: getImagePhotoList
		 * @Description: 获取影像照片列表
		 * @param zmObject
		 * @return void
		 */
		public void getImagePhotoList(ZMObject zmObject) {			
			mSubUrl = String.format("space/%s/%d/image/default", ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()),
					zmObject.getRemoteId());
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SPACE_IMAGE_PHOTO_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			mReceiveDataList = parseImage();
			return true;
		}

		private ArrayList<ZMObjectImage> parseImage() {
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
				Logger.getInstance(TAG ).debug(ex.getMessage(), ex);
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
			}
		}
	}
	
	public final static class GetSpaceImageVideoListProtocol extends ListProtocolHandlerBase<ZMSpaceVideo> {
		private final static String TAG = "GetSpaceImageVideoListProtocol";

		public GetSpaceImageVideoListProtocol(Context context, boolean refreshed, RefreshListData<ZMSpaceVideo> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}

		/** 
		 * @Title: getImageVideoList  
		 * @Description: 获取影像视频列表 
		 * @param zmObject 
		 * @return void 
		 */ 
		public void getImageVideoList(ZMObject zmObject) {			
			mSubUrl = String.format("space/%s/%d/content", ZMObjectKind.getZMObjectType(zmObject.getZMObjectType()),
					zmObject.getRemoteId());
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SPACE_IMAGE_VIDEO_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			mReceiveDataList = parseImage();
			return true;
		}

		private ArrayList<ZMSpaceVideo> parseImage() {
			ArrayList<ZMSpaceVideo> imageList = new ArrayList<ZMSpaceVideo>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ZMSpaceVideo video = ZMSpaceVideo.parse(item);
								if (video != null) {
									imageList.add(video);
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
				Logger.getInstance(TAG ).debug(ex.getMessage(), ex);
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
			}
		}
	}
	
	public final static class GetSelfRecommendedZMObjectListProtocol extends ListProtocolHandlerBase<ZMObject> {
		private final static String TAG = "GetSelfRecommendedZMObjectListProtocol";
		private int mNeedObjectType = 0;
		
		public GetSelfRecommendedZMObjectListProtocol(Context context, boolean refreshed,RefreshListData<ZMObject> data, IHttpRequestCallback callBack) {
			super(context, refreshed,data, callBack);
		}
		/**
		* @Title: getSelfRecommendedList
		* @Description: 获取推荐对象列表
		* @param objectType 需要获取的推荐对象类型
		* @param count 需要获取的推荐对象数目
		* @param sourceSpaceId 正在查看的空间ID
		* @return void
		*/
		public void getSelfRecommendedList(int objectType, long sourceSpaceId, int count) {
			mNeedObjectType = objectType;
			String tempUrl = "space/%s/%d/defined_recommended?pageSize=%d&startIndex=%d";	
			mSubUrl = mBaseUrl + String.format(tempUrl, ZMObjectKind.getZMObjectType(objectType), sourceSpaceId, count, mStartIndex);			
			RequestInfo info = new RequestInfo(mSubUrl);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		/**
		* @Title: getSquareRecommendedList
		* @Description: 广场空间推荐
		* @param objectType
		* @param sourceSpaceId
		* @return void
		*/
		public void getSquareRecommendedList(int objectType, long sourceSpaceId) {
			mNeedObjectType = objectType;
			String tempUrl = "space/%s/%d/square_recommended";		
			mSubUrl = mBaseUrl + String.format(tempUrl, ZMObjectKind.getZMObjectType(objectType), sourceSpaceId);			
			RequestInfo info = new RequestInfo(mSubUrl);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SQUARE_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL;
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
//									zmObject.setZMObjectType(objectgType);
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
	* @ClassName: GetNewRecommendedZMObjectListProtocol
	* @Description: 推荐ZMObject列表协议
	* @author liubingsr
	* @date 2012-7-20 下午12:07:44
	*
	*/
	public final static class GetBottomRecommendedListProtocol extends ListProtocolHandlerBase<ZMObject> {
		private final static String TAG = "GetBottomRecommendedListProtocol";
		private int mNeedObjectType = 0;
		
		public GetBottomRecommendedListProtocol(Context context, boolean refreshed,RefreshListData<ZMObject> data, IHttpRequestCallback callBack) {
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
		public void getRecommendedList(int zmObjectType, int count, String sourceSpaceKind, long sourceSpaceId, GeoCoordinate geo, long cityId) {
			mNeedObjectType = zmObjectType;
			if (cityId < 0) {
				cityId = 0;
			}
			//space/{spaceKind}/{spaceId}/{cityId}/similar_recommended 
			String tempUrl = "space/%s/%d/%d/similar_recommended?size=%d";			
			mSubUrl = mBaseUrl + String.format(tempUrl, ZMObjectKind.getZMObjectType(zmObjectType), sourceSpaceId, cityId, count);			
			if (geo != null) {
				mSubUrl += String.format("&latitude=%.14f&longitude=%.14f&latitudeGd=%.14f&longitudeGd=%.14f",
						geo.getLatitude(), geo.getLongitude(), geo.getGdLatitude(), geo.getGdLongitude());
			}			
			RequestInfo info = new RequestInfo(mSubUrl);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_BOTTOM_RECOMMENDED_LIST_PROTOCOL;
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
//								objectgType = mNeedObjectType;
								zmObject = ZMObjectFactory.create(item);
								if (zmObject != null) {
									zmObject.setZMObjectType(objectgType);
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
	public final static class GetOfficialNoticeListProtocol extends ListProtocolHandlerBase<Notice> {
		private final static String TAG = "GetOfficialNoticeListProtocol";
		private long mRegionId;
		
		public GetOfficialNoticeListProtocol(Context context,
				RefreshListData<Notice> data,
				IHttpRequestCallback callBack) {
			super(context, false, data, callBack);
		}
		/**
		* @Title: getOfficialNoticeList
		* @Description: 获取官方公告列表
		* @param regionId 区域id
		* @return void
		*/
		public void getOfficialNoticeList(long regionId, int ObjectType) {
			mRegionId = regionId;
			if (mRegionId <= 0) {
				mRegionId = 1;
			}
			mSubUrl = "notice/official/city/%d/space_kind/%s?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mRegionId, ZMObjectKind.getZMObjectType(ObjectType), mPageSize, mLastId);
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
				mDataList.setDataList(mReceiveDataList, null);
				if (mReceiveDataList.size() < mPageSize) {
					mDataList.setLastPage(true);
				}
			}			
		}		
	}
	/**
	* @ClassName: GetOfficeNoticeProtocol
	* @Description: 公告详情
	* @author liubingsr
	* @date 2012-7-16 下午3:17:47
	*
	*/
	public final static class GetOfficeNoticeProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetOfficeNoticeProtocol";
		private long mNoticeId;
		private Notice mNotice;

		public GetOfficeNoticeProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getNotice
		* @Description: 服务端返回的公告信息
		* @return
		* Notice
		*/
		public Notice getNotice() {
			return mNotice;
		}
		
		public void getOfficeNotice(long noticeId) {
			mNoticeId = noticeId;
			mSubUrl = "notice/official/%d";
			String url = mBaseUrl + String.format(mSubUrl, noticeId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_OFFICIAL_NOTICE_PROTOCOL;
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
							mNotice = Notice.parse(objArray.getJSONObject(0));
							if (mNotice != null) {
								mNotice.setNoticeId(mNoticeId);
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
	* @ClassName: GetVisitedUserListProtocol
	* @Description: 看看谁来过 访客记录
	* @author liubingsr
	* @date 2012-7-16 下午4:22:57
	*
	*/
	public final static class GetVisitedUserListProtocol extends ListProtocolHandlerBase<ZMObjectVisitedEntry> {
		private final static String TAG = "GetVisitedUserListProtocol";
		private ZMObject mZMObject;
		
		public GetVisitedUserListProtocol(Context context, boolean refreshed,
				RefreshListData<ZMObjectVisitedEntry> data,
				IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getVisitedUserList
		* @Description: 获取访问记录列表
		* @param zmObject zmObject
		* @return void
		*/
		public void getVisitedUserList(ZMObject zmObject) {
			mZMObject = zmObject;
			mSubUrl = "visited/%d/%d/visited_user?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mZMObject.getZMObjectType(), mZMObject.getRemoteId(), mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SPACE_VISITED_USER_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseVisitedLog(json);
			return true;
		}

		/**
		* @Title: parseVisitedLog
		* @Description: 解析数据包得到访问记录信息
		* @param json
		* @return ArrayList<ZMObjectVisitedLog>
		*/
		private ArrayList<ZMObjectVisitedEntry> parseVisitedLog(String json) {
			ArrayList<ZMObjectVisitedEntry> visitedLogList = new ArrayList<ZMObjectVisitedEntry>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ZMObjectVisitedEntry visitedLog = ZMObjectVisitedEntry.parse(item);
								if (visitedLog != null) {
									visitedLog.setZMObjectId(mZMObject.getId());
									visitedLogList.add(visitedLog);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return visitedLogList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				mDataList.setDataList(mReceiveDataList, null);
				if (mReceiveDataList.size() < mPageSize) {
					mDataList.setLastPage(true);
				}
			}			
		}		
	}
	/**
	* @ClassName: AddSpaceVisiteMessageProtocol
	* @Description: 看看谁来过 访客发表留言协议
	* @author liubingsr
	* @date 2012-8-31 下午5:21:49
	*
	*/
	public final static class AddSpaceVisiteMessageProtocol extends ProtocolHandlerBase {
		private final static String TAG = "AddSpaceVisiteMessageProtocol";
		private static Gson gson = null;
		static {
			if (gson == null) {
				gson = new Gson();
			}
		}
		public AddSpaceVisiteMessageProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: addMessage
		* @Description: 添加一条访问留言
		* @param zmObject
		* @param message
		* @return void
		*/
		public void addMessage(ZMObject zmObject, String message) {
			mSubUrl = "visited/%d/%d";
			String url = mBaseUrl + String.format(mSubUrl, zmObject.getZMObjectType(), zmObject.getRemoteId());
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			
			VoContent vo = new VoContent();
			vo.setContent(message);			
			String json = gson.toJson(vo);
			
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.ADD_SPACE_VISITE_MESSAGE_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			// 解析服务器返回的json数据包
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							return true;
						}
					}
				}
				Logger.getInstance(TAG).debug("数据包:<" + json + ">");
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug("接收到的数据包:<" + json + ">", ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			// TODO Auto-generated method stub			
		}		
	}
	/**
	* @ClassName: GetVisitorMessageListProtocol
	* @Description: 看看谁来过 访客留言协议
	* @author liubingsr
	* @date 2012-7-16 下午5:28:00
	*
	*/
	public final static class GetVisitorMessageListProtocol extends ListProtocolHandlerBase<VisitorMessage> {
		private final static String TAG = "GetVisitorMessageListProtocol";
		private long mVisiteLogId;
		
		public GetVisitorMessageListProtocol(Context context, boolean refreshed, RefreshListData<VisitorMessage> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getVisitorMessages
		* @Description: 获取访客留言列表
		* @param visiteLogId 访问记录id
		* @return void
		*/
		public void getVisitorMessages(long visiteLogId) {
			mVisiteLogId = visiteLogId;
			mSubUrl = "visited/visited_user/%d/message?pageSize=%d&lastId=%d";
			String url = mBaseUrl + String.format(mSubUrl, mVisiteLogId, mPageSize, mLastId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_SPACE_VISITOR_MESSAGE_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseVisitorMessage(json);
			return true;
		}

		/**
		* @Title: parseVisitorMessage
		* @Description: 解析数据包得到留言
		* @param json
		* @return ArrayList<VisitorMessage>
		*/
		private ArrayList<VisitorMessage> parseVisitorMessage(String json) {
			ArrayList<VisitorMessage> messageList = new ArrayList<VisitorMessage>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								VisitorMessage message = VisitorMessage.parse(item);
								if (message != null) {
									messageList.add(message);
								}
							}
						} else {
							// 没有结果集(最后一页)
							mDataList.setLastPage(true);
						}
					} else {
						// 没有结果集(最后一页)
						mDataList.setLastPage(true);
					}
				} else if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					// 没有结果集(最后一页)
					mDataList.setLastPage(true);
				}
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return messageList;
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
	* @ClassName: GetSpacePraiseCountProtocol
	* @Description: TODO(描述这个类的作用)
	* @author liubingsr
	* @date 2013-1-31 上午12:04:40
	*
	*/
	public final static class GetSpacePraiseCountProtocol extends ProtocolHandlerBase {
		private final static String TAG = "GetSpacePraiseCountProtocol";
		private ZMObject mZMObject;
		private int mCount = 0;

		public GetSpacePraiseCountProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		
		public int getCount() {
			return mCount;
		}
		
		public void getPraiseCount(ZMObject zmObject) {
			mZMObject = zmObject;
			mSubUrl = "like/%d/%d";//"space/%d/image";
			String url = mBaseUrl + String.format(mSubUrl, zmObject.getZMObjectType(), zmObject.getRemoteId());
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
							mCount = objArray.getInt(0);
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
	* @ClassName: SpaceDoPraiseProtocol
	* @Description: 发起赞操作
	* @author liubingsr
	* @date 2012-7-16 上午11:46:26
	*
	*/
	public final static class SpaceDoPraiseProtocol extends ProtocolHandlerBase {
		private final static String TAG = "SpaceDoPraiseProtocol";
		private ZMObject mZMObject;
		private int mPraiseCount = 0;

		public SpaceDoPraiseProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
		}
		/**
		* @Title: getPraiseCount
		* @Description: 服务端返回新的赞信息
		* @return
		* PraiseInfo
		*/
		public int getPraiseCount() {
			return mPraiseCount;
		}
		
		public void doPraise(ZMObject zmObject) {
			mZMObject = zmObject;
			//space/{spaceKind}/{spaceId}/praise/{praiseType}
			mSubUrl = "like/%d/%d";//"space/%d/image";
			String url = mBaseUrl + String.format(mSubUrl, zmObject.getZMObjectType(), zmObject.getRemoteId());
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
							mPraiseCount = objArray.getInt(0);
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
}
