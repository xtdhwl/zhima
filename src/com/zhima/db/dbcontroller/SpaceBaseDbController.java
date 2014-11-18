/* 
 * @Title: SpaceBaseDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.data.model.Spacekind;
import com.zhima.db.provider.ZhimaDatabase.SpaceBase;

/**
 * @ClassName: SpaceBaseDbController
 * @Description: 空间基类型DbController
 * @author liubingsr
 * @date 2012-6-11 下午2:36:57
 * 
 */
public abstract class SpaceBaseDbController extends BaseDataController<Spacekind> {

	public SpaceBaseDbController(Context c) {
		super(c);
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { SpaceBase.ID, SpaceBase.PARENT_ID,
				SpaceBase.NAME};
		return columns;
	}

	@Override
	public Spacekind createData(Cursor c) {
		Spacekind region = new Spacekind();
		int index = 0;
		region.setId(c.getLong(index++));
		region.setParentId(c.getLong(index++));
		region.setName(c.getString(index++));
		return region;
	}

	@Override
	public ContentValues getContentValues(Spacekind item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(SpaceBase.PARENT_ID, item.getParentId());
		values.put(SpaceBase.NAME, item.getName());
		return values;
	}
}
