/* 
 * @Title: RegionProtocolHandler.java
 * Created by liubingsr on 2012-6-1 上午10:15:53 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.Region;
import com.zhima.data.service.RegionService;

/**
* @ClassName: RegionProtocolHandler
* @Description: 空间类型字典协议
* @author liubingsr
* @date 2012-7-12 下午2:27:34
*
*/
public final class RegionProtocolHandler {
	/**
	* @ClassName: SyncRegionDictProtocol
	* @Description: 同步地区字典协议
	* @author liubingsr
	* @date 2012-9-21 下午6:28:19
	*
	*/
	public final static class SyncRegionDictProtocol extends ListProtocolHandlerBase<Region> {
		private final static String TAG = "SyncRegionDictProtocol";
		private long mLastTimestamp;
		
		public SyncRegionDictProtocol(Context context, boolean refreshed, RefreshListData<Region> data, IHttpRequestCallback callBack) {
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
			mSubUrl = "city/tree";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SYNC_REGION_DICT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseRegion();
			return true;
		}

		/**
		* @Title: parseRegion
		* @Description: 解析数据包得到字典信息
		* @param json
		* @return ArrayList<Region>
		*/
		private ArrayList<Region> parseRegion() {
			ArrayList<Region> list = new ArrayList<Region>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								Region region = Region.parse(item);
								if (region != null) {
									list.add(region);
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
			} catch (JSONException ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return list;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				RegionService.getInstance(mContext).addRegion(mReceiveDataList);
			}			
		}		
	}
}
