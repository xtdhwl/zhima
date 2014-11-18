/* 
 * @Title: PersonContactDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.data.model.ContactEntry;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.BaseTable;
import com.zhima.db.provider.ZhimaDatabase.ContactBase;
import com.zhima.db.utils.DatabaseUtil;

/**
* @ClassName: PersonContactDbController
* @Description: 个人通讯录DbController
* @author liubingsr
* @date 2012-8-10 下午4:34:22
*
*/
public class PersonContactDbController extends BaseDataController<ContactEntry> {
	public PersonContactDbController(Context c) {
		super(c);
		mDatabaseTable = new ZhimaDatabase.PersonContactTable();
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { ContactBase.ID, ContactBase.USER_ID,
				ContactBase.OBJECT_ID, ContactBase.TARGET_TYPE,
				ContactBase.TITLE, ContactBase.TELEPHONE,
				ContactBase.IMAGE_URL, ContactBase.TIMESTAMP,
				ContactBase.UPLOAD_FLAG};
		return columns;
	}

	@Override
	public ContactEntry createData(Cursor c) {
		ContactEntry entry = new ContactEntry();
		int index = 0;
		entry.setId(c.getLong(index++));
		entry.setUserId(c.getLong(index++));
		entry.setObjectId(c.getLong(index++));
		entry.setObjectType(c.getInt(index++));
		entry.setTitle(c.getString(index++));
		entry.setTelephone(c.getString(index++));
		entry.setImageUrl(c.getString(index++));
		entry.setTimestamp(c.getLong(index++));
		entry.setUpload(c.getInt(index++) == 1);
		return entry;
	}

	@Override
	public ContentValues getContentValues(ContactEntry item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(ContactBase.USER_ID, item.getUserId());
		values.put(ContactBase.OBJECT_ID, item.getObjectId());
		values.put(ContactBase.TARGET_TYPE, item.getObjectType());
		values.put(ContactBase.TITLE, item.getTitle());
		values.put(ContactBase.TELEPHONE, item.getTelephone());
		values.put(ContactBase.IMAGE_URL, item.getImageUrl());
		values.put(ContactBase.TIMESTAMP, item.getTimestamp());
		values.put(ContactBase.UPLOAD_FLAG, item.isUpload() ? 1 : 0);
		return values;
	}
	/**
	* @Title: reloadAllUnUpload
	* @Description: 加载所有未上传到服务器的数据
	* @return
	* ArrayList<ContactEntry>
	*/
	public ArrayList<ContactEntry> reloadAllUnUpload() {
		ArrayList<ContactEntry> array = new ArrayList<ContactEntry>();

		String where = ContactBase.UPLOAD_FLAG + "=0";
		String[] columns = getResultColumnWithOrder();
		Cursor cursor = DatabaseUtil.query(mContext, getDatabaseTable().getContentUri(),
				columns, where, null, BaseTable.SORT_ORDER);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				ContactEntry obj = createData(cursor);
				if (obj != null) {
					array.add(obj);
				}
			}
			cursor.close();
		}
		return array;
	}
	/**
	* @Title: uploadItem
	* @Description: 更新
	* @param item
	* void
	*/
	public void uploadItem(ContactEntry item) {
		updateData(item);		
	}
}
