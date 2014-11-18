/* 
 * @Title: ContactService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ContactType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ContactProtocolHandler.GetContactListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.AddContactProtocol;
import com.zhima.base.utils.DateUtils;
import com.zhima.base.utils.UniqueIdGenerator;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ReverseOrderComparator;
import com.zhima.db.dbcontroller.ContactDbController;
import com.zhima.db.dbcontroller.PersonContactDbController;
import com.zhima.ui.common.view.HaloToast;

/**
* @ClassName: ContactService
* @Description: 通讯录管理服务
* @author liubingsr
* @date 2012-8-10 下午5:33:46
*
*/
public class ContactService extends BaseService {
	private final static String TAG = "ContactService";
	private static ContactService mInstance = null;

	private Calendar mCal1;
	private Calendar mCal2;
	private Comparator<ContactEntry> mComp;
	
	private ContactDbController mDbController;
	private RefreshListData<ContactEntry> mSpaceContactList;
	
	private PersonContactDbController mPersonContactController;
	private RefreshListData<ContactEntry> mPersonContactList;

	private ContactService(Context context) {
		super(context);
		mDbController = new ContactDbController(mContext);
		mPersonContactController = new PersonContactDbController(mContext);
		mCal1 = Calendar.getInstance();
		mCal2 = Calendar.getInstance();
		mComp = new ReverseOrderComparator<ContactEntry>();
//		onCreate();
	}

	public static ContactService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ContactService(context);
			mInstance.onCreate();
		}
