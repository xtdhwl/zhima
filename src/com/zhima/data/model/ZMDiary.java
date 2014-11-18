/* 
 * @Title: ZMDiary.java
 * Created by liubingsr on 2012-5-18 下午5:24:30 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts;
import com.zhima.base.logger.Logger;

/**
* @ClassName: ZMDiary
* @Description: 日志
* @author liubingsr
* @date 2013-1-16 下午3:38:09
*
*/
public class ZMDiary extends ZMContent {
	private final static String TAG = "ZMDiary";
	/**
	 * 同步id，此属性只有在查看芝麻空间日志时有值
	 * 同步产生的日志是对原始日志的一个新引用
	 */
	private long mSyncId = ZMConsts.INVALID_ID;
	/**
	 * 内容的原始作者
	 */
	private User mRawAuthor = null;
	/**
	 * 标题
	 */
	private String mTitle = "";
	/**
	 * 日志html内容
	 */
	private String mHtml = "";
	/**
	 * 是否原创
	 */
	private boolean mOriginal = true;
	/**
	 * 私密状态(私密的日记好友不可见)
	 * @see com.zhima.base.consts.ZMConsts.DiaryPrivacyStatus
	 */
	private int mPrivacyStatus;
	/**
	 * 该日志是否是从芝麻空间转的，true是，false不是，如果为true则forwardedFromId为syncId，为false则日志id
	 */
	private boolean mIsSync = false;
	/**
	 * 原始日志id
	 */
	private long mOrginalDiaryId = 0;
	/**
	 * 被转发日志id
	 */
	private long mForwardFromId = 0;
	/**
	 * 原始内容是否存在
	 * @see com.zhima.base.consts.ZMConsts.SendStatus
	 * 注意：当原始内容删除时，要显示“原始内容已删除”字样
	 * 
	 * 精彩，转走了
	 * 别问我是谁：从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起
	 * 从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起
	 * 从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起从今天起
	 * 从今天起从今天起从今。。
	 * 
	 * 精彩，转走了
	 * 别问我是谁：原始内容已删除
	 */
	private boolean mOrginalExists = true;	
	/**
	 * 同步到的空间列表
	 */
	private List<ZMSpace> mSpaceList = null;
	/**
	 * 原始日志创建时间
	 */
	private long mRawCreatedOn = 0;
	
	public long getDiaryId() {
		return this.mId;
	}
	public void setDiaryId(long diaryId) {
		this.mId = diaryId;
	}
	/**
	* @Title: isSpaceDiaryId
	* @Description: 是否是芝麻空间的日志
	* @return true:芝麻空间日志
	*/
	public boolean isSpaceDiaryId() {
		return mSyncId > 0;
	}
	
	public User getRawAuthor() {
		return mRawAuthor;
	}
	public void setRawAuthor(User mRawAuthor) {
		this.mRawAuthor = mRawAuthor;
	}
	
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getHtml() {
		return mHtml;
	}
	public void setHtml(String html) {
		this.mHtml = html;
	}
	
	public boolean isOriginal() {
		return mOriginal;
	}
	public void setOriginal(boolean original) {
		this.mOriginal = original;
	}
	
	public boolean isPublic() {
		return mPrivacyStatus == ZMConsts.DiaryPrivacyStatus.PUBLIC;
	}
	public int getPrivacyStatus() {
		return mPrivacyStatus;
	}
	public void setPrivacyStatus(int privacyStatus) {
		this.mPrivacyStatus = privacyStatus;
	}
	
	public boolean isOrginalExists() {
		return mOrginalExists;
	}
	public void setOrginalExists(boolean exists) {
		this.mOrginalExists = exists;
	}	
	
	public long getOrginalDiaryId() {
		return this.mOrginalDiaryId;
	}
	public void setOrginalDiaryId(long diaryId) {
		this.mOrginalDiaryId = diaryId;
	}
	
	public long getSyncId() {
		return mSyncId;
	}
	public void setSyncId(long syncId) {
		this.mSyncId = syncId;
	}
	public boolean isSync() {
		return mIsSync;
	}
	public void setSync(boolean isSync) {
		this.mIsSync = isSync;
	}
	public long getForwardFromId() {
		return mForwardFromId;
	}
	public void setForwardFromId(long forwardFromId) {
		this.mForwardFromId = forwardFromId;
	}
	
	public List<ZMSpace> getSpaceList() {
		return mSpaceList;
	}
	public void setSpaceList(List<ZMSpace> spaceList) {
		this.mSpaceList = spaceList;
	}
	
	public long getRawCreatedOn() {
		return this.mRawCreatedOn;
	}
	public void setRawCreatedOn(long time) {
		this.mRawCreatedOn = time;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("diaryId:" + mId);
		sb.append(",title:" + mTitle);
		sb.append(",content:" + mContent);		
		sb.append(",imageUrl:" + mImageUrl);
		sb.append(",status:" + mStatus);
		sb.append(",privacyStatus:" + mPrivacyStatus);
		sb.append(",replyCount:" + mReplyCount);
		sb.append(",viewCount:" + mViewCount);
		sb.append(",forwardCount:" + this.mForwardCount);
		sb.append(",postTime:" + mPostTime);
		return sb.toString();
	}
	@Override
	public void create(JSONObject jsonObject) throws JSONException {
		super.create(jsonObject);
		if (!jsonObject.isNull("title")) {
			mTitle = jsonObject.getString("title");
		}
		if (!jsonObject.isNull("html")) {
			mHtml = jsonObject.getString("html");
		}
		if (!jsonObject.isNull("syncId")) {
			mSyncId = jsonObject.getLong("syncId");
		}
		if (!jsonObject.isNull("isSync")) {
			mIsSync = jsonObject.getBoolean("isSync");
		}
		if (!jsonObject.isNull("original")) {
			mOriginal = jsonObject.getBoolean("original");
		}		
		mPrivacyStatus = jsonObject.getInt("privateStatus");
		if (!jsonObject.isNull("rawBlogId")) {
			mOrginalDiaryId = jsonObject.getLong("rawBlogId");
		}
		if (!jsonObject.isNull("forwardedFromId")) {
			mForwardFromId = jsonObject.getLong("forwardedFromId");
		}
		if (!jsonObject.isNull("rawBlogExist")) {
			mOrginalExists = jsonObject.getBoolean("rawBlogExist");
		}		
		if (!jsonObject.isNull("rawCreatedOn")) {
			mRawCreatedOn = jsonObject.getLong("rawCreatedOn");
		}
		if (!jsonObject.isNull("rawUserInfo")) {
			mRawAuthor = User.parse(jsonObject.getJSONObject("rawUserInfo"));
		}
		if (!jsonObject.isNull("spaces")) {
			JSONArray objArray = jsonObject.getJSONArray("spaces");
			if (objArray != null && objArray.length() > 0) {
				mSpaceList = new ArrayList<ZMSpace>();
				JSONObject item;
				for (int index = 0, count = objArray.length(); index < count; ++index) {
					item = objArray.getJSONObject(index);
					ZMSpace space = ZMSpace.parse(item);
					if (space != null) {
						mSpaceList.add(space);
					}
				}
			}
		}
		
	}
	
	/**
	 * @Title: parse
	 * @Description: 由json解析出对象
	 * @param jsonObject
	 * @return null 解析失败
	 */
	public static ZMDiary parse(JSONObject jsonObject) {
		ZMDiary diary = new ZMDiary();
		try {
			diary.create(jsonObject);
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			diary = null;
		}
		return diary;
	}
}
