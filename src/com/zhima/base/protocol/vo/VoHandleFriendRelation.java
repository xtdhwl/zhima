package com.zhima.base.protocol.vo;

/**
 * @ClassName VoTargetStatus
 * @Description TODO()
 * @author jiang
 * @date 2013-1-23 上午10:53:45
 */
public class VoHandleFriendRelation {
	private long messageId;
	private int targetStatus;
	
	public long getMessageId(){
		return messageId;
	}
	
	public void setMessageId(long messageId){
		this.messageId = messageId;
	}

	public int getTargetStatus() {
		return targetStatus;
	}
	public void setTargetStatus(int targetStatus) {
		this.targetStatus = targetStatus;
	}
}
