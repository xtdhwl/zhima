/* 
 * @Title: VoForwardDiary.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

import java.util.List;

/**
 * @ClassName: VoForwardDiary
 * @Description: 转发日记
 * @author liubingsr
 * @date 2012-8-31 下午6:10:00
 * 
 */
public final class VoForwardDiary {
	/**
	 * 原始日志id
	 */
	private long rawBlogId;
	/**
	 * 标题
	 */
	private String title;


	/**
	 * 需要同步到的空间,list中的值为空间分类id#芝麻空间id
	 */
	private List<String> syncList;

	public long getRawBlogId() {
		return rawBlogId;
	}

	public void setRawBlogId(long rawBlogId) {
		this.rawBlogId = rawBlogId;
	}

	public List<String> getSyncList() {
		return syncList;
	}

	public void setSyncList(List<String> syncList) {
		this.syncList = syncList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
