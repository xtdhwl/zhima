/* 
 * @Title: BaseData.java
 * Created by liubingsr on 2012-6-1 下午3:37:11 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

/**
 * @ClassName: BaseData
 * @Description: 模型基类
 * @author liubingsr
 * @date 2012-6-1 下午3:37:11
 * 
 */
public abstract class BaseData {
	protected long mCursorId = -1L;

	public long getCursorId() {
		if (mCursorId == -1) {
			return getId();
		}
		return mCursorId;
	}

	public abstract long getId();
}
