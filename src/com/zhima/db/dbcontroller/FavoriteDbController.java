/* 
 * @Title: FavoriteDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.zhima.data.model.FavoriteEntry;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.BaseTable;
import com.zhima.db.provider.ZhimaDatabase.FavoriteTable;
import com.zhima.db.utils.DatabaseUtil;

/**
* @ClassName: FavoriteDbController
* @Description: 收藏夹DbController
* @author liubingsr
* @date 2012-8-10 下午4:15:49
*
*/
public class FavoriteDbController extends BaseDataController<FavoriteEntry> {
	private final static long DAY = 24 * 60 * 60 * 1000;
	public FavoriteDbController(Context c) {
		super(c);
		mDatabaseTable = new ZhimaDatabase.FavoriteTable();
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { FavoriteTable.ID, FavoriteTable.USER_ID,
				FavoriteTable.OBJECT_ID, FavoriteTable.TARGET_TYPE,
				FavoriteTable.TITLE, FavoriteTable.CONTENT,
				FavoriteTable.IMAGE_URL, FavoriteTable.SAVE_ON};
		return columns;
	}

	@Override
	public FavoriteEntry createData(Cursor c) {
		FavoriteEntry entry = new FavoriteEntry();
		int index = 0;
		entry.setId(c.getLong(index++));
		entry.setUserId(c.getLong(index++));
		entry.setObjectId(c.getLong(index++));
		entry.setObjectType(c.getInt(index++));
		entry.setTitle(c.getString(index++));
		entry.setImageUrl(c.getString(index++));
		entry.setFavoriteTime(c.getLong(index++));
		return entry;
	}

	@Override
	public ContentValues getContentValues(FavoriteEntry item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(FavoriteTable.USER_ID, item.getUserId());
		values.put(FavoriteTable.OBJECT_ID, item.getObjectId());
		values.put(FavoriteTable.TARGET_TYPE, item.getObjectType());
		values.put(FavoriteTable.TITLE, item.getTitle());
		values.put(FavoriteTable.IMAGE_URL, item.getImageUrl());
		values.put(FavoriteTable.SAVE_ON, item.getFavoriteTime());
		return values;
	}
	/**
	* @Title: isExists
	* @Description: 是否存在
	* @param type
	* @param code
	* @return
	* boolean
	*/
	public boolean isExists(int type, String code) {
		boolean result = false;
		long now = System.currentTimeMillis();
		String where = "(? - " + FavoriteTable.SAVE_ON + ") < ? AND " + FavoriteTable.TARGET_TYPE + "=? AND " + FavoriteTable.TITLE + "=?";
		String[] columns = { BaseTable.ID };
		Cursor cursor = DatabaseUtil.query(mContext, getDatabaseTable().getContentUri(), columns, where, new String[]{String.valueOf(now), String.valueOf(DAY), String.valueOf(type), code}, null);
		if (cursor != null) {
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close(); 
		}
		return result;
	}
	/**
	* @Title: deleteOldFavorite
	* @Description: 删除过期的数据
	* void
	 */
	public void deleteOldFavorite(long now) {
		new DeleteOldTask().execute(now);
	}
	private final class DeleteOldTask extends AsyncTask<Long, Void, Void> {
		@Override
		protected Void doInBackground(Long... params) {
			long now = params[0];
			String where = "(? - " + FavoriteTable.SAVE_ON + ") > ?";
			String[] columns = getResultColumnWithOrder();
			DatabaseUtil.delete(mContext, getDatabaseTable().getContentUri(), where, new String[]{String.valueOf(now), String.valueOf(DAY)});
			return null;
		}
	}
}
