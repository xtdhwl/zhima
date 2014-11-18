/* 
* @Title: VoWeddingComment.java
* Created by liubingsr on 2012-8-31 下午5:13:26 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.protocol.vo;

/**
 * @ClassName: VoWeddingComment
 * @Description: 喜印留言
 * @author liubingsr
 * @date 2012-8-31 下午5:13:26
 *
 */
public class VoWeddingComment {
	/**
	 * 评论内容
	 */
	private String content;
	/**
	 * 用户昵称
	 */
	private String createdByName;

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCreatedByName() {
		return createdByName;
	}
	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
}
