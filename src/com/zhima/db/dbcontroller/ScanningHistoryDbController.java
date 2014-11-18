/* 
 * @Title: ScanningHistoryDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.data.model.ScanningHistoryEntry;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.ScanningCodeHistoryTable;

/**
* @ClassName: ScanningHistoryDbController
* @Description: 扫码历史DbController
* @author liubingsr
* @date 2012-8-10 下午3:18:59
*
*/
public class ScanningHistoryDbController extends BaseDataController<ScanningHistoryEntry> {

	public ScanningHistoryDbController(Context c) {
		super(c);
		mDatabaseTable = new ZhimaDatabase.ScanningCodeHistoryTable();
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { ScanningCodeHistoryTable.ID, ScanningCodeHistoryTable.USER_ID,
				ScanningCodeHistoryTable.TITLE, ScanningCodeHistoryTable.CODE,
				ScanningCodeHistoryTable.SCANNING_ON};
		return columns;
	}

	@Override
	public ScanningHistoryEntry createData(Cursor c) {
		ScanningHistoryEntry entry = new ScanningHistoryEntry();
		int index = 0;
		entry.setId(c.getLong(index++));
		entry.setUserId(c.getLong(index++));
		entry.setTitle(c.getString(index++));
		entry.setZMCode(c.getString(index++));
		entry.setScanningTime(c.getLong(index++));
		return entry;
	}

	@Override
	public ContentValues getContentValues(ScanningHistoryEntry item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(ScanningCodeHistoryTable.USER_ID, item.getUserId());
		values.put(ScanningCodeHistoryTable.TITLE, item.getTitle());
		values.put(ScanningCodeHistoryTable.CODE, item.getZMCode());
		values.put(ScanningCodeHistoryTable.SCANNING_ON, item.getScanningTime());
		return values;
	}
}
