/* 
 * @Title: IdolJobDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.data.model.IdolJob;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.IdolJobTable;

/**
* @ClassName: IdolJobDbController
* @Description: 知天使职业DbController
* @author liubingsr
* @date 2012-8-10 下午4:34:22
*
*/
public class IdolJobDbController extends BaseDataController<IdolJob> {

	public IdolJobDbController(Context c) {
		super(c);
		mDatabaseTable = new ZhimaDatabase.IdolJobTable();
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { IdolJobTable.ID, IdolJobTable.JOB};
		return columns;
	}

	@Override
	public IdolJob createData(Cursor c) {
		IdolJob entry = new IdolJob();
		int index = 0;
		entry.setId(c.getLong(index++));
		entry.setJob(c.getString(index++));
		return entry;
	}

	@Override
	public ContentValues getContentValues(IdolJob item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(IdolJobTable.JOB, item.getJob());
		return values;
	}
	/**
	* @Title: uploadItem
	* @Description: 更新
	* @param item
	* void
	*/
	public void uploadItem(IdolJob item) {
		updateData(item);		
	}
}
