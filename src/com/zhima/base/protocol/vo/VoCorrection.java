/* 
* @Title: VoCorrection.java
* Created by liubingsr on 2012-8-31 下午5:13:26 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.protocol.vo;

/**
 * @ClassName: VoCorrection
 * @Description: TODO(描述这个类的作用)
 * @author liubingsr
 * @date 2012-8-31 下午5:13:26
 *
 */
public class VoCorrection {
	private String content;
	/**
	 * 目标Id
	 */
	private long targetId;
	/**
	 * 目标类型
	 */
	private int targetType;
	/**
	 * 目标名称
	 */
	private String targetName;

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTargetId() {
		return targetId;
	}
	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	public int getTargetType() {
		return targetType;
	}
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
}
