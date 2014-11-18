/* 
 * @Title: TemporaryStorage.java
 * Created by liubingsr on 2012-6-19 下午8:28:39 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.ui.diary.controller;

import java.util.ArrayList;

import android.text.Editable;

import com.zhima.base.consts.ZMConsts.DiaryPrivacyStatus;

/**
 * @ClassName: DiaryTemporaryStorage
 * @Description: 用于保存页面跳转间的临时数据(清楚数据发生在：发送日志成功)
 * @author liubingsr
 * @date 2012-6-19 下午8:28:39
 */
public class DiaryTemporaryStorage {
	private final static String TAG = "TemporaryStorage";
	private static DiaryTemporaryStorage mInstance = null;
	private ArrayList<String> mImageList = null;
	/**
	 * 日志编辑内容缓存.
	 */
	private Editable mText;
	/**
	 * 日志是否公开
	 */
	private int mDiaryPrivacy = DiaryPrivacyStatus.PUBLIC;
	/**
	 * 头像
	 * */
	private String mPhotoPath;


	private DiaryTemporaryStorage() {
		mImageList = new ArrayList<String>();
		mPhotoPath = null;
	}

	public static DiaryTemporaryStorage getInstance() {
		if (mInstance == null) {
			mInstance = new DiaryTemporaryStorage();
		}
		return mInstance;
	}

	/**
	 * @Title: addImage
	 * @Description: 加入一项
	 * @param String
	 * @return void
	 */
	public void addImage(String String) {
		if (!mImageList.contains(String)) {
			mImageList.add(String);
		}
	}

	/**
	 * @Title: contains
	 * @Description: 是否以包含
	 * @param str
	 * @return boolean
	 */
	public boolean contains(String str) {
		return mImageList.contains(str);
	}

	/**
	 * @Title: remove
	 * @Description: 移走指定项
	 * @param str
	 * @return void
	 */
	public boolean remove(String str) {
		return mImageList.remove(str);
		/*
		 * for (int index = mImageList.size() - 1; index >= 0; --index) { if
		 * (mImageList.get(index) == str) { mImageList.remove(index); break; } }
		 */
	}

	public ArrayList<String> getImageList() {
		return mImageList;
	}

	public void setText(Editable str) {
		mText = str;
	}

	public Editable getText() {
		return mText;
	}

	public void setDiaryPrivacy(int status) {
		mDiaryPrivacy = status;
	}


	/**
	 * 获取日志是否公开
	 * 
	 * @Title: getDiaryPrivacy
	 * @Description: TODO
	 * @return 1 为私密 (PRIVATE)2为公开 (PUBLIC) int
	 */
	public int getDiaryPrivacy() {
		return mDiaryPrivacy;
	}

	/**
	 * @Title: clear
	 * @Description: 清除存储的数据
	 * @return void
	 */
	public void clear() {
		mPhotoPath = null;
		mImageList.clear();
		mText = null;
	}

	/**
	 * @Title: getPhotoPath
	 * @Description: 获得头像
	 * @return String
	 */
	public String getPhotoPath() {
		return mPhotoPath;
	}

	/**
	 * @Title: setPhotoPath
	 * @Description: 设置头像
	 * @param photoPath
	 *            void
	 */
	public void setPhotoPath(String photoPath) {
		this.mPhotoPath = photoPath;
	}

	/**
	 * 获取TemporatryStorage集合大小
	 * 
	 * @Title: size
	 * @Description: TODO
	 * @return int
	 */
	public int size() {
		return mImageList.size();
	}

	/**
	 * @Title: remove
	 * @Description: 移走指定项
	 * @param String
	 * @return void
	 */
	public void remove(int index) {
		mImageList.remove(index);
	}
}
