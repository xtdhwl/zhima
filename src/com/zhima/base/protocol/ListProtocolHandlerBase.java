/* 
 * @Title: ListProtocolHandlerBase.java
 * Created by liubingsr on 2012-6-11 下午5:36:32 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import android.content.Context;

import com.zhima.base.config.SystemConfig;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.data.model.BaseData;
import com.zhima.data.model.RefreshListData;

/**
 * @ClassName: ListProtocolHandlerBase
 * @Description: 列表数据协议基类
 * @author liubingsr
 * @date 2012-6-11 下午5:36:32
 * 
 */
public abstract class ListProtocolHandlerBase<T extends BaseData> extends ProtocolHandlerBase {
	/**
	 * 传入的参数。
	 */
	protected RefreshListData<T> mDataList;
	/**
	 * 上次请求最后一条数据的id。(控制分页需要)
	 */
	protected long mLastId = -1L;
	/**
	 * 当前页第一项的index值，算式为：(页数-1)*分页大小。默认值为0
	 */
	protected int mStartIndex = 0;
	/**
	 * 是否刷新。如果是刷新，那么需要清空mDataList中原来的数据
	 */
	protected boolean mRefreshed = false;
	/**
	 * 暂存解析出的数据
	 */
	protected ArrayList<T> mReceiveDataList = new ArrayList<T>();
	/**
	 * 页大小
	 */
	protected int mPageSize = SystemConfig.PAGE_SIZE;

	public ListProtocolHandlerBase(Context context, boolean refreshed, RefreshListData<T> data, IHttpRequestCallback callBack) {
		super(context, callBack);
		mRefreshed = refreshed;
		mDataList = data;
		mDataList.setLastPage(false);
		mPageSize = mDataList.getPageSize();
		if (!refreshed) {
			mLastId = mDataList.getCursor();
			mStartIndex = (data.getPageNum() - 1) * mPageSize;
			if (mStartIndex < 0) {
				mStartIndex = 0;
			}
		}
	}

	public RefreshListData<T> getDataList() {
		return mDataList;
	}
}
