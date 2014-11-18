/* 
 * @Title: ProvinceDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.Context;

import com.zhima.db.provider.ZhimaDatabase.ProvinceTable;

/**
 * @ClassName: ProvinceDbController
 * @Description: 省(直辖市)DbController
 * @author liubingsr
 * @date 2012-6-11 下午2:36:57
 * 
 */
public class ProvinceDbController extends RegionBaseDbController {
	public ProvinceDbController(Context c) {
		super(c);
		mDatabaseTable = new ProvinceTable();
	}
}
