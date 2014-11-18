package com.zhima.base.protocol.vo;

import com.zhima.base.consts.ZMConsts;

public class VoReplyDiaryBody {	
	/**
	 * 回复内容
	 */
	private String content = "";
	/**
	 * 被回复者id
	 */
	private long repliedId = ZMConsts.INVALID_ID;

	public long getRepliedId() {
		return repliedId;
	}
	public void setRepliedId(long repliedId) {
		this.repliedId = repliedId;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
