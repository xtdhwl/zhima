/* 
 * @Title: VoDiary.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

import java.util.List;

/**
 * @ClassName: VoDiary
 * @Description: 日记
 * @author liubingsr
 * @date 2012-6-28 下午2:12:33
 * 
 */
public class VoDiary {
	/**
	 * 标题
	 */
	protected String title;
	/**
	 * 内容
	 */
	protected String content;
	/**
	 * @see com.zhima.base.consts.ZMConsts.DiaryPrivacyStatus
	 */
	protected int privateStatus;

	/**
	 * 同步到的空间列表
	 */
	protected List<String> syncList = null;

	
	

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPrivateStatus() {
		return privateStatus;
	}

	public void setPrivateStatus(int privateStatus) {
		this.privateStatus = privateStatus;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("title:" + title + ",");
		sb.append("content:" + content + ",");
		sb.append("privateStatus:" + privateStatus);
		return sb.toString();
	}
}
