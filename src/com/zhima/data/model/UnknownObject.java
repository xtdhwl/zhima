/* 
 * @Title: CommerceObject.java
 * Created by liubingsr on 2012-5-17 下午5:33:19 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;

/**
* @ClassName: UnknownObject
* @Description: 非知码对象
* @author liubingsr
* @date 2012-7-24 下午3:48:35
*
*/
public class UnknownObject extends ZMObject {
	private final static String TAG = "UnknownObject";
	
	public UnknownObject() {
		super();
		mZMObjectType = ZMObjectKind.UNKNOWN_OBJECT;
	}
}
