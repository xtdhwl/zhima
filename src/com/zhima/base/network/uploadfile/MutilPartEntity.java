/* 
 * @Title: MutilPartEntity.java
 * Created by liubingsr on 2012-5-16 下午8:18:02 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: MutilPartEntity
 * @Description: http multipart/form-data 
 * @author liubingsr
 * @date 2012-5-16 下午8:18:02
 * 
 */
public class MutilPartEntity {
	private String mBoundary = FileUploadConstant.BOUNDARY;

	private List<MutilPartBody> mBodyList;

	public MutilPartEntity() {
		this.mBoundary = FileUploadConstant.BOUNDARY;
	}

	public MutilPartEntity(List<MutilPartBody> bodyList) {
		this.mBoundary = FileUploadConstant.BOUNDARY;
		this.mBodyList = bodyList;
	}

	public MutilPartEntity(String boundary, List<MutilPartBody> bodyList) {
		this.mBoundary = boundary;
		this.mBodyList = bodyList;
	}

	public void addBody(MutilPartBody body) {
		if (mBodyList == null) {
			mBodyList = new ArrayList<MutilPartBody>();
		}
		mBodyList.add(body);
	}

	public List<MutilPartBody> getBodyList() {
		return mBodyList;
	}

	public void setBodyList(List<MutilPartBody> bodyList) {
		this.mBodyList = bodyList;
	}

	public String getBoundary() {
		return mBoundary;
	}

	public void setBoundary(String boundary) {
		this.mBoundary = boundary;
	}
}
