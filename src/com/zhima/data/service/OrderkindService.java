/* 
 * @Title: OrderkindService.java
 * Created by liubingsr on 2012-6-3 上午10:54:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.service;

import java.util.ArrayList;

import android.content.Context;

import com.zhima.base.consts.ZMConsts.OrderBy;
import com.zhima.data.model.Orderkind;

/**
 * @ClassName: OrderkindService
 * @Description: 排序类型
 * @author liubingsr
 * @date 2012-8-8 下午5:15:07
 * 
 */
public class OrderkindService extends BaseService {
	private final static String TAG = "SpaceKindService";
	private static OrderkindService mInstance = null;


	private ArrayList<Orderkind> mOrderkindList = new ArrayList<Orderkind>();

	private OrderkindService(Context context) {
		super(context);
		loadData();
		// onCreate();
	}

	public static OrderkindService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new OrderkindService(context);
		}
//		mInstance.mContext = context;
		return mInstance;
	}
	
	private void loadData() {
		Orderkind kind = new Orderkind();
		kind.setTitle("活跃");
		kind.setValue(OrderBy.NEW);
		mOrderkindList.add(kind);
		
//		kind = new Orderkind();
//		kind.setTitle("人气");
//		kind.setValue(OrderBy.HOT);
//		mOrderkindList.add(kind);
		
		kind = new Orderkind();
		kind.setTitle("周边");
		kind.setValue(OrderBy.NEAR);
		mOrderkindList.add(kind);
	}
	@Override
	public void onCreate() {
	}
	/**
	* @Title: getOrderkindList
	* @Description: 排序类型列表
	* @return
	* ArrayList<Orderkind>
	*/
	public ArrayList<Orderkind> getOrderkindList() {
		return mOrderkindList;
	}
	@Override
	public void onDestroy() {
		clear();
		System.gc();
	}

	@Override
	public void clear() {
		if (mOrderkindList != null) {
			mOrderkindList.clear();
		}
	}
}
