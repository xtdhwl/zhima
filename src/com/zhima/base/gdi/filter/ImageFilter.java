package com.zhima.base.gdi.filter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
/**
* @ClassName: ImageFilter
* @Description: 滤镜基类
* @author liubingsr
* @date 2012-6-25 上午10:06:26
*
*/
public abstract class ImageFilter {
	public static double LIB_PI = 3.14159265358979323846;
	
	protected Bitmap mOriginalBitmap;
	protected Bitmap mDestBitmap;

	// format of image (jpg/png)
	protected String mFormatName;
	// dimensions of image
	protected int mWidth, mHeight;
	// RGB Array Color
	protected int[] mColorArray;
	// 阻止创建
	protected ImageFilter() {		
	}
	
	protected void initFilter(Bitmap bitmap, Config config) {
		this.mOriginalBitmap = bitmap;
		mFormatName = "jpg";
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		mDestBitmap = Bitmap.createBitmap(mWidth, mHeight, config);
		updateColorArray();
	}
	protected int getRandomInt(int a, int b) {
		int min = Math.min(a, b);
		int max = Math.max(a, b);
		return min + (int) (Math.random() * (max - min + 1));
	}
	public void clearImage(int color) {
		for (int y = 0; y < mHeight; y++) {
			for (int x = 0; x < mWidth; x++) {
				setPixel(x, y, color);
			}
		}
	}
	public int getPixel(int x, int y) {
		return mColorArray[y * mWidth + x];
	}
	public void setPixel(int x, int y, int color) {
		mColorArray[y * mWidth + x] = color;
	}
	public void setPixel(int x, int y, int red, int green, int blue) {
		int rgb = (255 << 24) + (red << 16) + (green << 8) + blue;
		mColorArray[y * mWidth + x] = rgb;
	}
	private void updateColorArray() {
		mColorArray = new int[mWidth * mHeight];
		mOriginalBitmap.getPixels(mColorArray, 0, mWidth, 0, 0, mWidth, mHeight);		
	}
	protected void initColorData() {
		int r, g, b;
		for (int y = 0; y < mHeight; y++) {
			for (int x = 0; x < mWidth; x++) {
				int index = y * mWidth + x;
				r = (mColorArray[index] >> 16) & 0xff;
				g = (mColorArray[index] >> 8) & 0xff;
				b = mColorArray[index] & 0xff;
				mColorArray[index] = 0xff000000 | (b << 16) | (g << 8) | r;// android系统与window系统的rgb存储方式相反
			}
		}
	}
	protected void copyPixelsFromBuffer() { // 从缓冲区中copy数据以加快像素处理速度
		//IntBuffer vbb = IntBuffer.wrap(mColorArray);
		//mDestBitmap.copyPixelsFromBuffer(vbb);
		//pixels, 0, width, 0, 0, width, height
		mDestBitmap.setPixels(mColorArray, 0, mWidth, 0, 0, mWidth, mHeight);
		//vbb.clear();
	}
	public int getRedComponent(int x, int y) {
		return (mColorArray[y * mWidth + x] & 0x00FF0000) >>> 16;
	}
	public int getGreenComponent(int x, int y) {
		return (mColorArray[y * mWidth + x] & 0x0000FF00) >>> 8;
	}
	public int getBlueComponent(int x, int y) {
		return (mColorArray[y * mWidth + x] & 0x000000FF);
	}
	public void rotate(int rotateDegrees) {
		Matrix mtx = new Matrix();
		mtx.postRotate(rotateDegrees);
		mOriginalBitmap = Bitmap.createBitmap(mOriginalBitmap, 0, 0, mWidth, mHeight, mtx, true);
		mWidth = mOriginalBitmap.getWidth();
		mHeight = mOriginalBitmap.getHeight();
		updateColorArray();
	}
	public int getWidth() {
		return mWidth;
	}
	public void setWidth(int width) {
		this.mWidth = width; 
	}
	public int getHeight() {
		return mHeight;
	}	
	public void setHeight(int height) {
		this.mHeight = height;
	}
	public int[] getColorArray() {
		return mColorArray;
	}
	public void setColorArray(int[] colorArray) {
		this.mColorArray = colorArray;
	}
	public String getFormatName() {
		return mFormatName;
	}
	public void setFormatName(String formatName) {
		this.mFormatName = formatName;
	}
	public Bitmap getImage() {
		return mDestBitmap;
	}
	public void setImage(Bitmap bitmap) {
		this.mOriginalBitmap = bitmap;
	}
	public static int safeColor(int color) {
		if (color < 0) {
			return 0;
		} else if (color > 255) {
			return 255;
		} else {
			return color;
		}
	}
	public static class Function {
		// -------------------------------------------------------------------------------------
		// basic function
		// -------------------------------------------------------------------------------------
		// bound in [tLow, tHigh]
		public static int FClamp(final int t, final int tLow, final int tHigh) {
			if (t < tHigh) {
				return ((t > tLow) ? t : tLow);
			}
			return tHigh;
		}

		public static double FClampDouble(final double t, final double tLow,
				final double tHigh) {
			if (t < tHigh) {
				return ((t > tLow) ? t : tLow);
			}
			return tHigh;
		}

		public static int FClamp0255(final double d) {
			return (int) (FClampDouble(d, 0.0, 255.0) + 0.5);
		}
	}
	public abstract void process();
	public void release() {
//		if (mDestBitmap.isRecycled()) {
//			mDestBitmap.recycle();
//			mDestBitmap = null;
//			System.gc();
//		}
		mColorArray = null;
	}
	public String getFilterName() {
		return this.getClass().getSimpleName();
	}
}
