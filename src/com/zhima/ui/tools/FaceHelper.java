/* 
 * @Title: FaceHelper.java
 * Created by liubingsr on 2012-6-27 上午11:37:26 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.ui.tools;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.zhima.R;
import com.zhima.base.gdi.GraphicUtils;

/**
 * @ClassName: FaceHelper
 * @Description: 表情辅助类
 * @author liubingsr
 * @date 2012-6-27 上午11:37:26
 * 
 */
public class FaceHelper {
	private final static String TAG = "FaceHelper";	
	public final static String DELIMITER_BEGIN = "[";
	public final static String DELIMITER_END = "]";
	
	private static FaceHelper mInstance = null;
	private int widht = 35;
	private int height = 35;
	
	private Context mContext = null;
	ArrayList<FaceHolder> mFaceHolderList = null;
	ArrayList<FaceHolder> mFaceHolderThumbnailList = null;

	private FaceHelper(Context c) {
		mContext = c;				
		initFaceHolder();
	}

	public static FaceHelper getInstance(Context c) {
		if (mInstance == null) {
			mInstance = new FaceHelper(c);
		}
		return mInstance;
	}
	/**
	* @Title: destory
	* @Description: 释放掉bitmap占用的资源
	* void
	*/
	public void destory() {
		if (mFaceHolderList != null) {
			for(int index = 0, count = mFaceHolderList.size(); index < count; ++index) {
				mFaceHolderList.get(index).getBitmap().recycle();
			}
		}
		
		if (mFaceHolderThumbnailList != null) {
			for(int index = 0, count = mFaceHolderThumbnailList.size(); index < count; ++index) {
				mFaceHolderThumbnailList.get(index).getBitmap().recycle();
			}
		}
	}
	private void initFaceHolder() {
		mFaceHolderList = new ArrayList<FaceHolder>();
		mFaceHolderThumbnailList = new ArrayList<FaceHolder>();
		
		String[] faceSymbolArray = mContext.getResources().getStringArray(
				R.array.face_symbol);
		TypedArray faceImageIds = mContext.getResources().obtainTypedArray(R.array.face_drawables);
		if (faceImageIds != null) {
			int resId = 0;
			for (int index = 0, count = faceImageIds.length(); index < count; ++index) {
				resId = faceImageIds.getResourceId(index, -1);
				if (resId >= 0) {
					Bitmap bmp = BitmapFactory.decodeResource(
							mContext.getResources(), resId);
					//XXX bitmap大小
//					Bitmap ScaleBmp = GraphicUtils.getImageScale(bmp, 25, 25);
					FaceHolder holder = new FaceHolder(bmp, DELIMITER_BEGIN + faceSymbolArray[index] + DELIMITER_END);
					mFaceHolderList.add(holder);
					
					Bitmap ScaleBmp = GraphicUtils.getImageScale(bmp, widht, height);
					FaceHolder holderThumbnail = new FaceHolder(ScaleBmp, DELIMITER_BEGIN + faceSymbolArray[index] + DELIMITER_END);
					mFaceHolderThumbnailList.add(holderThumbnail);
				}
			}
		}
	}
	public final class FaceHolder {
		private Bitmap mBitmap;
		private String mSymbol;
		
		public FaceHolder(Bitmap bitmap, String symbol) {
			mBitmap = bitmap;
			mSymbol = symbol;
		}
		public Bitmap getBitmap() {
			return mBitmap;
		}
		public void setBitmap(Bitmap bitmap) {
			mBitmap = bitmap;
		}
		public String getSymbol() {
			return mSymbol;
		}
		public void setSymbol(String symbol) {
			mSymbol = symbol;
		}
	}
	/**
	* @Title: getFaceHolderList
	* @Description: 得到表情图片及表情符列表
	* @return
	* ArrayList<FaceHolder>
	*/
	public ArrayList<FaceHolder> getFaceHolderList() {
		return mFaceHolderList;
	}
	/**
	* @Title: getThumbnailFaceHolderList
	* @Description: 返回缩放的表情图片及表情符列表
	 */
	public ArrayList<FaceHolder> getThumbnailFaceHolderList() {
		return mFaceHolderThumbnailList;
	}
	/**
	* @Title: getSpannableString
	* @Description: 返回表情占位符替换成表情图片的SpannableString
	* @param text
	* @return
	* SpannableString
	*/
	public SpannableString getSpannableString(String text) {
		SpannableString result = new SpannableString(text);
		int faceCount = mFaceHolderList.size();
		int pos = 0, start = 0;
		for (int index = 0; index < faceCount; index++) {
			String symbol = mFaceHolderList.get(index).getSymbol();
			pos = 0;
			start = 0;
			while (pos != -1 && start + symbol.length() <= text.length()) {
				pos = text.indexOf(symbol, start);
				if (pos != -1) {
					ImageSpan span = new ImageSpan(mContext, mFaceHolderList.get(index).getBitmap());
					result.setSpan(span, pos, pos + symbol.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					start = pos + symbol.length();
				}
			}
		}
		return result;
	}
	
	public SpannableString getThumbnailSpannableString(SpannableString spann) {
		SpannableString result = spann;
		String text = spann.toString();
		int faceCount = mFaceHolderList.size();
		int pos = 0, start = 0;
		for (int index = 0; index < faceCount; index++) {
			String symbol = mFaceHolderList.get(index).getSymbol();
			pos = 0;
			start = 0;
			while (pos != -1 && start + symbol.length() <= text.length()) {
				pos = text.indexOf(symbol, start);
				if (pos != -1) {
					ImageSpan span = new ImageSpan(mContext, mFaceHolderThumbnailList.get(index).getBitmap());
					result.setSpan(span, pos, pos + symbol.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					start = pos + symbol.length();
				}
			}
		}
		return result;
	}
	public SpannableString getThumbnailSpannableString(String text) {
		return getThumbnailSpannableString(new SpannableString(text));
	}
}
