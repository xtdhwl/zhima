/* 
 * @Title: ZMObjectDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.base.logger.Logger;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectFactory;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.db.provider.ZhimaDatabase.ZMObjectTable;

/**
 * @ClassName: ZMObjectDbController
 * @Description: ZMObject对象
 * @author liubingsr
 * @date 2012-7-11 下午6:15:11
 * 
 */
public class ZMObjectDbController extends BaseDataController<ZMObject> {
	private final static String TAG = "ZMObjectDbController";
	
	public ZMObjectDbController(Context c) {
		super(c);
		mDatabaseTable = new ZhimaDatabase.ZMObjectTable();
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { ZMObjectTable.ID, ZMObjectTable.ZMOBJECT_ID,
				ZMObjectTable.OBJECT_TYPE, ZMObjectTable.CODE,
				ZMObjectTable.NAME, ZMObjectTable.TITLE,
				ZMObjectTable.DESCRIPTION, ZMObjectTable.IMAGE_URL,
				ZMObjectTable.LATITUDE, ZMObjectTable.LONGITUDE,
				ZMObjectTable.JSON, ZMObjectTable.JSON_COMPRESSED,
				ZMObjectTable.TIMESTAMP };

		return columns;
	}

	@Override
	public ZMObject createData(Cursor c) {
		ZMObject zmObject = null;
		int typeIndex = 2, jsonIndex = 10;
		int compressedIndex = jsonIndex + 1;
		int objectType = c.getInt(typeIndex);
		boolean compressed = c.getInt(compressedIndex) == 1;
		String zipStr = c.getString(jsonIndex);
		String json = zipStr;
//		if (compressed) {			
//			try {
//				json = zipCompressHelper.unCompressFromStr(zipStr);
//			} catch (IOException e) {
//				Logger.getInstance(TAG).debug(e.getMessage(), e);
//				json = zipStr;
//			}
//		}
		JSONTokener jsonParser = new JSONTokener(json);
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.nextValue();
			zmObject = ZMObjectFactory.create(jsonObject, objectType);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return zmObject;
		}		
//		zmObject.setId(c.getLong(index++));
//		zmObject.setZMObjectId(c.getLong(index++));
//		zmObject.setZMObjectType(c.getInt(index++));
//		zmObject.setZMCode(c.getString(index++));
//		zmObject.setName(c.getString(index++));
//		zmObject.setTitle(c.getString(index++));
//		zmObject.setDescription(c.getString(index++));
//		zmObject.setImageUrl(c.getString(index++));
//		zmObject.setLatitude(c.getDouble(index++));
//		zmObject.setLongitude(c.getDouble(index++));
//		zmObject.setJson(c.getString(index++));
//		zmObject.setTimeStamp(c.getLong(index++));
		return zmObject;
	}

	@Override
	public ContentValues getContentValues(ZMObject item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(ZMObjectTable.ZMOBJECT_ID, item.getRemoteId());
		values.put(ZMObjectTable.OBJECT_TYPE, item.getZMObjectType());
		values.put(ZMObjectTable.CODE, item.getZMCode());
		values.put(ZMObjectTable.NAME, item.getName());
		values.put(ZMObjectTable.TITLE, item.getTitle());
		values.put(ZMObjectTable.DESCRIPTION, item.getDescription());
		values.put(ZMObjectTable.IMAGE_URL, item.getImageUrl());
//		values.put(ZMObjectTable.LATITUDE, item.getGeo());
//		values.put(ZMObjectTable.LONGITUDE, item.getLongitude());
		values.put(ZMObjectTable.JSON, item.getJson());
		values.put(ZMObjectTable.JSON_COMPRESSED, item.getJsonCompressed() ? 1 : 0);
		values.put(ZMObjectTable.TIMESTAMP, item.getTimeStamp());
		return values;
	}
}
