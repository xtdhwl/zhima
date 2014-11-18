/* 
 * @Title: Promotion.java
 * Created by liubingsr on 2012-5-24 下午3:28:45 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

/**
 * @ClassName: Promotion
 * @Description: 商户活动
 * @author liubingsr
 * @date 2012-5-24 下午3:28:45
 * 
 */
public abstract class Promotion extends BaseData {
	/**
	 * 活动id
	 */
	protected long mActionId;
	/**
	 * 活动名称
	 */
	protected String mName;
	/**
	 * 活动详细描述
	 */
	protected String mDescription;
	/**
	 * 图片
	 */
	protected String mImageUrl;
	/**
	 * 开始时间
	 */
	protected long mBeginTime;
	/**
	 * 截止时间。如果值是：Long.MAX_VALUE，表示没有结束时间
	 */
	protected long mDeadlineTime;

	@Override
	public long getId() {
		return mActionId;
	}

	public long getActionId() {
		return mActionId;
	}

	public void setActionId(long actionId) {
		this.mActionId = actionId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		this.mDescription = description;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.mImageUrl = imageUrl;
	}

	public long getBeginTime() {
		return mBeginTime;
	}

	public void setBeginTime(long beginTime) {
		this.mBeginTime = beginTime;
	}

	/**
	 * @Title: getDeadlineTime
	 * @Description: 获取截止时间。如果值是：Long.MAX_VALUE，表示没有结束时间
	 * @return long
	 */
	public long getDeadlineTime() {
		return mDeadlineTime;
	}

	public void setDeadlineTime(long deadlineTime) {
		this.mDeadlineTime = deadlineTime;
	}
	
	/**
	 * 是否为无限期
	 * @return
	 */
	public boolean isDateless() {
		return this.mDeadlineTime == Long.MAX_VALUE ? true : false;
	}
}
