/* 
 * @Title: VoAlbumImage.java
 * Created by liubingsr on 2012-6-28 下午2:12:33 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol.vo;

/**
* @ClassName: VoAlbumImage
* @Description: 用户相册图片
* @author liubingsr
* @date 2012-8-18 下午1:54:30
*
*/
public final class VoAlbumImage {
	/**
	 * 昵称
	 */
//	private String name = "";
	private String description = "";
//	private long userAlbumId = 0;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("description:" + description + ",");
//		sb.append("description:" + description + ",");
//		sb.append("userAlbumId:" + userAlbumId);
		return sb.toString();
	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
//
//	public long getUserAlbumId() {
//		return userAlbumId;
//	}
//
//	public void setUserAlbumId(long userAlbumId) {
//		this.userAlbumId = userAlbumId;
//	}
}
