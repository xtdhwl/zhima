/* 
 * @Title: SpaceKindProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.Spacekind;
import com.zhima.data.service.SpaceKindService;

/**
* @ClassName: SpaceKindProtocolHandler
* @Description: 空间类型字典协议
* @author liubingsr
* @date 2012-7-12 下午2:27:34
*
*/
public final class SpaceKindProtocolHandler {
	/**
	* @ClassName: SyncSpacekindDictProtocol
	* @Description: 同步空间类型字典协议
	* @author liubingsr
	* @date 2012-9-21 下午6:28:19
	*
	*/
	public final static class SyncSpacekindDictProtocol extends ListProtocolHandlerBase<Spacekind> {
		private final static String TAG = "SyncSpacekindDictProtocol";
		private long mLastTimestamp;
		
		public SyncSpacekindDictProtocol(Context context, boolean refreshed, RefreshListData<Spacekind> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		public long getLastTimestamp() {
			return mLastTimestamp;
		}
		/**
		* @Title: sync
		* @Description: 获取字典
		* @param lastTimestamp 时间戳
		* @return void
		*/
		public void sync(long lastTimestamp) {
			mLastTimestamp = lastTimestamp;
			mSubUrl = "space/kind/tree";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SYNC_SPACEKIND_DICT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseSpacekind();
			return true;
		}

		/**
		* @Title: parseSpacekind
		* @Description: 解析数据包得到字典信息
		* @param json
		* @return ArrayList<Spacekind>
		*/
		private ArrayList<Spacekind> parseSpacekind() {
			ArrayList<Spacekind> spacekindList = new ArrayList<Spacekind>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								Spacekind kind = Spacekind.parse(item);
								if (kind != null) {
									spacekindList.add(kind);
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
			return spacekindList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				// 存入本地缓存
				SpaceKindService.getInstance(mContext).addSpacekind(mReceiveDataList);
			}			
		}		
	}
}
