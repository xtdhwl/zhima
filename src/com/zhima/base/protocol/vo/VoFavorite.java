/* 
 * @Title: VoFavorite.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

/**
 * @ClassName: VoFavorite
 * @Description: 收藏vo
 * @author liubingsr
 * @date 2012-6-28 下午2:12:33
 * 
 */
public class VoFavorite {
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容。zmcode
	 */
	private String content;
	/**
	 * 类型
	 */
	private int targetType;
	private long targetId;
	private String imageUrl;

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
	
	public int getTargetType() {
		return targetType;
	}
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}
	public long getTargetId() {
		return targetId;
	}
	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("title:" + title + ",");
		sb.append("targetType:" + targetType + ",");
		sb.append("targetId:" + targetId + ",");
		sb.append("imageUrl:" + imageUrl);
		return sb.toString();
	}	
}
