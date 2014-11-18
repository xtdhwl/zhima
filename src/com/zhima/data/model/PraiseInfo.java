/* 
 * @Title: PraiseInfo.java
 * Created by liubingsr on 2012-5-31 下午5:26:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.zhima.base.consts.ZMConsts.PraiseKind;

/**
 * @ClassName: PraiseInfo
 * @Description: 赞信息
 * @author liubingsr
 * @date 2012-7-13 下午3:54:02
 * 
 */
public final class PraiseInfo extends BaseData {
	private final static String TAG = "PraiseInfo";
	private static Gson gson = null;    
    static {  
        if (gson == null) {  
            gson = new Gson();  
        }  
    }
	// id
	private long mId;
	/**
	 * zmObjectId
	 */
	private long mZMObjectId;
	/**
	 * 赞信息
	 */
	private Map<Integer, Integer> mPraise;

	public PraiseInfo() {
		mId = 0;
		mPraise = new HashMap<Integer, Integer>();
	}

	@Override
	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	public long getZMObjectId() {
		return mZMObjectId;
	}

	public void setZMObjectId(long zmObjectId) {
		this.mZMObjectId = zmObjectId;
	}

	public void setPraise(int praiseType, int count) {
		mPraise.put(praiseType, count);
	}

	/**
	 * @Title: getPraiseCount
	 * @Description: 得到指定类型的赞数量
	 * @param praiseType
	 *            赞类型
	 * @see com.zhima.base.consts.ZMConsts.PraiseKind
	 * @return int 数量
	 */
	public int getPraiseCount(int praiseType) {
		if (mPraise.containsKey(praiseType)) {
			return mPraise.get(praiseType);
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer kind : mPraise.keySet()) {
			sb.append("kind_" + kind + ":" + mPraise.get(kind) + ",");
		}
		return sb.toString();
	}

	/**
	 * @Title: parse
	 * @Description: 解析出赞信息
	 * @param json
	 * @return PraiseInfo
	 */
	public static PraiseInfo parse(String json) {
		PraiseInfo praise = new PraiseInfo();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Integer>>(){}.getType();
		Map<String, Integer> map = gson.fromJson(json, type);
		if (map != null && !map.isEmpty()) {
			for (String key : map.keySet()) {
				praise.setPraise(PraiseKind.getPraiseType(key), map.get(key));
			}
		}
		return praise;
	}
}
