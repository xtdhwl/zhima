/* 
 * @Title: ContactProtocolHandler.java
 * Created by liubingsr on 2012-5-21 下午3:35:44 
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
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.ContactService;

public final class ContactProtocolHandler {
	
	public final static class DeleteContactProtocol extends ProtocolHandlerBase {
		private final static String TAG = "DeleteContactProtocol";
		private ArrayList<Long> mDeleteIds = null;
		private boolean mIsPersonContact = false;
		
		public DeleteContactProtocol(Context context, IHttpRequestCallback callBack) {
			super(context, callBack);
			mDeleteIds = new ArrayList<Long>();
		}
		/**
		* @Title: deleteContact
		* @Description: 删除一条记录
		* @param entryId 条目id
		* @return void
		*/
		public void deleteContact(long entryId, boolean isPersonContact) {
			mIsPersonContact = isPersonContact;
			mDeleteIds.clear();
			mDeleteIds.add(entryId);
			mSubUrl = "space_contact/%d";
			String url = mBaseUrl + String.format(mSubUrl, entryId);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.DELETE);
//			String json = "{\"userId\":" + entryId + "\"}";
//			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_CONTACT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		public void deleteContact(ArrayList<Long> idList, boolean isPersonContact) {
			mIsPersonContact = isPersonContact;
			mDeleteIds.clear();
			mDeleteIds = new ArrayList<Long>(idList);
			mSubUrl = "space_contact/batch_delete";
			String url = mBaseUrl + mSubUrl;
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			String json = IdListToJson(idList); //"{\"userId\":" + idList.toString() + "\"}";
			info.setBody(json);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.DELETE_CONTACT_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		@Override
		public boolean parse() {			
			try {				
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						return true;
//						JSONArray objArray = mResponeVO.getJSONArray("items");
//						if (objArray != null && objArray.length() > 0) {
//							return true;
//						}
					}					
				}
			} catch (Exception ex) {
				Logger.getInstance(TAG).debug(ex.getMessage(), ex);
			}
			return false;
		}

		@Override
		public void afterParse() {
			ContactService.getInstance(mContext).deleteCacheContact(mDeleteIds, mIsPersonContact);
		}		
	}
	public final static class GetContactListProtocol extends ListProtocolHandlerBase<ContactEntry> {
		private final static String TAG = "GetContactListProtocol";
		
		public GetContactListProtocol(Context context, boolean refreshed, RefreshListData<ContactEntry> data, IHttpRequestCallback callBack) {
			super(context, refreshed, data, callBack);
		}
		/**
		* @Title: getContactList
		* @Description: 获取列表
		* @param contactType 通讯录类型
		* @return void
		*/
		public void getContactList(int contactType) {
			mSubUrl = "space_contact/?contactType=%d";
			String url = mBaseUrl + String.format(mSubUrl, contactType);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.GET);
			this.setRequestInfo(info);
			mProtocolType = ProtocolType.GET_CONTACT_LIST_PROTOCOL;
			mRequestService.sendRequest(this);
		}
		
		@Override
		public boolean parse() {
			String json = mRequestInfo.getRecieveData();
			mReceiveDataList = parseResult(json);
			return true;
		}

		/**
		* @Title: parseResult
		* @Description: 解析数据包
		* @param json
		* @return
		*/
		private ArrayList<ContactEntry> parseResult(String json) {
			ArrayList<ContactEntry> contactList = new ArrayList<ContactEntry>();
			try {
				if (mProtocolStatus == ZMConsts.ProtocolStatus.RESULT_SUCCESS) {
					if (!mResponeVO.isNull("items")) {
						JSONArray objArray = mResponeVO.getJSONArray("items");
						if (objArray != null && objArray.length() > 0) {
							JSONObject item;
							for (int index = 0, count = objArray.length(); index < count; ++index) {
								item = objArray.getJSONObject(index);
								ContactEntry entry = ContactEntry.parse(item);
								if (entry != null) {
									contactList.add(entry);
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
			return contactList;
		}

		@Override
		public void afterParse() {
			if (mReceiveDataList != null && !mReceiveDataList.isEmpty()) {
				// 存入本地sqlite
				if (mRefreshed) {
					mDataList.clear();
				}
				mDataList.setDataList(mReceiveDataList, null);
			}			
		}		
	}
}