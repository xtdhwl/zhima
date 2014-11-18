/* 
 * @Title: RegionBaseDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zhima.data.model.Region;
import com.zhima.db.provider.ZhimaDatabase.RegionBase;

/**
 * @ClassName: RegionBaseDbController
 * @Description: 地区基类DbController
 * @author liubingsr
 * @date 2012-6-11 下午2:36:57
 * 
 */
public abstract class RegionBaseDbController extends BaseDataController<Region> {

	public RegionBaseDbController(Context c) {
		super(c);
//		mDatabaseTable = new ProvinceTable();
	}

	@Override
	public String[] getResultColumns() {
		String[] columns = { RegionBase.ID, RegionBase.PARENT_ID,
				RegionBase.NAME, RegionBase.PYNAME,
				RegionBase.POSTCODE, RegionBase.TAG_ID};
		return columns;
	}

	@Override
	public Region createData(Cursor c) {
		Region region = new Region();
		int index = 0;
		region.setId(c.getLong(index++));
		region.setParentId(c.getLong(index++));
		region.setName(c.getString(index++));
		region.setPinYinName(c.getString(index++));
		region.setPostCode(c.getInt(index++));
		region.setCityTagId(c.getLong(index++));
		return region;
	}

	@Override
	public ContentValues getContentValues(Region item) {
		ContentValues values = createBaseDataContentValues(item);
		values.put(RegionBase.PARENT_ID, item.getParentId());
		values.put(RegionBase.NAME, item.getName());
		values.put(RegionBase.PYNAME, item.getPinYinName());
		values.put(RegionBase.POSTCODE, item.getPostCode());
		values.put(RegionBase.TAG_ID, item.getCityTagId());
		return values;
	}
}
