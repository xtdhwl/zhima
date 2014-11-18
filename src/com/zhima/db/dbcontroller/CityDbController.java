/* 
 * @Title: CityDbController.java
 * Created by liubingsr on 2012-6-3 上午11:32:51 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.db.dbcontroller;

import android.content.Context;

import com.zhima.db.provider.ZhimaDatabase.CityTable;

/**
 * @ClassName: CityDbController
 * @Description: 城市(区)DbController
 * @author liubingsr
 * @date 2012-6-11 下午2:36:57
 * 
 */
public class CityDbController extends RegionBaseDbController {
	public CityDbController(Context c) {
		super(c);
		mDatabaseTable = new CityTable();
	}
}
