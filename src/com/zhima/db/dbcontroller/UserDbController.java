package com.zhima.db.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.data.model.User;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.UserTable;

public class UserDbController extends BaseDataController<User> {

	public UserDbController(Context c) {
		super(c);
		mDatabaseTable = new ZhimaDatabase.UserTable();
	}

	@Override
	public ContentValues getContentValues(User item) {
		ContentValues values = new ContentValues();
		values.put(UserTable.ID, item.getUserId());
		values.put(UserTable.NICKNAME, item.getNickname());
		values.put(UserTable.IMAGE_URL, item.getImageUrl());
		return values;
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { UserTable.ID, UserTable.NICKNAME,
				UserTable.IMAGE_URL, UserTable.ACCOUNT_ID };
		return columns;
	}

	@Override
	public User createData(Cursor c) {
		User user = null;
		return user;
	}
}
