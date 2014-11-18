/* 
 * @Title: VoFavorite.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

/**
 * @ClassName: VoContact
 * @Description: 通讯录vo
 * @author liubingsr
 * @date 2012-6-28 下午2:12:33
 * 
 */
public class VoContact {
	/**
	 * 标题
	 */
	private String title;
	private String telephone;
	private int targetType;
	private long targetId;
	private String imageUrl;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
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
		sb.append("telephone:" + telephone + ",");
		sb.append("targetType:" + targetType + ",");
		sb.append("targetId:" + targetId + ",");
		sb.append("imageUrl:" + imageUrl);
		return sb.toString();
	}	
}
