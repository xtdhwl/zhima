/* 
 * @Title: FormFile.java
 * Created by liubingsr on 2012-5-16 下午6:28:07 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.zhima.base.logger.Logger;
import com.zhima.base.storage.FileHelper;

/**
 * @ClassName: FormFile
 * @Description: HTTP文件上传辅助类
 * @author liubingsr
 * @date 2012-5-16 下午6:28:07
 * 
 */
public class FormFile {
	private static final int MAX_IMAGE_WIDTH = 400;
	// 文件内容数据
	private byte[] mData;
	// 文件名
	private String mFileName;
	// form field名
	private String mFieldName;
	// MIME
	private String mContentType = "application/octet-stream";

	public FormFile(String fieldName, String fileName, byte[] data, String contentType) {
		mData = data;
		mFileName = fileName;
		mFieldName = fieldName;
		if (!TextUtils.isEmpty(contentType)) {
			mContentType = contentType;
		}
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: http post file
	 * </p>
	 * @param fileFullPath 文件路径
	 * @param fieldName form filed名称
	 */
	public FormFile(String fileFullPath, String fieldName) {
		mFileName = FileHelper.getFileName(fileFullPath);
		mFieldName = mFileName;
		mContentType = FileHelper.getMIME(mFileName);
		compressData(fileFullPath);
	}

	private void compressData(String fileFullPath) {
		mData = FileHelper.getFileBytes(fileFullPath);
		Logger.getInstance("dddd").debug("start data:" + mData.length);
		if (FileHelper.isImageFile(fileFullPath)) {
			// 如果是上传图片，图片大小压缩在这里处理
			mData = createThumbnail(mData, false);
			Logger.getInstance("dddd").debug("end data:" + mData.length);
		} else {
			// 其它文件的压缩处理在这里
			// TODO

		}
	}

	public byte[] getData() {
		return mData;
	}

	public void setData(byte[] data) {
		mData = data;
	}

	public String getFileName() {
		return mFileName;
	}

	public void setFileName(String fileName) {
		mFileName = fileName;
	}

	public String getFieldName() {
		return mFieldName;
	}

	public void setFieldName(String formName) {
		mFieldName = formName;
	}

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		mContentType = contentType;
	}

	private byte[] createThumbnail(byte[] bytes, boolean tiny) {
		//creates a thumbnail and returns the bytes

		int finalHeight = 0;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

		int width = opts.outWidth;
		int height = opts.outHeight;

		int finalWidth = 500; //default to this if there's a problem
		//Change dimensions of thumbnail

		if (tiny) {
			finalWidth = 150;
		}

		byte[] finalBytes;

		if (bytes.length > 200000) //it's a biggie! don't want out of memory crash
		{
			float finWidth = 1000;
			int sample = 0;

			float fWidth = width;
			sample = new Double(Math.ceil(fWidth / finWidth)).intValue();

			if (sample == 3) {
				sample = 4;
			} else if (sample > 4 && sample < 8) {
				sample = 8;
			}

			opts.inSampleSize = sample;
			opts.inJustDecodeBounds = false;

			float percentage = (float) finalWidth / width;
			float proportionateHeight = height * percentage;
			finalHeight = (int) Math.rint(proportionateHeight);

			bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);

			bm.recycle(); //free up memory

			finalBytes = baos.toByteArray();
		} else {
			finalBytes = bytes;
		}

		return finalBytes;

	}
}