//		mInstance.mContext = context;
		return mInstance;
	}

	@Override
	public void onCreate() {
		mSpaceContactList = new RefreshListData<ContactEntry>(mDbController.reloadAllDataWithOrder(),mDbController);
		mPersonContactList = new RefreshListData<ContactEntry>(mPersonContactController.reloadAllDataWithOrder(),mPersonContactController);
	}

	@Override
	public void onDestroy() {
		clear();
		mSpaceContactList = null;
		mPersonContactList = null;
		System.gc();
	}
	/**
	* @Title: getContactOfDay
	* @Description: 获取指定日期的项目
	* @param day 日期值
	* @return
	* ArrayList<ContactEntry>
	*/
	public ArrayList<ContactEntry> getContactOfDay(Date day) {
		long destDay = day.getTime();
		return getContactOfDay(destDay);
	}
	/**
	* @Title: getContactOfDay
	* @Description: 获取指定日期的收藏项目
	* @param dayInMillis 日期值(UTC milliseconds)
	* @return
	* ArrayList<ContactEntry>
	*/
	public ArrayList<ContactEntry> getContactOfDay(long dayInMillis) {
		ArrayList<ContactEntry> ret = new ArrayList<ContactEntry>();
		ArrayList<ContactEntry> list = getContactList();
		mCal1.setTimeInMillis(dayInMillis);
 		for (ContactEntry entry : list) {			
			mCal2.setTimeInMillis(entry.getTimestamp());
			if (DateUtils.isSameDay(mCal1, mCal2)) {
				ret.add(entry);
			}
		}
		return ret;
	}
	/**
	* @Title: searchContactList
	* @Description: 搜索出空间通讯录标题里含有关键字的条目列表
	* @param keyword 关键字
	* @return
	* ArrayList<ContactEntry>
	*/
	public ArrayList<ContactEntry> searchContactList(String keyword) {		
		ArrayList<ContactEntry> list = mSpaceContactList.getDataList();
		if (TextUtils.isEmpty(keyword)) {
			return list;
		}
		ArrayList<ContactEntry> ret = new ArrayList<ContactEntry>();
		String lowerCaseWord = keyword.toLowerCase();
		for (ContactEntry entry : list) {			
			if (entry.getTitle().toLowerCase().indexOf(lowerCaseWord) >= 0) {
				ret.add(entry);
			}
		}
		return ret;
	}
	/**
	* @Title: getContactList
	* @Description: 得到空间通讯录项目(按照加入通讯录时间倒序排列)
	* @return
	* ArrayList<ContactEntry>
	*/
	public ArrayList<ContactEntry> getContactList() {
		ArrayList<ContactEntry> list = mSpaceContactList.getDataList();		
        Collections.sort(list, mComp);
		return list;
	}
	/**
	* @Title: getSpaceContactRefreshData
	* @Description: 得到空间通讯录项目
	* @return
	* RefreshListData<ContactEntry>
	*/
	public RefreshListData<ContactEntry> getSpaceContactRefreshData() {
		return mSpaceContactList;
	}
	/**
	* @Title: clearRefreshListData
	* @Description: 清空数据
	* @return void
	*/
	public void clearSpaceContactListData() {
		mSpaceContactList = null;
		mSpaceContactList = new RefreshListData<ContactEntry>();
	}
	private boolean isExists(String name, boolean isPersonContact) {
		ArrayList<ContactEntry> list;
		if (isPersonContact) {
			list = mPersonContactList.getDataList();
		} else {
			list = mSpaceContactList.getDataList();
		}
		for (ContactEntry obj : list) {
			if (obj.getTitle().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	/**
	* @Title: addContact
	* @Description: 添加一条空间通讯录项目
	* @param title 标题
	* @param entry 条目
	* @return void
	*/
	private void addContact(ContactEntry entry, boolean isPersonContact) {
		if (entry == null) {
			return;
		}
		long userId = AccountService.getInstance(mContext).getUserId();
		entry.setId(UniqueIdGenerator.getInstance().genericId());
		entry.setUserId(userId);
//		mDbController.updateData(entry);
		if (isPersonContact) {
			mPersonContactList.add(entry);
		} else {
			mSpaceContactList.add(entry);
		}
	}
	/**
	 * @Title: addContact
	 * @Description: 保存到空间通讯录(通讯录条目(通讯录目标类型是：除去3000件商品、非3000件商品之外其它类型的空间))
	 * @param entry 通讯录对象
	 * @param isPersonContact 是否是个人通讯录
	 * @param callBack 结果通知回调
	 * @return void
	 */
	public void addContact(ContactEntry entry, boolean isPersonContact, IHttpRequestCallback callBack) {
		if (isExists(entry.getTitle(), isPersonContact)) {
			HaloToast.show(mContext, R.string.favorite_repeat_failed, 0);
			return;
		}
		addContact(entry, isPersonContact);
		AddContactProtocol protocol = new AddContactProtocol(mContext, callBack);
		protocol.addContact(entry);
	}
	/**
	* @Title: deleteCacheContact
	* @Description: 删除一条空间通讯录
	* @param entryId 要删除的记录id
	* @return boolean
	*/
	public void deleteCacheContact(long entryId, boolean isPersonContact) {
		if (isPersonContact) {
			mPersonContactList.delete(entryId);
		} else {
			mSpaceContactList.delete(entryId);
		}
	}
	public void deleteCacheContact(ArrayList<Long> ids, boolean isPersonContact) {
		if (isPersonContact) {
			for (Long id : ids) {
				mPersonContactList.delete(id);
			}
		} else {
			for (Long id : ids) {
				mSpaceContactList.delete(id);
			}
		}		
	}
	/**
	* @Title: deleteContactById
	* @Description:  删除一条空间通讯录项目
	* @param entryId 要删除的记录id
	* @param callBack
	* @return void
	*/
	public void deleteContactById(long entryId, boolean isPersonContact, IHttpRequestCallback callBack) {
		deleteCacheContact(entryId, isPersonContact);
	}	
	/**
	* @Title: deleteAll
	* @Description: 删除所有空间通讯录
	* @return void
	*/
	public void deleteAll(boolean isPersonContact) {
		if (isPersonContact) {
			mPersonContactList.clear();
			mPersonContactController.deleteAll();
		} else {
			mSpaceContactList.clear();
			mDbController.deleteAll();
		}		
	}
	/**
	* @Title: deleteAllContact
	* @Description: 删除所有通讯录(logout成功后调用此方法清空用户数据)
	* @return void
	*/
	public void deleteAllContact() {
		deleteAll(true);
		deleteAll(false);
	}
	/**
	* @Title: getPersonContactList
	* @Description: 得到个人通讯录项目(按照加入通讯录时间倒序排列)
	* @return
	* ArrayList<ContactEntry>
	*/
	public ArrayList<ContactEntry> getPersonContactList() {
		ArrayList<ContactEntry> list = mPersonContactList.getDataList();		
        Collections.sort(list, mComp);
		return list;
	}
	/**
	* @Title: getPersonContactRefreshData
	* @Description: 得到个人通讯录项目
	* @return
	* RefreshListData<ContactEntry>
	*/
	public RefreshListData<ContactEntry> getPersonContactRefreshData() {
		return mPersonContactList;
	}
	/**
	* @Title: getContactList
	* @Description: 从服务器获取通讯录
	* @param contactType 通讯录类型
	* @param callBack
	* @return void
	*/
	public void getContactList(int contactType, IHttpRequestCallback callBack) {
		RefreshListData<ContactEntry> list = null;
		switch(contactType) {
		case ContactType.PERSONAL:
			list = mPersonContactList;
			break;
		case ContactType.SPACE:
			list = mSpaceContactList;
			break;
		default:
			list = mSpaceContactList;
		}
		GetContactListProtocol protocol = new GetContactListProtocol(mContext, true, list, callBack);
		protocol.getContactList(contactType);
	}
	
	@Override
	public void clear() {
		if (mSpaceContactList != null) {
			mSpaceContactList.clear();
		}
		if (mPersonContactList != null) {
			mPersonContactList.clear();
		}
	}
}
