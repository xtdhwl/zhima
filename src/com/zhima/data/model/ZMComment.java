/* 
 * @Title: ZMComment.java
 * Created by liubingsr on 2012-5-17 下午7:24:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.StringHelper;

/**
* @ClassName: ZMComment
* @Description: 评论
* @author liubingsr
* @date 2012-7-25 下午4:49:32
*
*/
public class ZMComment extends ZMContent {
	private final static String TAG = "ZMComment";
	/**
	 * 被评论的目标id
	 */
	private long mTargetId = 0;
	
	public long getTargetId() {
		return mTargetId;
	}
	public void setTargetId(long targetId) {
		this.mTargetId = targetId;
	}

	public static ZMComment parse(JSONObject jsonObject) {
		ZMComment comment = new ZMComment();
		try {
			comment.setId(jsonObject.getLong("id"));
			comment.setContent(StringHelper.jsonNullToEmpty(jsonObject.getString("content")));
			comment.setImageUrl(StringHelper.jsonNullToEmpty(jsonObject.getString("imageUrl")));
			comment.setReplyCount(jsonObject.getInt("replyCount"));
			comment.setPostTime(jsonObject.getLong("updatedOn"));
		} catch (JSONException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return comment;
	}
}
