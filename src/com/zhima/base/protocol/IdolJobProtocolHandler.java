/* 
 * @Title: IdolJobProtocolHandler.java
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
import com.zhima.data.model.IdolJob;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.IdolJobService;

/**
* @ClassName: IdolJobProtocolHandler
* @Description: 知天使职业字典协议
* @author liubingsr
* @date 2012-7-12 下午2:27:34
*
*/
public final class IdolJobProtocolHandler {
	/**
	* @ClassName: IdolJobDictProtocol
	* @Description: 同步知天使职业字典协议
	* @author liubingsr
	* @date 2012-9-21 下午6:28:19
	*
	*/
	public final static class IdolJobDictProtocol extends ListProtocolHandlerBase<IdolJob> {
		private final static String TAG = "IdolJobDictProtocol";
		private long mLastTimestamp;
		
		public IdolJobDictProtocol(Context context, boolean refreshed, RefreshListData<IdolJob> data, IHttpRequestCallback callBack) {
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
			mSubUrl = "space/idol/job";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.SYNC_IDOLJOB_DICT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {			
			mReceiveDataList = parseJobs();
			return true;
		}

		/**
		* @Title: parseJobs
		* @Description: 解析数据包得到字典信息
		* @param json
		* @return ArrayList<IdolJob>
		*/
		private ArrayList<IdolJob> parseJobs() {
			ArrayList<IdolJob> jobList = new ArrayList<IdolJob>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								IdolJob job = IdolJob.parse(item);
								if (job != null) {
									jobList.add(job);
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
			return jobList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				if (mRefreshed) {
				    mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
				// 存入本地缓存
				IdolJobService.getInstance(mContext).addIdolJob(mReceiveDataList);
			}			
		}		
	}
}
